package testdatagen;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ibm.icu.util.Calendar;

import cfg.ICFG;
import cfg.testpath.ITestpathInCFG;
import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import exception.GUINotifyException;
import parser.projectparser.ProjectLoader;
import testdata.object.TestpathString_Marker;
import testdatagen.fastcompilation.FastFunctionExecution;
import testdatagen.structuregen.ChangedToken;
import testdatagen.structuregen.ChangedTokens;
import tree.object.IFunctionNode;
import tree.object.INode;
import tree.object.IProjectNode;
import utils.Utils;
import utils.search.ExeNodeCondition;
import utils.search.Search;

public abstract class AbstractTestdataGeneration implements ITestdataGeneration {
	final static Logger logger = Logger.getLogger(AbstractTestdataGeneration.class);
	// Number of using solver
	public static int numOfSolverCalls = 0;
	// Number of symbolic execution
	public static int numOfSymbolicExecutions = 0;
	// Number of symbolic statements. In each symbolic execution, some of
	// statements will be symbolic.
	public static int numOfSymbolicStatements = 0;
	// Number of execution
	public static int numOfExecutions = 0;
	// Number of solver calls but its solution does not increase coverage
	public static int numOfNotChangeToCoverage = 0;
	// Number of solver calls but can not solve (cause errors)
	public static int numOfSolverCallsbutCannotSolve = 0;
	// The total time of running make command (ms)
	public static long makeCommandRunningTime = 0;
	// The number of running make command
	public static int makeCommandRunningNumber = 0;
	// The total time of execution time (ms)
	public static long executionTime = 0;
	// The total time of waiting solver to get solution (ms)
	public static long solverRunningTime = 0;
	// The total time of generating test data (ms)
	public static long totalRunningTime;
	// The total time of normalizing function (ms)
	public static long normalizationTime;
	// The total time of symbolic execution (ms)
	public static long symbolicExecutionTime;
	// The total time of macro normalization (ms)
	public static long macroNormalizationTime;
	// Bugs detected while executing the program
	public static Set<Bug> bugs = new HashSet<>();
	// Testdata
	public static List<TestdataInReport> testdata = new ArrayList<>();
	// Code coverage
	public static float branchCoverage = 0.0f;
	public static float statementCoverage = 0.0f;

	public static boolean isTerminateDuetoTooLong = false;

	// DOES NOT RESET THESE FOLLOWING FIELDS (FOR SUMMARY PURPOSE)
	public static int numOfBranches = 0;// OVERALL SCOPE
	public static int numOfVisitedBranches = 0;// OVERALL SCOPE
	public static int totalSolverCalls = 0;// OVERALL SCOPE
	public static int totalNumOfExecution = 0;// OVERALL SCOPE
	public static int totalSymbolicStatements = 0;// OVERALL SCOPE
	// item = {iteration, visited branches}
	public static List<Integer[]> visitedBranchesInfor = new ArrayList<>();// OVERALL
																			// SCOPE
	public static int tmp_iterations = 0;// OVERALL SCOPE
	public static int currentNumOfVisitedBranches = 0;// OVERALL SCOPE

	public static int removedConstraints = 0; // OVERALL SCOPE
	public static int removedTestdata = 0;// OVERALL SCOPE

	protected IFunctionNode fn = null;
	protected int numPossibleTestpath = -1;

	@Override
	public void generateTestdata() throws Exception {
		numOfSolverCalls = 0;
		numOfSymbolicExecutions = 0;
		numOfSymbolicStatements = 0;
		numOfExecutions = 0;
		numOfSolverCallsbutCannotSolve = 0;
		makeCommandRunningTime = 0;
		makeCommandRunningNumber = 0;
		solverRunningTime = 0;
		totalRunningTime = 0;
		executionTime = 0;
		normalizationTime = 0;
		symbolicExecutionTime = 0;
		macroNormalizationTime = 0;
		bugs = new HashSet<>();
		testdata = new ArrayList<>();
		branchCoverage = 0f;
		statementCoverage = 0f;
		isTerminateDuetoTooLong = false;

		Date startTime = Calendar.getInstance().getTime();

		this.generateTestdata(fn);

		Date end = Calendar.getInstance().getTime();
		totalRunningTime = end.getTime() - startTime.getTime();
	}

	public AbstractTestdataGeneration(IFunctionNode fn) {
		this.fn = fn;

	}

	protected abstract void generateTestdata(IFunctionNode originalFunction) throws Exception;

	/**
	 * Execute a function under testing
	 *
	 * @param staticSolution
	 *            test data
	 * @param changedTokens
	 * @param normalizedCFG
	 * @param originalFunction
	 * @return
	 * @throws Exception
	 */
	protected ITestdataExecution executeFunction(String staticSolution, ChangedTokens changedTokens, ICFG normalizedCFG,
			IFunctionNode originalFunction) throws Exception {
		ITestdataExecution testdataExecution = executeTestdata(originalFunction, staticSolution);

		// If the function is executed successfully under test data
		if (testdataExecution != null) {
			changedTokens.addAll(testdataExecution.getChangedTokens());
			TestpathString_Marker executionTestpath = testdataExecution.getEncodedTestpath();

			normalizedCFG.updateVisitedNodes_Marker(executionTestpath);

		}
		return testdataExecution;
	}

