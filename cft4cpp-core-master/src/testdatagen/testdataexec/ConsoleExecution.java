package testdatagen.testdataexec;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ibm.icu.util.Calendar;

import config.AbstractSetting;
import config.ISettingv2;
import config.Paths;
import testdatagen.AbstractTestdataGeneration;
import utils.Utils;
import utils.UtilsVu;

/**
 * Execute in console
 *
 * @author DucAnh
 */
public class ConsoleExecution {
	final static Logger logger = Logger.getLogger(ConsoleExecution.class);

	/**
	 * Compile make file
	 *
	 * @param makefilePath
	 * @throws Exception
	 */
	public static void compileMakefile(File makefilePath) throws Exception {
		logger.info("Compile " + makefilePath);
		Date startTime = Calendar.getInstance().getTime();
		try {
			if (Utils.isWindows()) {

				switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
				case ISettingv2.PROJECT_VISUALSTUDIO: {
					String target = AbstractSetting.getValue(ISettingv2.MSBUILD_PATH);
					String nameMakefile = makefilePath.getName();
					UtilsVu.runMsbuildByCommand(target, null, makefilePath.getParentFile(), "/m", "/nodeReuse:False",
							nameMakefile);
					break;
				}

				case ISettingv2.PROJECT_DEV_CPP: {
					
					String target = "D:/program files/Dev-Cpp/MinGW64/bin/mingw32-make.exe";
					//String target = AbstractSetting.getValue(ISettingv2.GNU_MAKE_PATH);
					String folder = "D:/cft4cpp-core/data-test/tsdv/Sample_for_R1";
					//String folder = Utils.normalizePath(makefilePath.getParent());
					UtilsVu.runCommand(target, null, new File(folder), "-f", makefilePath.getName());
					break;
				}
				case ISettingv2.PROJECT_ECLIPSE: {
					String command = Utils
							.normalizePath(Utils.readFileContent(new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH)));
					String environment = new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).getParent();
					logger.debug("make command: " + command);
					logger.debug("environment: " + environment);
					Process p = Runtime.getRuntime().exec(command, null, new File(environment));
					p.waitFor(100, TimeUnit.SECONDS);

					// Display errors/warnings if exists
					BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String err;
					while ((err = error.readLine()) != null)
						logger.debug(err);
					break;
				}
				}
			} else if (Utils.isUnix()) {
				switch (Paths.CURRENT_PROJECT.TYPE_OF_PROJECT) {
				case ISettingv2.PROJECT_ECLIPSE: {
					String command = "./" + new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).getName();

					String environment = new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).getParent();
					logger.debug("make command: " + command);
					logger.debug("environment: " + environment);

					Process p = Runtime.getRuntime().exec(command, null, new File(environment));
					p.waitFor();

					// Display errors/warnings if exists
					BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String err;
					while ((err = error.readLine()) != null)
						logger.debug(err);
					break;
				}
				case ISettingv2.PROJECT_CUSTOMMAKEFILE: {
					String command = "make all";
					String environment = new File(Paths.CURRENT_PROJECT.MAKEFILE_PATH).getParent();
					logger.debug("make command: " + command);
					logger.debug("environment: " + environment);

					Process p = Runtime.getRuntime().exec(command, null, new File(environment));
					p.waitFor();

					// Display errors/warnings if exists
					BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String err;
					while ((err = error.readLine()) != null)
						logger.debug(err);
					break;
				}
				}

			} else {
				throw new Exception("Dont support to compile" + makefilePath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Date end = Calendar.getInstance().getTime();
			AbstractTestdataGeneration.makeCommandRunningTime += end.getTime() - startTime.getTime();
			AbstractTestdataGeneration.makeCommandRunningNumber++;
		}
	}

	/**
	 * Execute .exe
	 *
	 * @param exePath
	 */
	public static boolean executeExe(File exePath) throws Exception {
		boolean isTerminated = false;
		String command = "";
		if (Utils.isWindows())
			command = "\"" + exePath.getCanonicalPath() + "\"";

		else if (Utils.isUnix()) {
			command = exePath.getCanonicalPath();
		}

		logger.info("Executing " + command);

		Date startTime = Calendar.getInstance().getTime();
		Process p = Runtime.getRuntime().exec(command);
		p.waitFor(30, TimeUnit.SECONDS);

		if (p.isAlive()) {
			p.destroy(); // tell the process to stop
			p.waitFor(10, TimeUnit.SECONDS); // give it a chance to stop
			p.destroyForcibly(); // tell the OS to kill the process
			p.waitFor();
			isTerminated = true;
		}
		Date end = Calendar.getInstance().getTime();
		AbstractTestdataGeneration.executionTime += end.getTime() - startTime.getTime();
		return isTerminated;
	}

	/**
	 * Kill a process
	 *
	 * @param processName
	 * @throws Exception
	 */
	public static void killProcess(String processName) throws Exception {
		try {
			Runtime.getRuntime().exec("taskkill /F /IM " + processName);
		} catch (Exception e) {
			throw new Exception("Cannot kill process " + processName);
		}
	}
}
