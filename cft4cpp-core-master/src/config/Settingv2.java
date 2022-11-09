package config;

import testdatagen.ITestdataGeneration;
import utils.Utils;

/**
 * Manage settings in setting.properties
 *
 * @author ducanh
 */
public class Settingv2 extends AbstractSetting {

	public static void main(String[] args) {
		AbstractSetting.settingPath = "D:/ava/classes/artifacts/cft4cpp/test/setting.properties";
		Settingv2.create();
	}

	public static void create() {
		if (Utils.isWindows())
			initialWindowOS_forDevelopment();
		else if (Utils.isUnix())
			initialUnixOS_forTesting();
	}

	public static void initialUnixOS_forTesting() {
		getAttributes().put(VERSION_NAME, VERSION);
		getAttributes().put(INPUT_PROJECT_PATH, "");

		// Variables configuration (by default)
		getAttributes().put(COMMENT_PREFIX + "Variables configuration", "");
		getAttributes().put(SELECTED_SOLVING_STRATEGY, SUPPORT_SOLVING_STRATEGIES[0]);
		getAttributes().put(DEFAULT_CHARACTER_LOWER_BOUND, 32);
		getAttributes().put(DEFAULT_CHARACTER_UPPER_BOUND, 126);
		getAttributes().put(DEFAULT_NUMBER_LOWER_BOUND, -100);
		getAttributes().put(DEFAULT_NUMBER_UPPER_BOUND, 100);
		getAttributes().put(MAX_ITERATION_FOR_EACH_LOOP, 20);
		getAttributes().put(DEFAULT_TEST_ARRAY_SIZE, 10);

		// Compiler configuration (by default)
		getAttributes().put(COMMENT_PREFIX + "Compiler configuration", "");
		getAttributes().put(GNU_GCC_NAME, "gcc");
		getAttributes().put(GNU_GCC_PATH, "/usr/bin/gcc");
		getAttributes().put(GNU_GPlusPlus_NAME, "g++");
		getAttributes().put(GNU_GPlusPlus_PATH, "/usr/bin/g++");
		getAttributes().put(GNU_GENERAL, "/usr/bin");
		getAttributes().put(GNU_MAKE_NAME, "make");
		getAttributes().put(GNU_MAKE_PATH, "/usr/bin/make");

		getAttributes().put(ECLIPSE_NAME, "eclipse");
		getAttributes().put(ECLIPSE_PATH, "");

		// Solver configuration
		getAttributes().put(COMMENT_PREFIX + "Solver", "");
		getAttributes().put(SOLVER_Z3_NAME, "z3");
		getAttributes().put(SOLVER_Z3_PATH, "/usr/bin/z3");
		getAttributes().put(SMT_LIB_FILE_PATH, getFolderSettingPath() + "/constraints.smt2");

		// Preprocessor configuration
		getAttributes().put(COMMENT_PREFIX + "Preprocessor mcpp", "");
		getAttributes().put(MCPP_NAME, "mcpp");
		getAttributes().put(MCPP_EXE_PATH, "/usr/bin/mcpp");

		// Google test
		getAttributes().put(COMMENT_PREFIX + "Google test", "");
		getAttributes().put(ORIGINAL_GOOGLE_TEST, getFolderSettingPath() + "/GoogleTest");

		// History configuration
		getAttributes().put(COMMENT_PREFIX + "History", "");
		getAttributes().put(LIST_PROJECT_OPENED, "");
		getAttributes().put(NUMBER_OF_PROJECT, 5);

		// GUI configuration
		// getAttributes().put(COMMENT_PREFIX + "GUI", "");
		// getAttributes().put(MAX_CFG_NODE_LINE, 0);
		// getAttributes().put(MAX_GUI_TAB, 1);
		// getAttributes().put(LOOK_AND_FEEL,
		// "javax.swing.plaf.metal.MetalLookAndFeel");
		// getAttributes().put(CFG_OVERVIEW_LEVEL, 2);
		// getAttributes().put(CFG_MARGIN_Y, 50);
		// getAttributes().put(CFG_MARGIN_X, 120);
		// getAttributes().put(MAX_CFG_NODE_WIDTH, 30);

		getAttributes().put(IN_TEST_MODE, "false");

		getAttributes().put(TESTDATA_STRATEGY, ITestdataGeneration.TESTDATA_GENERATION_STRATEGIES.MARS2 + "");
		getAttributes().put(PATH_SELECTION_STRATEGY, "DART_BFS");
		getAttributes().put(TEST_REPORT, "TEST_REPORT");
		// Write all configuration to file
		save(getAttributes());
	}