	/**
	 * Restore a test path to its original test path.
	 *
	 * @param testpath
	 * @param tokens
	 * @return
	 */
	protected String restoreTestpath(ArrayList<String> testpath, ChangedTokens tokens) {
		String originalTestpath = "";
		for (String testpathItem : testpath) {
			for (ChangedToken item : tokens)
				testpathItem = testpathItem.replaceAll(Utils.toRegex(item.getNewName()), item.getOldName());
			originalTestpath += testpathItem + ITestpathInCFG.SEPARATE_BETWEEN_NODES;
		}
		return originalTestpath.substring(0,
				originalTestpath.length() - new String(ITestpathInCFG.SEPARATE_BETWEEN_NODES).length());
	}

	/**
	 * Restore a test data to its original test data.
	 *
	 * @param testdata
	 * @param changedTokens
	 * @return
	 */
	protected String restoreTestdata(String testdata, ChangedTokens changedTokens) {
		String originalTestdata = testdata;
		for (ChangedToken item : changedTokens)
			originalTestdata = originalTestdata.replaceAll(Utils.toRegex(item.getNewName()), item.getOldName());
		return originalTestdata;
	}

	/**
	 * Execute function under a test data
	 *
	 * @param fn
	 * @param staticSolution
	 * @return
	 * @throws Exception
	 */
	protected ITestdataExecution executeTestdata(IFunctionNode fn, String staticSolution) throws Exception {
		ITestdataExecution dynamic = null;
		int MAX_NUMBER_OF_EXECUTION = 4;
		int MIN_NUMBER_OF_EXECUTION = 1;
		do {
			switch (AbstractSetting.getValue(ISettingv2.TESTDATA_STRATEGY)) {
			case ITestdataGeneration.TESTDATA_GENERATION_STRATEGIES.MARS2 + "":{
				dynamic = new FunctionExecution();
				((FunctionExecution) dynamic).setTestedFunction(fn);
				((FunctionExecution) dynamic).setPreparedInput(staticSolution);
				break;
			}
			case ITestdataGeneration.TESTDATA_GENERATION_STRATEGIES.FAST_MARS + "":
				dynamic = new FastFunctionExecution(fn, staticSolution);
				break;

			default:
				throw new Exception("Wrong test data generation strategy");
			}

			MIN_NUMBER_OF_EXECUTION++;
		} while (dynamic != null && dynamic.getEncodedTestpath().getEncodedTestpath().length() == 0
				&& MIN_NUMBER_OF_EXECUTION <= MAX_NUMBER_OF_EXECUTION);

		if (MIN_NUMBER_OF_EXECUTION > MAX_NUMBER_OF_EXECUTION)
			return null;
		else
			return dynamic;
	}

	/**
	 * Find the path of .exe
	 * 
	 * @param rootProject
	 *            The root of the testing project
	 * @throws Exception
	 */
	protected void getExePath(INode rootProject) throws Exception {
		// Create the absolute path of exe
		if (Utils.isWindows()) {

			if (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT == ISettingv2.PROJECT_DEV_CPP) {

				String nameExeFile = Utils.getNameOfExeInDevCppMakefile(Paths.CURRENT_PROJECT.MAKEFILE_PATH);

				if (!nameExeFile.equals("")) {
					String pathFolderEXE = Paths.CURRENT_PROJECT.MAKEFILE_PATH.substring(0,
							Paths.CURRENT_PROJECT.MAKEFILE_PATH.lastIndexOf(File.separator));

					Paths.CURRENT_PROJECT.EXE_PATH = pathFolderEXE + File.separator + nameExeFile;
				} else
					throw new GUINotifyException(
							"Compile project don't generate file \"*.exe\"!!!\nTool don't support for this project!!!");

			} else if (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT == ISettingv2.PROJECT_VISUALSTUDIO) {

				String nameProject = rootProject.getNewType() + ".exe";
				IProjectNode root = new ProjectLoader().load(new File(Utils.getRoot(rootProject).getAbsolutePath()));
				List<INode> nodeExe = Search.searchNodes(root, new ExeNodeCondition(), nameProject);

				if (nodeExe != null)
					Paths.CURRENT_PROJECT.EXE_PATH = nodeExe.get(0).getAbsolutePath();
				else
					throw new GUINotifyException(
							"Compile project don't generate file \"*.exe\"!!!\nTool don't support for this project!!!");
			} else if (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT == ISettingv2.PROJECT_ECLIPSE) {
				Paths.CURRENT_PROJECT.EXE_PATH = Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH + File.separator
						+ "Release" + File.separator + new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getName()
						+ ".exe";
				logger.debug("Exe name: " + Paths.CURRENT_PROJECT.EXE_PATH);
			}
		} else if (Utils.isUnix()) {
			switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {

			case ISettingv2.PROJECT_ECLIPSE: {
				Paths.CURRENT_PROJECT.EXE_PATH = Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH + File.separator
						+ "Release" + File.separator + new File(Paths.CURRENT_PROJECT.ORIGINAL_PROJECT_PATH).getName();

				if (!new File(Paths.CURRENT_PROJECT.EXE_PATH).exists())
					Paths.CURRENT_PROJECT.EXE_PATH += ".exe";
				logger.debug("Exe name: " + Paths.CURRENT_PROJECT.EXE_PATH);
				break;
			}

			default: {
				String nameExeFile = Utils.getNameOfExeInDevCppMakefile(Paths.CURRENT_PROJECT.MAKEFILE_PATH);

				String pathFolderEXE = Paths.CURRENT_PROJECT.MAKEFILE_PATH.substring(0,
						Paths.CURRENT_PROJECT.MAKEFILE_PATH.lastIndexOf(File.separator));

				Paths.CURRENT_PROJECT.EXE_PATH = pathFolderEXE + File.separator + nameExeFile;
				break;
			}
			}
		}
	}

}
