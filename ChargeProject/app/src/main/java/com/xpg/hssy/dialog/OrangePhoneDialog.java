package com.xpg.hssy.dialog;

import android.content.Context;

import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月7日
 * @version 1.0.0
 */

public class OrangePhoneDialog extends BaseDialog {
	public OrangePhoneDialog(Context context) {
		super(context);
		setContentView(R.layout.orange_phone_dialog);
	}

	public void setBtnText(String text) {
		setLeftBtnText(text);
	}

	public void setBtnText(int textId) {
		setLeftBtnText(textId);
	}
}
