package com.xpg.hssy.util;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by black-Gizwits on 2015/10/12.
 */
public final class FileUtils {
	private FileUtils() {
		throw new Error("don't instantiation Utils class !");
	}

	public static final long THRESHOLD_KB = 1 << 10;//1024B,1KB
	public static final long THRESHOLD_MB = 1 << 20;//1024KB,1MB
	public static final long THRESHOLD_GB = 1 << 30;//1024MB,1GB
	public static final long THRESHOLD_TB = 1 << 40;//1024GB/1TB

	private static final DecimalFormat FILE_SIZE_FORMAT = new DecimalFormat("0.##");

	/**
	 * 递归计算文件夹下的文件总大小,也可以计算单个文件的大小,如果预期目录比较大,请使用线程
	 * @param file
	 * @return
	 */
	public static long calculateFileSize(File file) {
		long totalSize = 0;
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File subFile : files) {
					totalSize += calculateFileSize(subFile);
				}
			} else {
				totalSize += file.length();
			}
		}
		return totalSize;
	}

	/**
	 * 递归计算文件夹下的文件总大小,也可以计算单个文件的大小,如果预期目录比较大,请使用线程
	 * @param filePath
	 * @return
	 */
	public static long calculateFileSize(String filePath) {
		return calculateFileSize(new File(filePath));
	}
	/**
	 * 递归删除文件夹下的所有文件,也可以删除单个文件,如果预期目录比较大,请使用线程
	 * @param file
	 * @return
	 */
	public static void deleteFile(File file) {
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File subFile : files) {
					deleteFile(subFile);
				}
			} else {
				file.delete();
			}
		}
	}
	/**
	 * 递归删除文件夹下的所有文件,也可以删除单个文件的,如果预期目录比较大,请使用线程
	 * @param filePath
	 * @return
	 */
	public static void deleteFile(String filePath) {
		deleteFile(new File(filePath));
	}

	public static String formatFileSize(long fileSize) {
		String formatString = "0KB";
		if (0 < fileSize && fileSize < THRESHOLD_KB) {
			formatString = FILE_SIZE_FORMAT.format(fileSize) + "B";
		} else if (THRESHOLD_KB < fileSize && fileSize < THRESHOLD_MB) {
			formatString = FILE_SIZE_FORMAT.format(fileSize * 1.0 / THRESHOLD_KB) + "KB";
		} else if (THRESHOLD_MB < fileSize && fileSize < THRESHOLD_GB) {
			formatString = FILE_SIZE_FORMAT.format(fileSize * 1.0 / THRESHOLD_MB) + "MB";
		} else if (THRESHOLD_GB < fileSize && fileSize < THRESHOLD_TB) {
			formatString = FILE_SIZE_FORMAT.format(fileSize * 1.0 / THRESHOLD_GB) + "GB";
		} else if (THRESHOLD_TB < fileSize) {
			formatString = FILE_SIZE_FORMAT.format(fileSize * 1.0 / THRESHOLD_TB) + "TB";
		}
		return formatString;
	}
}
