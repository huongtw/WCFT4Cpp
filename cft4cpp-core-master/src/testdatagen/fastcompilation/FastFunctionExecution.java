package testdatagen.fastcompilation;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import config.Settingv2;
import exception.GUINotifyException;
import parser.projectparser.ProjectParser;
import testdatagen.Backup;
import testdatagen.FunctionExecution;
import testdatagen.testdataexec.ConsoleExecution;
import testdatagen.testdataexec.ITestdriverGeneration;
import testdatagen.testdataexec.TestdriverGenerationforC;
import tree.object.CFileNode;
import tree.object.CppFileNode;
import tree.object.FunctionNode;
import tree.object.IFunctionNode;
import utils.Utils;
import utils.search.FunctionNodeCondition;
import utils.search.Search;

/**
 * Enhance the old function execution by compiling the testing project once
 * time.
 * 
 * @author DucAnh
 */
public class FastFunctionExecution extends FunctionExecution {
	final static Logger logger = Logger.getLogger(FastFunctionExecution.class);

	public static void main(String[] args) throws Exception {
		Settingv2.create();
		AbstractSetting.setValue(ISettingv2.SOLVER_Z3_PATH, "C:/z3/bin/z3.exe");
		AbstractSetting.setValue(ISettingv2.GNU_MAKE_PATH, "C:/Dev-Cpp/MinGW64/bin/mingw32-make.exe");
		AbstractSetting.setValue(ISettingv2.GNU_GCC_PATH, "C:/Dev-Cpp/MinGW64/bin/gcc.exe");
		AbstractSetting.setValue(ISettingv2.GNU_GPlusPlus_PATH, "C:/Dev-Cpp/MinGW64/bin/g++.exe");

		ProjectParser parser = new ProjectParser(new File(Paths.TSDV_R1));
		FunctionNode testedFunction = (FunctionNode) Search
				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "IntTest(int)").get(0);