	public static void initialWindowOS_forDevelopment() {
		getAttributes().put(VERSION_NAME, VERSION);
		getAttributes().put(INPUT_PROJECT_PATH, "");

		// Variables configuration (by default)
		getAttributes().put(COMMENT_PREFIX + "Variables configuration", "");
		getAttributes().put(SELECTED_SOLVING_STRATEGY, SUPPORT_SOLVING_STRATEGIES[0]);
		getAttributes().put(DEFAULT_CHARACTER_LOWER_BOUND, 32);
		getAttributes().put(DEFAULT_CHARACTER_UPPER_BOUND, 126);
		getAttributes().put(DEFAULT_NUMBER_LOWER_BOUND, -100);
		getAttributes().put(DEFAULT_NUMBER_UPPER_BOUND, 100);
		getAttributes().put(MAX_ITERATION_FOR_EACH_LOOP, 20);
		getAttributes().put(DEFAULT_TEST_ARRAY_SIZE, 10);
		getAttributes().put(COVERAGE, SUPPORT_COVERAGE_CRITERIA[0]);

		// Compiler configuration (by default)
		getAttributes().put(COMMENT_PREFIX + "Compiler configuration", "");

		getAttributes().put(GNU_GCC_NAME, "gcc.exe");
		getAttributes().put(GNU_GCC_PATH, "E:/Dev-Cpp/MinGW64/bin/gcc.exe");
		getAttributes().put(GNU_GPlusPlus_NAME, "g++.exe");
		getAttributes().put(GNU_GPlusPlus_PATH, "E:/Dev-Cpp/MinGW64/bin/g++.exe");
		getAttributes().put(GNU_GENERAL, "E:\\Dev-Cpp\\MinGW64\\bin");
		getAttributes().put(GNU_MAKE_NAME, "mingw32-make.exe");
		getAttributes().put(GNU_MAKE_PATH, "E:/Dev-Cpp/MinGW64/bin/mingw32-make.exe");

		getAttributes().put(MSBUILD_PATH, "");
		getAttributes().put(MSBUILD_NAME, "MSBuild.exe");

		getAttributes().put(ECLIPSE_NAME, "eclipsec.exe");
		getAttributes().put(ECLIPSE_PATH, "");

		// Solver configuration
		getAttributes().put(COMMENT_PREFIX + "Solver", "");
		getAttributes().put(SOLVER_Z3_NAME, "z3.exe");
		getAttributes().put(SOLVER_Z3_PATH, getFolderSettingPath() + "/z3/bin/z3.exe");
		getAttributes().put(SMT_LIB_FILE_PATH, getFolderSettingPath() + "/constraints.smt2");

		// Preprocessor configuration
		getAttributes().put(COMMENT_PREFIX + "Preprocessor mcpp", "");
		getAttributes().put(MCPP_NAME, "mcpp.exe");
		getAttributes().put(MCPP_EXE_PATH, getFolderSettingPath() + "/mcpp/bin/mcpp.exe");

		// Google test
		getAttributes().put(COMMENT_PREFIX + "Google test", "");
		getAttributes().put(ORIGINAL_GOOGLE_TEST, getFolderSettingPath() + "/GoogleTest");

		// History configuration
		getAttributes().put(COMMENT_PREFIX + "History", "");
		getAttributes().put(LIST_PROJECT_OPENED, "");
		getAttributes().put(NUMBER_OF_PROJECT, 5);

		// GUI configuration
		// getAttributes().put(COMMENT_PREFIX + "GUI", "");
		// getAttributes().put(MAX_CFG_NODE_LINE, 0);
		// getAttributes().put(MAX_GUI_TAB, 1);
		// getAttributes().put(LOOK_AND_FEEL,
		// "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		// getAttributes().put(CFG_OVERVIEW_LEVEL, 2);
		// getAttributes().put(CFG_MARGIN_Y, 50);
		// getAttributes().put(CFG_MARGIN_X, 120);
		// getAttributes().put(MAX_CFG_NODE_WIDTH, 30);

		getAttributes().put(IN_TEST_MODE, "false");

		getAttributes().put(TESTDATA_STRATEGY, ITestdataGeneration.TESTDATA_GENERATION_STRATEGIES.MARS2 + "");
		getAttributes().put(PATH_SELECTION_STRATEGY, "DART_BFS");
		getAttributes().put(TEST_REPORT, "TEST_REPORT");
		// Write all configuration to file
		save(getAttributes());
	}
}
