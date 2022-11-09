package config;

import java.util.ArrayList;
import java.util.List;

/**
 * Keys in setting
 *
 * @author ducanhnguyen
 */
public interface ISettingv2 {
	String VERSION_NAME = "VERSION";
	String VERSION = "31/05/2017";

	String COMMENT_PREFIX = ";";
	// GUI configuration
	String CFG_OVERVIEW_LEVEL = "CFG_OVERVIEW_LEVEL";
	String CFG_MARGIN_Y = "CFG_MARGIN_Y";
	String CFG_MARGIN_X = "CFG_MARGIN_X";
	String MAX_CFG_NODE_WIDTH = "MAX_CFG_NODE_WIDTH";
	String MAX_CFG_NODE_LINE = "MAX_CFG_NODE_LINE";
	String MAX_GUI_TAB = "MAX_GUI_TAB";
	String OPEN_FN_CFG_SOURCE = "OPEN_FN_CFG_SOURCE";
	String LOOK_AND_FEEL = "LOOK_AND_FEEL";

	// Variables configuration
	String DEFAULT_CHARACTER_LOWER_BOUND = "DEFAULT_CHARACTER_LOWER_BOUND";
	String DEFAULT_CHARACTER_UPPER_BOUND = "DEFAULT_CHARACTER_UPPER_BOUND";
	String DEFAULT_NUMBER_LOWER_BOUND = "DEFAULT_NUMBER_LOWER_BOUND";
	String DEFAULT_NUMBER_UPPER_BOUND = "DEFAULT_NUMBER_UPPER_BOUND";

	String DEFAULT_TEST_ARRAY_SIZE = "TEST_ARRAY_SIZE";
	String MAX_ITERATION_FOR_EACH_LOOP = "MAX_ITERATION_FOR_EACH_LOOP";

	// do not change the order of options here
	String[] SUPPORT_COVERAGE_CRITERIA = new String[] { "branch", "statement", "sub-condition" };
	String COVERAGE = "COVERAGE";

	// do not change the order of options here
	String[] SUPPORT_SOLVING_STRATEGIES = new String[] { "Z3 strategy", "User-bound strategy" };
	String SELECTED_SOLVING_STRATEGY = "DEFAULT_SOLVING_STRATEGY";

	// Compiler
	String GNU_GENERAL = "GNU_GENERAL";

	String GNU_MAKE_NAME = "GNU_MAKE_NAME";
	String GNU_MAKE_PATH = "GNU_MAKE_PATH";

	String GNU_GPlusPlus_NAME = "GNU_GPlusPlus_NAME";
	String GNU_GPlusPlus_PATH = "GNU_GPlusPlus_PATH";

	String GNU_GCC_NAME = "GNU_GCC_NAME";
	String GNU_GCC_PATH = "GNU_GCC_PATH";

	String MSBUILD_NAME = "MSBUILD_NAME"; // for project written in Visual
	// studio
	String MSBUILD_PATH = "MSBUILD_PATH"; // for project written in Visual
	// studio

	String ECLIPSE_NAME = "ECLIPSE_NAME";// for project written in Eclipse
	String ECLIPSE_PATH = "ECLIPSE_PATH";// for project written in Eclipse

	// Z3 solver
	String SOLVER_Z3_NAME = "Z3_SOLVER_NAME";
	String SOLVER_Z3_PATH = "Z3_SOLVER_PATH";
	String SMT_LIB_FILE_PATH = "SMT_LIB_FILE_PATH";

	// Currently testing project
	String INPUT_PROJECT_PATH = "INPUT_PROJECT_PATH";

	// History
	String LIST_PROJECT_OPENED = "listProjectOpened";
	String NUMBER_OF_PROJECT = "numberOfProject";
	List<String> RECENT_PROJECTS = new ArrayList<>();

	// Google test
	String ORIGINAL_GOOGLE_TEST = "ORIGINAL_GOOGLE_TEST";

	// mcpp
	String MCPP_EXE_PATH = "MCPP_EXE_PATH";
	String MCPP_NAME = "MCPP_NAME";

	// If this flag turns on, we dont perform macro normalization
	String IN_TEST_MODE = "IN_TEST_MODE";

	// others
	final int PROJECT_UNKNOWN_TYPE = -1;
	final int PROJECT_DEV_CPP = 0;
	final int PROJECT_CODEBLOCK = 1;
	final int PROJECT_VISUALSTUDIO = 2;
	final int PROJECT_ECLIPSE = 3;
	// Represent project has a makefile named "Makefile"
	final int PROJECT_CUSTOMMAKEFILE = 4;

	String TESTDATA_STRATEGY = "TESTDATA_STRATEGY";
	String PATH_SELECTION_STRATEGY = "PATH_SELECTION_STRATEGY";
	String TEST_REPORT = "TEST_REPORT";
}