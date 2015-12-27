package com.xpg.hssy.view;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年5月13日
 * @version 1.0.0
 */

public interface TimeCircleListener {
	void onStart(int beginMinutes, int endMinutes);

	void onChanged(int beginMinutes, int endMinutes);

	void onFinish(int beginMinutes, int endMinutes);
}
