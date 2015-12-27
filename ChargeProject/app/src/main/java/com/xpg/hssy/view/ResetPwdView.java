package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.xpg.hssychargingpole.R;

public class ResetPwdView extends LinearLayout {

	private Context context;
	private EditText resetPwd_et_confirmNo;
	private EditText resetPwd_et_confirmPwd;

	public ResetPwdView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.resetpwd_layout, null);
		addView(view);
		resetPwd_et_confirmNo = (EditText) findViewById(R.id.resetPwd_et_confirmNo);
		resetPwd_et_confirmPwd = (EditText) findViewById(R.id.resetPwd_et_confirmPwd);
	}

	private void initLinstener() {
		// TODO Auto-generated method stub

	}

	public String getConfirmNo() {
		return resetPwd_et_confirmNo.getText().toString().trim();
	}

	public String getConfirmPwd() {
		return resetPwd_et_confirmPwd.getText().toString().trim();
	}

}
