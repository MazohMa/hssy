package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.xpg.hssychargingpole.R;

public class ConfrimNoPwdView extends LinearLayout {

	private Context context;
	private EditText confrimnopwd_et_confirmNo;
	private EditText confrimnopwd_et_confirmPwd;

	public ConfrimNoPwdView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(context).inflate(
				R.layout.confirmnopwd_layout, null);
		addView(view);
		confrimnopwd_et_confirmNo = (EditText) findViewById(R.id.confrimnopwd_et_confirmNo);
		confrimnopwd_et_confirmPwd = (EditText) findViewById(R.id.confrimnopwd_et_confirmPwd);

	}

	private void initLinstener() {
		// TODO Auto-generated method stub

	}

	public String getConfirmNo() {
		return confrimnopwd_et_confirmNo.getText().toString().trim();
	}

	public String getConfirmPwd() {
		return confrimnopwd_et_confirmPwd.getText().toString().trim();
	}

	public void setConfirmNo(String str) {
		confrimnopwd_et_confirmNo.setText(str);
	}

	public void setConfirmPwd(String str) {
		confrimnopwd_et_confirmPwd.setText(str);
	}

}
