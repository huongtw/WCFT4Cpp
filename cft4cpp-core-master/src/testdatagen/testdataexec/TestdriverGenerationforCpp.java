package testdatagen.testdataexec;

import config.ISettingv2;
import config.Paths;
import tree.object.INode;
import utils.Utils;

/**
 * Generate test driver for function put in an .cpp file
 *
 * @author ducanhnguyen
 */
public class TestdriverGenerationforCpp extends TestdriverGeneration {

	@Override
	public void generate() throws Exception {
		String oldContent = Utils.readFileContent(Utils.getSourcecodeFile(
				functionNode).getAbsolutePath());

		String include = generateIncludes();

		String instrumentedFunction = generateInstrumentedFunction();
		instrumentedFunction = removeStaticInFunctionDefinition(instrumentedFunction);
		/**
		 * is this function in unname(annonymous) namespace ?
		 */
		if (getFunction().isChildrenOfUnnameNamespace()) {
			instrumentedFunction = "namespace {" + instrumentedFunction + "}";
		}
		String main = generateMain(initialization, functionNode);

		String testDriver = "writeContentToFile(\""
				+ Utils.getSourcecodeFile(functionNode).getParent()
						.getAbsolutePath().replace("\\", "/") + "/"
				+ Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_NAME
				+ "\", build);";

		String append = include + "\r" + ITestdriverGeneration.CPP.WRITTER
				+ "\r" + "\r" + ITestdriverGeneration.CPP.MARK_BEGIN
				+ testDriver + ITestdriverGeneration.CPP.MARK_END + "\r" + "\r"
				+ instrumentedFunction + "\r" + main;

		this.testDriver = oldContent + append;
	}

	private String generateIncludes() {
		String include = "";
		switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
		case ISettingv2.PROJECT_DEV_CPP:
		case ISettingv2.PROJECT_ECLIPSE: {
			final String[] INCLUDES_FOR_CPP_FILE = new String[] {
					"#include <string>", "#include <iostream>",
					"#include <fstream>", "#include <stdlib.h>",
					"using namespace std;" };
			for (String item : INCLUDES_FOR_CPP_FILE)
				include += item + "\r";
			break;
		}
		case ISettingv2.PROJECT_VISUALSTUDIO:
		case ISettingv2.PROJECT_CODEBLOCK:
		case ISettingv2.PROJECT_UNKNOWN_TYPE:
		case ISettingv2.PROJECT_CUSTOMMAKEFILE:
			break;
		}

		return include;
	}

	private String generateMain(String values, INode testedFunction) {
		return "int main(){" + ITestdriverGeneration.CPP.START_CATCH + values
				+ functionCall + ITestdriverGeneration.CPP.END_CATCH
				+ "return 0;}";
	}
}
