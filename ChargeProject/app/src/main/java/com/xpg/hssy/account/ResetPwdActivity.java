package com.xpg.hssy.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.ResetPwdView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class ResetPwdActivity extends BaseActivity implements OnClickListener {

	private LinearLayout content_layout;
	private ImageButton btn_left;
	private Button btn_right;
	private TextView tv_title;
	private ResetPwdView resetPwdView;
	private LinearLayout.LayoutParams params;
	// private ProgressDialog m_pDialog;
	private SharedPreferences sp;
	private LoadingDialog loadingDialog = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.resetpwd, null);
		setContentView(view);
		hideSoftInput(view);

		btn_left = (ImageButton) findViewById(R.id.btn_left);

		btn_right = (Button) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.VISIBLE);
		tv_title = (TextView) findViewById(R.id.tv_center);
		tv_title.setText("重置密码");
		content_layout = (LinearLayout) findViewById(R.id.resetPwd_content_layout);
		if (resetPwdView == null) {

			resetPwdView = new ResetPwdView(this);
		}
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		content_layout.removeAllViews();
		content_layout.addView(resetPwdView, params);

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
			// Intent forgetPwdIntent = new Intent(ResetPwdActivity.this,
			// ForgetPwdActivity.class);
			// startActivity(forgetPwdIntent);
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;
		case R.id.btn_right:

			String code = getIntent().getStringExtra("code"); // 验证码
			String phone = getIntent().getStringExtra("phone"); // 电话
			String confirmNo = resetPwdView.getConfirmNo(); // 填写的验证码
			String newPwd = resetPwdView.getConfirmPwd();// 新密码
			if (TextUtils.isEmpty(confirmNo) || TextUtils.isEmpty(newPwd)) {
				/*
				 * 
				 * 发送短信以及设置密码业务
				 */
				ToastUtil.show(this, "验证码或新密码不能为空");
				return;
			} else if (newPwd.length() < 6) {
				ToastUtil.show(this, "密码应为6-16位数字或字母");
				return;
			}
			resetPassword(phone, confirmNo, newPwd); // 网络请求提交密码重置确认
			break;

		}
	}

	private void resetPassword(String phone, String confirmNo, String newPwd) {
		// if (m_pDialog == null) {
		// m_pDialog = new ProgressDialog(ResetPwdActivity.this);
		// m_pDialog.setMessage("提交中...");
		// m_pDialog.setCancelable(false);
		// }
		// m_pDialog.show();
		loadingDialog = new LoadingDialog(ResetPwdActivity.this,R.string.please_wait) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().resetPassword(confirmNo, 1, phone, newPwd,
				new WebResponseHandler<User>(ResetPwdActivity.this) {

					@Override
					public void onStart() {
						super.onStart();

					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(self, e);
						loadingDialog.dismiss();
					}

					@Override
					public void onFailure(WebResponse<User> response) {
						super.onFailure(response);
						TipsUtil.showTips(self, response);
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(WebResponse<User> response) {
						super.onSuccess(response);
						loadingDialog.dismiss();

						// String user_id = sp.getString("user_id", "");
						// User user =
						// DbHelper.getInstance(ResetPwdActivity.this)
						// .getUserDao().load(user_id);
						// if (user != null) {
						// String token = user.getToken();
						// WebAPIManager.getInstance().setAccessToken(token);
						// }
						sp.edit().putString("user_id", "").commit();
						// ToastUtil.show(ResetPwdActivity.this,
						// response.getResultObj().toString()) ;
						ToastUtil.show(ResetPwdActivity.this, "提交成功");
						finish();
						overridePendingTransition(R.anim.slide_right_in,
								R.anim.slide_left_out);
					}

				});
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
