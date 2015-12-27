package com.xpg.hssy.bt;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2014年11月4日
 * @version 1.0.0
 */

public class BTHandler {
	public String tag = "OnBTHandler";

	protected final static int MSG_SUCCESS = 2;
	protected final static int MSG_ERROR = 4;
	protected final static int MSG_TIME_OUT = 5;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:
				onSuccess((byte[]) msg.obj);
				break;
			case MSG_ERROR:
				onError((Throwable) msg.obj);
				break;
			case MSG_TIME_OUT:
				onTimeOut();
				break;
			}
		}
	};

	void sendError(Throwable e) {
		Message msg = Message.obtain();
		msg.what = MSG_ERROR;
		msg.obj = e;
		mHandler.sendMessage(msg);
	}

	public void onError(Throwable e) {
		Log.v(tag, "onError: " + e.getMessage() + "");
	}

	void sendSuccess(byte[] data) {
		Message msg = Message.obtain();
		msg.what = MSG_SUCCESS;
		msg.obj = data;
		mHandler.sendMessage(msg);
	}

	public void onSuccess(byte[] dada) {
		Log.v(tag, "onSuccess: " + dada + "");
	}

	void sendTimeOut() {
		mHandler.sendEmptyMessage(MSG_TIME_OUT);
	}

	public void onTimeOut() {
		Log.v(tag, "onTimeOut");
	}
}
