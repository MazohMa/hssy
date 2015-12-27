package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xpg.hssychargingpole.R;

public class ForgetPwdView extends LinearLayout {

	private Context context;
	private EditText forgetPwd_et_phone;
	private TextView forgetPwd_tv_tips;
	private ImageView forgetPwd_img_phone;

	public ForgetPwdView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.forgetpwd_phone_layout, null);
		addView(view);
		forgetPwd_et_phone = (EditText) view
				.findViewById(R.id.forgetPwd_et_phone);
		forgetPwd_tv_tips = (TextView) view
				.findViewById(R.id.forgetPwd_tv_tips);
		forgetPwd_img_phone = (ImageView) view
				.findViewById(R.id.forgetPwd_img_phone);
	}

	private void initLinstener() {

	}

	public void setTips(String str, boolean isNext) {
		forgetPwd_et_phone.setText("");
		if (isNext) {
			forgetPwd_img_phone.setVisibility(View.GONE);
			forgetPwd_et_phone.setHint(context.getString(R.string.input_code));
		} else {
			forgetPwd_img_phone.setVisibility(View.VISIBLE);
			forgetPwd_et_phone.setHint(context.getString(R.string.input_phone));
		}
		forgetPwd_tv_tips.setText(str);
	}

	public String getPhone() {
		return forgetPwd_et_phone.getText().toString();
	}
}
