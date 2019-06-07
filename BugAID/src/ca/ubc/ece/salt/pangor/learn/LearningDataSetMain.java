package ca.ubc.ece.salt.pangor.learn;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import weka.core.WekaException;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.Commit.Type;
import ca.ubc.ece.salt.pangor.api.KeywordUse;
import ca.ubc.ece.salt.pangor.api.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.pangor.learn.analysis.LearningDataSet;
import ca.ubc.ece.salt.pangor.learn.analysis.LearningMetrics;
import ca.ubc.ece.salt.pangor.learn.analysis.LearningMetrics.KeywordFrequency;

/**
 * Creates clusters similar repairs.
 *
 * 1. Reads the results of the data mining task ({@code LearningAnalysisMain})
 * 2. Builds a CSV file of WEKA-supported feature vectors
 * 3. Clusters the feature vectors
 */
public class LearningDataSetMain {

	protected static final Logger logger = LogManager.getLogger(LearningDataSetMain.class);

	/**
	 * Creates the learning data sets for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LearningDataSetOptions options = new LearningDataSetOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LearningDataSetMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			LearningDataSetMain.printHelp(parser);
			return;
		}

		/* Get the clusters for the data set. */

		/* The clusters stored according to their keyword. */
		Set<ClusterMetrics> keywordClusters = new TreeSet<ClusterMetrics>(new Comparator<ClusterMetrics>() {
			@Override
			public int compare(ClusterMetrics c1, ClusterMetrics c2) {
				if(c1.totalInstances == c2.totalInstances) return c1.toString().compareTo(c2.toString());
				else if(c1.totalInstances < c2.totalInstances) return 1;
				else return -1;
			}
		});

		/* Re-construct the data set. */
		LearningDataSet clusteringDataSet =
				LearningDataSet.createLearningDataSet(
						options.getDataSetPath(),
						options.getOraclePath(),
						new LinkedList<KeywordUse>(), // column filters
						options.getEpsilon(),
						options.getComplexityWeight(),
						options.getMinClusterSize());

		/* Store the total instances in the dataset before filtering. */
		ClusterMetrics clusterMetrics = new ClusterMetrics();

		/* Pre-process the file. */
		clusteringDataSet.preProcess(getBasicRowFilterQuery(options.getMaxChangeComplexity()));
		clusterMetrics.setTotalInstances(clusteringDataSet.getSize());
		clusteringDataSet.preProcess(getStatementRowFilterQuery(options.getMaxChangeComplexity()));

		/* Print the metrics from the processed data set. */
		LearningMetrics metrics = clusteringDataSet.getMetrics();
		for(KeywordFrequency frequency : metrics.changedKeywordFrequency) {
			System.out.println(frequency.keyword + " : " + frequency.frequency);
		}

		/* Get the clusters. */
		try {
			clusteringDataSet.getWekaClusters(clusterMetrics);
		} catch (WekaException ex) {
			logger.error("Weka error on building clusters.", ex);
			return;
		}

		/* Save arff file */
		if (options.getArffFolder() != null)
			clusteringDataSet.writeArffFile(options.getArffFolder(), "ALL_KEYWORDS.arff");

		/* We only have one ClusterMetrics now. */
		keywordClusters.add(clusterMetrics);

		/* Write the evaluation results from the clustering. */
//		EvaluationResult result = clusteringDataSet.evaluate(clusterMetrics);
//		System.out.println(result.getConfusionMatrix());

		System.out.println(clusterMetrics.getRankedClusterTable());
		//System.out.println(ClusterMetrics.getLatexTable(keywordClusters));

	}

	/**
	 * Prints the help file for main.
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
        System.out.print("Usage: DataSetMain ");
        parser.printSingleLineUsage(System.out);
        System.out.println("\n");
        parser.printUsage(System.out);
        System.out.println("");
        return;
	}

	/**
	 * Prints the usage of main.
	 * @param error The error message that triggered the usage message.
	 * @param parser The args4j parser.
	 */
	private static void printUsage(String error, CmdLineParser parser) {
        System.out.println(error);
        System.out.print("Usage: DataSetMain ");
        parser.printSingleLineUsage(System.out);
        System.out.println("");
        return;
	}

	/**
	 * Selects feature vectors with:
	 *  - Complexity <= {@code complexity}
	 *  - Commit message != MERGE
	 * @param maxComplexity The maximum complexity for the feature vector.
	 * @return The Datalog query that selects which rows to data mine.
	 */
	public static IQuery getBasicRowFilterQuery(Integer maxComplexity) {

		IVariable complexity = Factory.TERM.createVariable("Complexity");

		IQuery query =
			Factory.BASIC.createQuery(
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("FeatureVector", 8),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createVariable("URL"),
						Factory.TERM.createVariable("BuggyCommitID"),
						Factory.TERM.createVariable("RepairedCommitID"),
						Factory.TERM.createVariable("Class"),
						Factory.TERM.createVariable("Method"),
						complexity)),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createLessEqual(
						complexity,
						Factory.CONCRETE.createInt(maxComplexity))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createGreater(
						complexity,
						Factory.CONCRETE.createInt(0))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createString(Type.MERGE.toString()))));
//				Factory.BASIC.createLiteral(true,
//					Factory.BUILTIN.createEqual(
//						Factory.TERM.createVariable("CommitMessage"),
//						Factory.TERM.createString(Type.BUG_FIX.toString())))

		return query;

	}

	/**
	 * Selects feature vectors with:
	 *  - At least one keyword with context != STATEMENT
	 * @param maxComplexity The maximum complexity for the feature vector.
	 * @return The Datalog query that selects which rows to data mine.
	 */
	public static IQuery getStatementRowFilterQuery(Integer maxComplexity) {

		IVariable complexity = Factory.TERM.createVariable("Complexity");

		IQuery query =
			Factory.BASIC.createQuery(
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("FeatureVector", 8),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createVariable("URL"),
						Factory.TERM.createVariable("BuggyCommitID"),
						Factory.TERM.createVariable("RepairedCommitID"),
						Factory.TERM.createVariable("Class"),
						Factory.TERM.createVariable("Method"),
						complexity)),
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("KeywordChange", 7),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("KeywordType"),
						Factory.TERM.createVariable("KeywordContext"),
						Factory.TERM.createVariable("ChangeType"),
						Factory.TERM.createVariable("Package"),
						Factory.TERM.createVariable("Keyword"),
						Factory.TERM.createVariable("Count"))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("KeywordContext"),
						Factory.TERM.createString(KeywordContext.STATEMENT.toString()))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("ChangeType"),
						Factory.TERM.createString(ChangeType.UPDATED.toString()))));

		return query;

	}

}