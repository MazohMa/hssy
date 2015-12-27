package com.xpg.hssy.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.view.ConfrimNoPwdView;
import com.xpg.hssychargingpole.R;

public class ConfirmNoPwdActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout content_layout;
	private ImageButton btn_left, btn_right;
	private TextView tv_title;
	private ConfrimNoPwdView confrimNoPwdView;
	private LinearLayout.LayoutParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.confirmnopwd,
				null);
		setContentView(view);
		hideSoftInput(view);

		// instance = this;
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		tv_title = (TextView) findViewById(R.id.tv_center);
		tv_title.setText("重置密码");
		content_layout = (LinearLayout) findViewById(R.id.confirmNoPwd_content_layout);
		if (confrimNoPwdView == null) {
			confrimNoPwdView = new ConfrimNoPwdView(this);
		}
		Intent _intent = this.getIntent();
		String confirmNo = _intent.getStringExtra("confirmNo");
		String newPwd = _intent.getStringExtra("newPwd");
		ToastUtil.show(this, confirmNo + ":" + newPwd);
		confrimNoPwdView.setConfirmNo(confirmNo);
		confrimNoPwdView.setConfirmPwd(newPwd);
		params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		content_layout.removeAllViews();
		content_layout.addView(confrimNoPwdView, params);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			// ToastUtil.show(this, "上一步");
			Intent forgetPwdIntent = new Intent(ConfirmNoPwdActivity.this,
					ResetPwdActivity.class);
			startActivity(forgetPwdIntent);
			finish();
			break;
		case R.id.btn_right:
			// String confirmNo = resetPwdView.getConfirmNo() ;
			// String newPwd = resetPwdView.getConfirmPwd() ;

			ToastUtil.show(this, "提交完成！！！");
			Intent confirmIntent = new Intent(ConfirmNoPwdActivity.this,
					MainActivity.class);
			startActivity(confirmIntent);
			finish();
			break;

		}
	}

}
