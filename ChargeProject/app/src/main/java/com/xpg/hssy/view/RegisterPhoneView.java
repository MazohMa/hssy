package com.xpg.hssy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xpg.hssychargingpole.R;

public class RegisterPhoneView extends LinearLayout {

	private Context context;
	private EditText et_phone;
	private TextView tv_tips;
	private ImageView img_phone;
	private boolean isCodeView;

	public RegisterPhoneView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.register_phone_layout, null);
		addView(view);
		et_phone = (EditText) view.findViewById(R.id.et_phone);
		tv_tips = (TextView) view.findViewById(R.id.tv_tips);
		img_phone = (ImageView) view.findViewById(R.id.img_phone);
	}

	public void setTips(String str, boolean isNext) {
		et_phone.setText("");
		isCodeView = isNext;
		if (isNext) {// 验证码
			img_phone.setVisibility(View.GONE);
			et_phone.setHint(context.getString(R.string.input_code));

		} else {// 手机号码
			img_phone.setVisibility(View.VISIBLE);
			et_phone.setHint(context.getString(R.string.input_phone));
		}

		tv_tips.setText(str);
	}

	public String getPhone() {
		return et_phone.getText().toString();
	}

	public String getCode() {
		if (isCodeView) {
			String code = et_phone.getText().toString();
			if (!TextUtils.isEmpty(code)) {
				return code;
			}
		}
		return null;
	}
}
