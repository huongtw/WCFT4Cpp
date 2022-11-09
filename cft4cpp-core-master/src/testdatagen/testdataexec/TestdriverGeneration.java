package testdatagen.testdataexec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;

import config.IFunctionConfig;
import config.Paths;
import instrument.FunctionInstrumentationForStatementvsBranch_Marker;
import instrument.FunctionInstrumentationForSubCondition;
import instrument.IFunctionInstrumentationGeneration;
import parser.projectparser.ProjectParser;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Generate test driver for a function
 *
 * @author Vu + D.Anh
 */
public abstract class TestdriverGeneration implements ITestdriverGeneration {

	protected IFunctionNode functionNode;

	protected String initialization = "";

	protected String functionCall = "";

	protected String testDriver = "";

	public TestdriverGeneration() {
	}

	public static void main(String[] args) throws Exception {
		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
		FunctionNode testedFunction = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "IntTest(int)").get(0);

		List<String> values = new ArrayList<>();
		values.add("\tint x = 0;");

		TestdriverGeneration testdriverGen = new TestdriverGenerationforCpp();
		testdriverGen.setTestedFunction(testedFunction);
		testdriverGen.setRandomValues(values);
		testdriverGen.setFunctionCall("/*no function call*/");
		testdriverGen.generate();
	
		System.out.println("test driver=\n" + testdriverGen.getCompleteSourceFile());
		BufferedWriter writer = new BufferedWriter(new FileWriter("myTestDriver.txt", false));
		writer.write(testdriverGen.getCompleteSourceFile());
		writer.close();
	}

	protected String generateInstrumentedFunction() {
		String instrumentedFunction = "";
		// generate instrumented source code of the complete function
		IASTFunctionDefinition oldAST = getFunction().getAST();
		String completeFunction = getFunction().generateCompleteFunction();
		IASTFunctionDefinition newAST = Utils.getFunctionsinAST(completeFunction.toCharArray()).get(0);
		getFunction().setAST(newAST);

		IFunctionInstrumentationGeneration fnInstrumentation = null;
		int typeofCoverage = getFunction().getFunctionConfig().getTypeofCoverage();
		switch (typeofCoverage) {
		case IFunctionConfig.BRANCH_COVERAGE:
		case IFunctionConfig.STATEMENT_COVERAGE:
			fnInstrumentation = new FunctionInstrumentationForStatementvsBranch_Marker(getFunction());
			break;
		case IFunctionConfig.SUBCONDITION:
			fnInstrumentation = new FunctionInstrumentationForSubCondition(getFunction());
			break;
		}

		if (fnInstrumentation != null) {
			instrumentedFunction = fnInstrumentation.generateInstrumentedFunction();
			getFunction().setAST(oldAST);
		} else
			instrumentedFunction = "//error when instrumenting";
		return instrumentedFunction;
	}

	/**
	 * "static void test(...){...}" ------------> "void test(...){...}"
	 *
	 * @param function
	 */
	protected String removeStaticInFunctionDefinition(String function) {
		String oldDefinition = function.substring(0, function.indexOf("("));
		String newDefinition = oldDefinition.replaceAll("^static\\s*", "");
		function = function.replace(oldDefinition, newDefinition);
		return function;
	}

	@Override
	public IFunctionNode getFunction() {
		return functionNode;
	}

	public String getRandomValues() {
		return initialization;
	}

	public void setRandomValues(List<String> randomValues) {
		for (String item : randomValues)
			initialization += item;
	}

	@Override
	public String getFunctionCall() {
		return functionCall;
	}

	@Override
	public void setFunctionCall(String functionCall) {
		this.functionCall = functionCall;
	}

	@Override
	public String getCompleteSourceFile() {
		return testDriver;
	}

	public void setCompleteSourceFile(String completeSourceFile) {
		testDriver = completeSourceFile;
	}

	@Override
	public void setTestedFunction(IFunctionNode testedFunction) {
		functionNode = testedFunction;
	}

	@Override
	public void setInitialization(String randomValues) {
		initialization = randomValues;
	}
}
