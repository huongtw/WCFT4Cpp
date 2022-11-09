package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileDeleteStrategy;

import tree.object.INode;

/**
 * Utils for input/output
 * 
 * @author ducanhnguyen
 *
 */
public class IOUtils {

	public static void writeContentToFile(String content, INode n) {
		IOUtils.writeContentToFile(content, n.getAbsolutePath());
	}

	public static void writeContentToFile(String content, String filePath) {
		try {
			IOUtils.createFolder(new File(filePath).getParent());
			PrintWriter out = new PrintWriter(filePath);
			out.println(content);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static String readFileContent(File file) {
		return Utils.readFileContent(file.getAbsolutePath());
	}

	public static String readFileContent(INode n) {
		return Utils.readFileContent(n.getAbsolutePath());
	}

	public static String getFileExtension(String path) {
		String[] pathSegments = path.split(File.separator);
		String fileName = pathSegments[pathSegments.length - 1];
	
		int i = fileName.lastIndexOf('.');
		if (i == -1)
			return "";
		else
			return fileName.substring(i + 1);
	}

	/**
	 * Tao folder
	 *
	 * @param path
	 */
	public static void createFolder(String path) {
		File destDir = new File(path);
		if (!destDir.exists())
			destDir.mkdir();
	}

	public static void deleteFileOrFolder(File path) {
		try {
			FileDeleteStrategy.FORCE.delete(path);
			// FileUtils.deleteDirectory(new File(path));
			if (!path.exists())
				return;
		} catch (IOException e) {
			try {
				Thread.sleep(30);
				IOUtils.deleteFileOrFolder(path);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {
	
		if (src.isDirectory()) {
	
			// if directory not exists, create it
			if (!dest.exists())
				dest.mkdir();
	
			// list all the directory contents
			String files[] = src.list();
	
			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				IOUtils.copyFolder(srcFile, destFile);
			}
	
		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
	
			byte[] buffer = new byte[1024];
	
			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0)
				out.write(buffer, 0, length);
	
			in.close();
			out.close();
		}
	}

	/**
	 * Bá»• sung thÃªm ná»™i dung vÃ o tá»‡p
	 *
	 * @param path
	 * @param content
	 */
	public static void appendContentToFile(String content, String path) {
		File f = new File(path);
		if (!f.exists())
			writeContentToFile(content, path);
		else {
			String currentContent = Utils.readFileContent(path);
			writeContentToFile(currentContent + content, path);
		}
	}

}
