package console;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import testdatagen.Bug;
import testdatagen.TestdataInReport;
import tree.object.IFunctionNode;

public class ConsoleOutput {
	private IFunctionNode functionNode;

	// Real coverage
	private float branchCoverge;
	private float statementCoverge;

	private int numTestpath;

	private long runningTime;

	// Number of using solver
	private int numOfSolverCalls = 0;

	// Number of symbolic execution
	private int numOfSymbolicExecutions = 0;

	// Number of symbolic statements. In each symbolic execution, some of
	// statements will be symbolic.
	private int numOfSymbolicStatements = 0;

	// Number of execution
	private int numOfExecutions = 0;

	// Number of solver calls but its solution does not increase coverage
	private int numOfNoChangeToCoverage = 0;

	// Number of solver calls but can not solve (cause errors)
	private int numOfSolverCallsbutCannotSolve = 0;

	// The total time of running make command (ms)
	private long makeCommandRunningTime = 0;

	// The number of make file call
	private int makeCommandRunningNumber = 0;

	// The total time of execution
	private long executionTime = 0;

	// The total time of waiting solver to get solution (ms)
	private long solverRunningTime = 0;

	// The total time of normalizing function(ms)
	private long normalizationTime = 0;

	// The total time of symbolic execution (ms)
	private long symbolicExecutionTime = 0;

	// The total time of macro normalization (ms)
	public long macroNormalizationTime = 0;

	public Set<Bug> bugs = new HashSet<>();

	public List<TestdataInReport> testdata = new ArrayList<>();

	public String log = "";

	public float getBranchCoverge() {
		return branchCoverge;
	}

	public void setBranchCoverge(float branchCoverage) {
		this.branchCoverge = branchCoverage;
	}

	public int getNumTestpath() {
		return numTestpath;
	}

	public void setNumTestpath(int numTestpath) {
		this.numTestpath = numTestpath;
	}

	public IFunctionNode getFunctionNode() {
		return functionNode;
	}

	public void setFunctionNode(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	public long getRunningTime() {
		return runningTime;
	}

	public void setRunningTime(long runningTime) {
		this.runningTime = runningTime;
	}

	public int getNumOfSolverCalls() {
		return numOfSolverCalls;
	}

	public void setNumOfSolverCalls(int numOfSolverCalls) {
		this.numOfSolverCalls = numOfSolverCalls;
	}

	public int getNumOfSymbolicExecutions() {
		return numOfSymbolicExecutions;
	}

	public void setNumOfSymbolicExecutions(int numOfSymbolicExecutions) {
		this.numOfSymbolicExecutions = numOfSymbolicExecutions;
	}

	public int getNumOfSymbolicStatements() {
		return numOfSymbolicStatements;
	}

	public void setNumOfSymbolicStatements(int numOfSymbolicStatements) {
		this.numOfSymbolicStatements = numOfSymbolicStatements;
	}

	public int getNumOfExecutions() {
		return numOfExecutions;
	}

	public void setNumOfExecutions(int numOfExecutions) {
		this.numOfExecutions = numOfExecutions;
	}

	public int getNumOfNoChangeToCoverage() {
		return numOfNoChangeToCoverage;
	}

	public void setNumOfNoChangeToCoverage(int numOfNoChangeToCoverage) {
		this.numOfNoChangeToCoverage = numOfNoChangeToCoverage;
	}

	public int getNumOfSolverCallsbutCannotSolve() {
		return numOfSolverCallsbutCannotSolve;
	}

	public void setNumOfSolverCallsbutCannotSolve(int numOfSolverCallsbutCannotSolve) {
		this.numOfSolverCallsbutCannotSolve = numOfSolverCallsbutCannotSolve;
	}

	public long getSolverRunningTime() {
		return solverRunningTime;
	}

	public void setSolverRunningTime(long solverRunningTime) {
		this.solverRunningTime = solverRunningTime;
	}

	public long getMakeCommandRunningTime() {
		return makeCommandRunningTime;
	}

	public void setMakeCommandRunningTime(long makeCommandRunningTime) {
		this.makeCommandRunningTime = makeCommandRunningTime;
	}

	public void setMakeCommandRunningNumber(int makeCommandRunningNumber) {
		this.makeCommandRunningNumber = makeCommandRunningNumber;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public long getNormalizationTime() {
		return normalizationTime;
	}

	public void setNormalizationTime(long normalizationTime) {
		this.normalizationTime = normalizationTime;
	}

	public long getSymbolicExecutionTime() {
		return symbolicExecutionTime;
	}

	public void setSymbolicExecutionTime(long symbolicExecutionTime) {
		this.symbolicExecutionTime = symbolicExecutionTime;
	}

	public int getMakeCommandRunningNumber() {
		return makeCommandRunningNumber;
	}

	public long getMacroNormalizationTime() {
		return macroNormalizationTime;
	}

	public void setMacroNormalizationTime(long macroNormalizationTime) {
		this.macroNormalizationTime = macroNormalizationTime;
	}

	public Set<Bug> getBugs() {
		return bugs;
	}

	public void setBugs(Set<Bug> bugs) {
		this.bugs = bugs;
	}

	@Deprecated
	public String getLog() {
		return log;
	}

	@Deprecated
	public void setLog(String log) {
		this.log = log;
	}

	public List<TestdataInReport> getTestdata() {
		return testdata;
	}

	public void setTestdata(List<TestdataInReport> testdata) {
		this.testdata = testdata;
	}

	public float getStatementCoverge() {
		return statementCoverge;
	}

	public void setStatementCoverge(float statementCoverge) {
		this.statementCoverge = statementCoverge;
	}
}
