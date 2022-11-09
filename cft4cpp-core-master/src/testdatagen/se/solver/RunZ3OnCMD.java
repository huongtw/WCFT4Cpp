package testdatagen.se.solver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ibm.icu.util.Calendar;

import testdatagen.AbstractTestdataGeneration;
import testdatagen.se.ISymbolicExecution;
import utils.Utils;

/**
 * Chạy file smt-lib trên cmd sử dụng SMT-Solver Z3
 *
 * @author anhanh
 */
public class RunZ3OnCMD {
	final static Logger logger = Logger.getLogger(RunZ3OnCMD.class);

	private String Z3Path;
	private String smtLibPath;
	private String result;

	public RunZ3OnCMD(String Z3Path, String smtLibPath) throws IOException, InterruptedException {
		this.Z3Path = Z3Path;
		this.smtLibPath = smtLibPath;
		result = "";
	}

	public static void main(String[] args) throws Exception {
		RunZ3OnCMD run = new RunZ3OnCMD("D:\\cft4cpp-core\\local\\z3\\bin\\z3", "D:\\cft4cpp-core\\myConstraint.smt2");
		run.execute();
		System.out.print(run.getSolution());
	}

	public synchronized void execute() throws Exception {
		logger.debug("RunZ3OnCMD begin");
		String content = Utils.readFileContent(new File(smtLibPath));

		if (content.contains(ISymbolicExecution.NO_SOLUTION_CONSTRAINT_SMTLIB))
			result = ISymbolicExecution.UNSAT_IN_Z3;

		else {
			String commandLine = "";

			if (Utils.isWindows()) {
				commandLine = "\"" + Z3Path + "\"" + " -smt2 " + "\"" + smtLibPath + "\"";

			} else if (Utils.isUnix()) {
				if (smtLibPath.contains(" "))
					throw new Exception("Path of smt-lib file does not contain space. " + smtLibPath);
				else
					commandLine = "z3 -smt2 " + smtLibPath;
			} else {
				throw new Exception("Your OS is not support!!");
			}

			logger.debug("z3 command: " + commandLine);

			Date startTime = Calendar.getInstance().getTime();

			Process p = Runtime.getRuntime().exec(commandLine);
			p.waitFor(10, TimeUnit.SECONDS);

			AbstractTestdataGeneration.numOfSolverCalls++;
			Date end = Calendar.getInstance().getTime();
			AbstractTestdataGeneration.solverRunningTime += end.getTime() - startTime.getTime();

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
				result += line + "\n";

			// Display errors if exists
			if (p.getErrorStream() != null) {
				BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String err;
				boolean hasError = false;
				while ((err = error.readLine()) != null) {
					logger.error(err);
					hasError = true;
				}
				if (hasError)
					AbstractTestdataGeneration.numOfSolverCallsbutCannotSolve++;
			}
		}
		logger.debug("RunZ3OnCMD end");
	}

	public String getSolution() {
		return result;
	}
}
