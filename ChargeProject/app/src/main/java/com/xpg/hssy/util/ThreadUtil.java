package com.xpg.hssy.util;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月23日
 * @version 1.0.0
 */

public class ThreadUtil {
	public static boolean isMainThread() {
		return Thread.currentThread().getName().equals("main");
	}
}
