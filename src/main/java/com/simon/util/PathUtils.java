package com.simon.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Georges Alfaro S.
 * @version 1.0
 */
public class PathUtils {

	public static String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public static String getHomeDirectory() {
		return System.getProperty("user.home");
	}

	public static String getLocalPath(String newFileName) {
		return FileSystems.getDefault().getPath(getWorkingDirectory(), newFileName).toString();
	}

	public static String getUserPath(String newFileName) {
		return FileSystems.getDefault().getPath(getHomeDirectory(), newFileName).toString();
	}

	public static String getFileNameWithoutExtension(String fileName) {
		String r = null;
		try {
			r = fileName.replaceFirst("[.][^.]+$", "");
		} catch (Exception ex) {
			System.err.printf("Exception: '%s'%n", ex.getMessage());
		}
		return r;
	}

	public static String getFileNameExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	public static String appendToFilePath(String filePath, String suffix) {
		Path path = Paths.get(filePath);
		return String.format("%s%s.%s",
				PathUtils.getFileNameWithoutExtension(filePath), suffix,
				PathUtils.getFileNameExtension(filePath));
	}

	public static void main(String[] args) {
		System.out.printf("Working directory: \t'%s'%n", getWorkingDirectory());
		System.out.printf("Home directory: \t'%s'%n", getHomeDirectory());
		System.out.println();

		String fileName = "dummy.txt";

		System.out.printf("\t-> \t'%s'%n", fileName);
		System.out.println();

		System.out.printf("\t-> \t'%s'%n", getLocalPath(fileName));
		System.out.printf("\t-> \t'%s'%n", getUserPath(fileName));

		System.out.printf("\t-> \t'%s'%n", appendToFilePath(getLocalPath(fileName), " (suffix)"));
		System.out.printf("\t-> \t'%s'%n", appendToFilePath(getUserPath(fileName), " (suffix)"));
		System.out.println();
	}

}

