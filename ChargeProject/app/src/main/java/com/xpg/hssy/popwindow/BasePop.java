package com.xpg.hssy.popwindow;

import android.app.Activity;
import android.content.Context;
import android.widget.PopupWindow;

/**
 * BasePop
 * 
 * @Description
 * @author Joke Huang
 * @createDate 2014年7月24日
 * @version 1.0.0
 */

public class BasePop extends PopupWindow {
	protected Activity mActivity;

	public BasePop() {
	}

	public BasePop(Context context) {
		mActivity = (Activity) context;
		initData();
		initUI();
		initEvent();
	}

	protected void initData() {
	}

	protected void initUI() {
	}

	protected void initEvent() {
	}
}
