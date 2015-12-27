package com.xpg.hssy.bt;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月5日
 * @version 1.0.0
 */

public interface BTConnectListener {
	void onConnecting();

	void onConnected();

	void onConnectFailed();

	void onConnectTimeOut();

	void onDisconnected();
}
