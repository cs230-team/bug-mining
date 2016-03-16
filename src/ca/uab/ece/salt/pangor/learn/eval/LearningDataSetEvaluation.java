package ca.uab.ece.salt.pangor.learn.eval;

import java.util.LinkedList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import weka.core.WekaException;
import ca.ubc.ece.salt.pangor.analysis.Commit.Type;
import ca.ubc.ece.salt.pangor.api.KeywordUse;
import ca.ubc.ece.salt.pangor.api.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.pangor.learn.ClusterMetrics;
import ca.ubc.ece.salt.pangor.learn.EvaluationResult;
import ca.ubc.ece.salt.pangor.learn.analysis.LearningDataSet;

/**
 * Evaluates a list of datasets for various values of epsilon (and possibly
 * complexity weight). Outputs an R script which charts the results.
 *
 * For each data set:
 * 1. Reads the results of the data mining task ({@code LearningAnalysisMain})
 * 2. Builds a CSV file of WEKA-supported feature vectors
 * 3. Clusters the feature vectors
 */
public class LearningDataSetEvaluation {

	protected static final Logger logger = LogManager.getLogger(LearningDataSetEvaluation.class);

	/**
	 * Creates the learning data sets for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LearningDataSetEvaluationOptions options = new LearningDataSetEvaluationOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LearningDataSetEvaluation.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			LearningDataSetEvaluation.printHelp(parser);
			return;
		}

		double min = 0.1, max = 6, interval = 0.2;
		EvaluationResult[][] results = new EvaluationResult[options.getDataSetPaths().length][(int)Math.ceil((max-min)/interval)];

		/* Evaluate each data set. */
		for(int i = 0; i < options.getDataSetPaths().length; i++) {

			String dataSetPath = options.getDataSetPaths()[i];

			/* Evaluate each value of epsilon. */
			for(double epsilon = min; epsilon <= max; epsilon += interval) {

				int j = (int)(epsilon / interval);
				double complexityWeight = 0.2;

				/* Re-construct the data set. */
				LearningDataSet dataSet =
					LearningDataSet.createLearningDataSet(
						dataSetPath, options.getOraclePath(),
						new LinkedList<KeywordUse>(),
						epsilon, complexityWeight,
						options.getMinClusterSize());

				/* Store the total instances in the dataset before filtering. */
				ClusterMetrics clusterMetrics = new ClusterMetrics();

				/* Pre-process the file. */
				dataSet.preProcess(getBasicRowFilterQuery(options.getMaxChangeComplexity()));
				clusterMetrics.setTotalInstances(dataSet.getSize());
				dataSet.preProcess(getStatementRowFilterQuery(options.getMaxChangeComplexity()));

				/* Cluster the data set. */
				try {
					dataSet.getWekaClusters(clusterMetrics);
				} catch (WekaException ex) {
					logger.error("Weka error on building clusters.", ex);
				}

				/* Evaluate the clusters. */
				EvaluationResult result = dataSet.evaluate(clusterMetrics);
				results[i][j] = result;

			}

		}

		LearningDataSetEvaluation.printCSV(results, new String[]{"3", "5", "6", "7"});
		//System.out.println("-----------------");
		//RLineChart.printPRChart(results);
		//System.out.println("-----------------");
		//RLineChart.printDensityChart(results, new String[]{"3", "5", "6", "7"});

	}

	/**
	 * Print the evaluation results as a CSV file
	 * @param results The results of the evaluation.
	 */
	private static void printCSV(EvaluationResult[][] results, String[] classes) {
		System.out.println(results[0][0].getResultsArrayHeader());
		for(int i = 0; i < results.length; i++) {
			for(int j = 0; j < results[i].length; j++) {
				System.out.println(i + " ," + results[i][j].getResultsArray(classes));
			}
		}
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
						Factory.TERM.createString(KeywordContext.STATEMENT.toString()))));

		return query;

	}

}