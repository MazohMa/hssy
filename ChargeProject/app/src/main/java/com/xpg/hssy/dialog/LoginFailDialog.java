package com.xpg.hssy.dialog;

import com.xpg.hssychargingpole.R;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class LoginFailDialog extends Dialog {

	public LoginFailDialog(Context context) {
		super(context, R.style.dialog_no_frame);
		setContentView(R.layout.login_fail_tips2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}
}
