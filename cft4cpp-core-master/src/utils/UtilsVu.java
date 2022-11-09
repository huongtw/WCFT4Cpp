package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Utilities
 * 
 * @author ducvu
 */
public class UtilsVu {

	public static String getContentFile(File file) throws IOException {
		FileInputStream st = new FileInputStream(file);
		String content = UtilsVu.getContentStream(st);

		st.close();
		return content;
	}

	public static String getContentStream(InputStream stream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = stream.read(buffer)) != -1)
			result.write(buffer, 0, length);
		return result.toString("UTF-8");
	}

	public static String html(String body) {
		return String.format("<html><body>%s</body></html>", body);
	}

	public static String htmlCenter(String body) {
		return String.format("<html><body style='text-align:center'>%s</body></html>", body);
	}

	public static String htmlEscape(String text) {
		return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	public static String runCommand(Object target, String[] envp, File dir, Object... args)
			throws IOException, InterruptedException, ProcessErrorException, NullPointerException {

		String[] cmdArray = new String[args.length + 1];
		cmdArray[0] = target.toString();
		for (int i = 0; i < args.length; i++)
			cmdArray[i + 1] = " "+args[i].toString()+" ";

		Process p = Runtime.getRuntime().exec(cmdArray, envp, dir);
		final int MAX_SECONDS = 10;
		p.waitFor(MAX_SECONDS, TimeUnit.SECONDS);

		return UtilsVu.getContentStream(p.getInputStream());
	}

	public static void runMsbuildByCommand(Object target, String[] envp, File dir, Object... args) throws IOException {

		String[] cmdArray = new String[args.length + 1];
		cmdArray[0] = target.toString();
		for (int i = 0; i < args.length; i++)
			cmdArray[i + 1] = args[i].toString();
		Runtime.getRuntime().exec(cmdArray, envp, dir);
	}

	public static class ProcessErrorException extends Exception {

		private static final long serialVersionUID = 1L;

		private int exitCode;
		private String message;

		public ProcessErrorException(int exitCode, String message) {
			this.exitCode = exitCode;
			this.message = message;
		}

		public int getExitCode() {
			return exitCode;
		}

		@Override
		public String getMessage() {
			return message;
		}
	}
}
