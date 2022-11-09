package testdatagen.testdataexec;

import config.ISettingv2;
import config.Paths;
import tree.object.INode;
import utils.Utils;

public class TestdriverGenerationforC extends TestdriverGeneration {

	@Override
	public void generate() throws Exception {
		String oldContent = Utils.readFileContent(Utils.getSourcecodeFile(functionNode).getAbsolutePath());

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

		String testDriver = "writeContentToFile(\"" + Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_PATH
				+ "\", build);";

		String append = include + "\r" + ITestdriverGeneration.C.WRITTER + "\r" + "\r"
				+ ITestdriverGeneration.C.MARK_BEGIN + testDriver + ITestdriverGeneration.C.MARK_END + "\r" + "\r"
				+ instrumentedFunction + "\r" + main;

		this.testDriver = oldContent + append;
	}

	private String generateMain(String randomValues, INode testedFunction) {
		return "int main(){" + randomValues + functionCall + "return 0;}";
	}

	private String generateIncludes() {
		String include = "";
		switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
		case ISettingv2.PROJECT_DEV_CPP:
		case ISettingv2.PROJECT_ECLIPSE: {
			final String[] INCLUDE_FOR_DEVC_C = new String[] { "#include <stdio.h>", "#include <memory.h>",
					"#include <stdlib.h>", "#include <string.h>" };
			for (String item : INCLUDE_FOR_DEVC_C)
				include += item + "\r";
			break;
		}
		case ISettingv2.PROJECT_VISUALSTUDIO:
			break;
		case ISettingv2.PROJECT_CODEBLOCK:
			break;
		case ISettingv2.PROJECT_UNKNOWN_TYPE:
			break;
		case ISettingv2.PROJECT_CUSTOMMAKEFILE:
			break;
		}

		return include;
	}

}
