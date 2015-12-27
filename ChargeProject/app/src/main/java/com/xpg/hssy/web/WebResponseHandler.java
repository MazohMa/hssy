package com.xpg.hssy.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class WebResponseHandler<T> {
	public String tag = "WebResponseHandler";

	protected final static int MSG_START = 1;
	protected final static int MSG_SUCCESS = 2;
	protected final static int MSG_FAILURE = 3;
	protected final static int MSG_ERROR = 4;
	protected final static int MSG_FINISH = 5;

	private boolean abandon;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		WebResponse<T> response = null;

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (abandon) {
				return;
			}
			switch (msg.what) {
			case MSG_START:
				onStart();
				break;
			case MSG_SUCCESS:
				onSuccess((WebResponse<T>) msg.obj);
				break;
			case MSG_FAILURE:
				response = (WebResponse<T>) msg.obj;
				onFailure(response);
				break;
			case MSG_ERROR:
				onError((Throwable) msg.obj);
				break;
			case MSG_FINISH:
				onFinish();
				break;

			default:
				break;
			}
		};
	};

	public WebResponseHandler(Context context) {
		tag = context.getClass().getSimpleName();
	}

	public WebResponseHandler(String tag) {
		this.tag = tag;
	}

	public WebResponseHandler() {
	}

	void sendStart() {
		handler.sendEmptyMessage(MSG_START);
	}

	public void onStart() {
		Log.v(tag, "onStart");
	}

	void sendError(Throwable e) {
		Message msg = Message.obtain();
		msg.what = MSG_ERROR;
		msg.obj = e;
		handler.sendMessage(msg);
	}

	public void onError(Throwable e) {
		Log.v(tag, "onError:" + e.getMessage() + "");
	}

	void sendFailure(WebResponse<T> response) {
		Message msg = Message.obtain();
		msg.what = MSG_FAILURE;
		msg.obj = response;
		handler.sendMessage(msg);
	}

	public void onFailure(WebResponse<T> response) {
		Log.v(tag, "onFailure:" + response + "");
	}

	void sendSuccess(WebResponse<T> response) {
		Message msg = Message.obtain();
		msg.what = MSG_SUCCESS;
		msg.obj = response;
		handler.sendMessage(msg);
	}

	public void onSuccess(WebResponse<T> response) {
		Log.v(tag, "onSuccess:" + response + "");

	}

	void sendFinish() {
		handler.sendEmptyMessage(MSG_FINISH);
	}

	public void onFinish() {
		Log.v(tag, "onFinish");
	}

	public void abandon() {
		this.abandon = true;
	}

	public boolean isAbandon() {
		return abandon;
	}
}
