package testdatagen.testdataexec;

import interfaces.IGeneration;
import tree.object.IFunctionNode;

/**
 * Generate test driver
 *
 * @author Vu + D.Anh
 */
public interface ITestdriverGeneration extends IGeneration {

	void generate() throws Exception;

	interface C {

		final String START_CATCH = "";
		final String END_CATCH = "";

		final String MARK_BEGIN = "char* build=\"\";int mark(char* append){char * tmp = (char *) malloc(2 + strlen(build)+ strlen(append) );strcpy(tmp, build);strcat(tmp, append);strcat(tmp, \"\\n\");build = tmp;";
		final String MARK_END = "return 1;}";

		final String WRITTER = "void writeContentToFile(char* path, char* content) {FILE *fp;fp = fopen(path, \"w+\");fprintf(fp, content);fclose(fp);}";
	}

	interface CPP {

		final String START_CATCH = "try{";
		final String END_CATCH = " }catch(exception& error){build=\"Phat hien loi runtime\";}";

		final String MARK_BEGIN = "string build = \"\";bool mark(string append){build += append + \"\\n\";";
		final String MARK_END = "return true;}";

		final String WRITTER = "void writeContentToFile(char* path, string content){ofstream myfile;myfile.open(path);myfile << content;myfile.close();}";
	}

	IFunctionNode getFunction();

	public String getFunctionCall();

	void setFunctionCall(String functionCall);

	void setTestedFunction(IFunctionNode testedFunction);

	void setInitialization(String randomValues);

	String getCompleteSourceFile();
}