		String preparedInput = "";
		new FastFunctionExecution(testedFunction, preparedInput);
	}

	public FastFunctionExecution(IFunctionNode fn, String staticSolution) throws Exception {
		if (isInitializedCompilerEnvironment()) {
			Backup backup = saveCurrentState(fn);
			try {
				Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_PATH = generateExecutionFile(fn);
				Paths.CURRENT_PROJECT.TESTDATA_INPUT_FILE_PATH = (new File(
						Utils.getSourcecodeFile(fn).getAbsolutePath()).getParent() + File.separator
						+ Paths.CURRENT_PROJECT.TESTDATA_INPUT_FILE_NAME).replace("\\", "/");

				// Normalize function before executing it
				// logger.debug("Normalize function before executing it");
				IFunctionNode clone = (IFunctionNode) fn.clone();
				clone.setAST(clone.getGeneralNormalizationFunction().getNormalizedAST());
				changedTokens = clone.getGeneralNormalizationFunction().getTokens();
				updateProject(backup, fn);

				ITestdriverGeneration testdriverGen = generateTestdriver(clone, staticSolution, backup);
				if (testdriverGen != null) {

					if (Utils.getSourcecodeFile(fn) instanceof CFileNode) {
						// Up to now, I have not had any idea for generating better test driver than the
						// older version.
						if (new File(Paths.CURRENT_PROJECT.EXE_PATH).exists())
							killExeProcess(Paths.CURRENT_PROJECT.EXE_PATH);
						logger.debug(Paths.CURRENT_PROJECT.EXE_PATH + " does not exist");
						ConsoleExecution.compileMakefile(new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH));

					} else if (Utils.getSourcecodeFile(fn) instanceof CppFileNode) {
						// Improvement here: The test driver is created for all set of values. We only
						// need to create it
						// once!
						logger.debug(
								Paths.CURRENT_PROJECT.EXE_PATH + " does not exist, so we have to compile project!");
						if (!new File(Paths.CURRENT_PROJECT.EXE_PATH).exists()) {
							ConsoleExecution.compileMakefile(new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH));
						} else
							logger.info("Does not run make command");
					}

					executeExecutableFile(Utils.findRootProject(fn),
							Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_PATH);
				}
			} catch (GUINotifyException e) {
				e.printStackTrace();
				throw new GUINotifyException(e.getMessage());

			} catch (Exception e) {
				e.printStackTrace();
				initialization = "";

			} finally {
				backup.restore();

				// Delete exe program if it exists before
				if (Utils.getSourcecodeFile(fn) instanceof CFileNode
						&& new File(Paths.CURRENT_PROJECT.EXE_PATH).exists())
					ConsoleExecution.killProcess(new File(Paths.CURRENT_PROJECT.EXE_PATH).getName());

				if (encodedTestpath.getEncodedTestpath().length() == 0)
					initialization = "";
			}
		}
	}

	private ITestdriverGeneration generateTestdriver(IFunctionNode clone, String staticSolution, Backup backup)
			throws Exception {
		// Generate test driver
		logger.info("Generate a test driver");
		dataGen = clone.generateDataTree(FunctionExecution.staticSolutionsGen(staticSolution));
		dataGen.setFunctionNode(clone);
		dataGen.generateTree();

		String functionCall = dataGen.getFunctionCall();

		ITestdriverGeneration testdriverGen = null;
		if (Utils.getSourcecodeFile(clone) instanceof CFileNode) {
			initialization = dataGen.getInputforGoogleTest();
			testdriverGen = new TestdriverGenerationforC();

		} else if (Utils.getSourcecodeFile(clone) instanceof CppFileNode) {
			// Improvement here
			initialization = dataGen.getInputformFile();
			logger.debug(new File(Paths.CURRENT_PROJECT.TESTDATA_INPUT_FILE_PATH).getName() + "="
					+ dataGen.getInputSavedInFile().replace("\n", "; "));
			Utils.writeContentToFile(dataGen.getInputSavedInFile(), Paths.CURRENT_PROJECT.TESTDATA_INPUT_FILE_PATH);
			testdriverGen = new FastTestdriverGenerationforCpp();
		} else
			throw new Exception("Dont support this type of file source code");

		logger.debug("driver=" + initialization.replace("\n", "").replace("\t", "") + functionCall.replace("\n", "")
				+ "...");

		if (testdriverGen != null) {
			testdriverGen.setTestedFunction(clone);
			testdriverGen.setInitialization(initialization);
			testdriverGen.setFunctionCall(functionCall);
			testdriverGen.generate();

			Utils.writeContentToFile(testdriverGen.getCompleteSourceFile(), backup.getFnParent());
		}
		return testdriverGen;
	}

	/**
	 * Create for storing execution test path
	 * 
	 * @param fn
	 * @return
	 * @throws IOException
	 */
	private String generateExecutionFile(IFunctionNode fn) throws IOException {
		String executionFilePath = "";
		Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_NAME = FunctionExecution.id
				+ Paths.CURRENT_PROJECT.TESTDRIVER_EXECUTION_NAME_POSTFIX;

		switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {

		case ISettingv2.PROJECT_ECLIPSE: {
			executionFilePath = new File(Utils.getSourcecodeFile(fn).getAbsolutePath()).getParent() + File.separator
					+ Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_NAME;
			break;
		}
		case ISettingv2.PROJECT_DEV_CPP:
		case ISettingv2.PROJECT_CODEBLOCK:
		case ISettingv2.PROJECT_CUSTOMMAKEFILE:
		case ISettingv2.PROJECT_VISUALSTUDIO:
			executionFilePath = new File(Utils.getSourcecodeFile(fn).getAbsolutePath()).getParent() + File.separator
					+ Paths.CURRENT_PROJECT.CURRENT_TESTDRIVER_EXECUTION_NAME;
			break;
		}
		executionFilePath = executionFilePath.replace("\\", "/");
		Utils.writeContentToFile("", executionFilePath);
		return executionFilePath;
	}
}
