

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.InvalidSyntaxException;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import java.io.File;
import java.util.List;

public class change {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "C:/Users/rupa/Documents/Rupa/UCLA/Q3/CS230/Project/dataset/input/Defects4J/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		for(int i = 0; i < listOfFiles.length; i++){
			if(listOfFiles[i].isDirectory()){
				String changeFilesPath = listOfFiles[i] + "/revFiles/";
				System.out.println(changeFilesPath);
				File changeFiles = new File(changeFilesPath);
				File[] changeFilesList = changeFiles.listFiles();
				
				for(File chngFile : changeFilesList){
					System.out.println(chngFile.getName());
					File left = new File(listOfFiles[i] + "/prevFiles/prev_" +chngFile.getName());
					File right = new File(listOfFiles[i] + "/revFiles/" +chngFile.getName());
					/*FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
					try {
					    distiller.extractClassifiedSourceCodeChanges(left, right);
					} catch(Exception e) {
					    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
					       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
					       attach the full stack trace along with the two files that you tried to distill. */
					    /*System.err.println("Warning: error while change distilling. " + e.getMessage());
					}

					List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
					if(changes != null) {
					    for(SourceCodeChange change : changes) {
					        // see Javadocs for more information
					    	System.out.println(change.getLabel());
					    	System.out.println(change);
					    }
					}*/
				}
			}
		}
		/*for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
		    System.out.println("File " + listOfFiles[i].getName());
		  } else if (listOfFiles[i].isDirectory()) {
		    System.out.println("Directory " + listOfFiles[i].getName());
		  }
		}*/
		//C:\Users\rupa\Documents\Rupa\UCLA\Q3\CS230\Project\dataset\input\Defects4J\
		/*String path = "C:/Users/rupa/Documents/Rupa/UCLA/Q3/CS230/Project/dataset/input/Defects4J/closure-compiler.git/";
		File left = new File(path + "prevFiles/prev_00b151_974027_src#com#google#javascript#jscomp#FunctionTypeBuilder.java");
		File right = new File(path + "revFiles/00b151_974027_src#com#google#javascript#jscomp#FunctionTypeBuilder.java");

		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		   /* System.err.println("Warning: error while change distilling. " + e.getMessage());
		}

		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		if(changes != null) {
		    for(SourceCodeChange change : changes) {
		        // see Javadocs for more information
		    	System.out.println(change);
		    	
		    	System.out.println("Label :" + change.getLabel());
		    	System.out.println("Change Type : " + change.getChangeType());
		    	//System.out.println();
		    	//System.out.println();
		    	//System.out.println();
		    	//System.out.println();
		    	//System.out.println();
		    	
		    }
		}*/
	}

}
