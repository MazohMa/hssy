package com.xpg.hssy.account;

import android.content.Intent;
import android.os.Bundle;
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
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.SharePreUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.ForgetPwdView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class ForgetPwdActivity extends BaseActivity implements OnClickListener {

	private LinearLayout content_layout;
	private ImageButton btn_left;
	private Button btn_right;
	private TextView tv_title;
	private ForgetPwdView forgetPwdView;
	private LinearLayout.LayoutParams params;
	private String phone;
	private SharePreUtil spu;
	private String code;
    private LoadingDialog loadingDialog = null ;
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btn_left:
			finish();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;
		case R.id.btn_right:
			phone = forgetPwdView.getPhone();
			if (phone.isEmpty() || phone.length() != 11) {
				ToastUtil.show(this, "手机号格式错误");
				return;
			}

			// 系统字段判断手机是否已注册，直接获取验证码就可以了
			getVerifyCode(phone);

			break;

		default:
			break;
		}

	}


	private void getVerifyCode(String phone) {
		loadingDialog = new LoadingDialog(ForgetPwdActivity.this,R.string.loading) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().getForgetPwdVerifyCode(phone,
				new WebResponseHandler<Object>(this) {

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(self, e);
						loadingDialog.dismiss(); ;
					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						TipsUtil.showTips(self, response);
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);
						loadingDialog.dismiss();
						code = response.getCode();
						Intent resetPwdIntent = new Intent(
								ForgetPwdActivity.this, ResetPwdActivity.class);
						resetPwdIntent.putExtra("code", code);
						resetPwdIntent.putExtra("phone",
								ForgetPwdActivity.this.phone);
						startActivity(resetPwdIntent);
						overridePendingTransition(R.anim.slide_left_in,
								R.anim.slide_right_out);
						finish();
					}

				});
	}

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
		spu = SharePreUtil.getInstance(this);
		View view = LayoutInflater.from(this)
				.inflate(R.layout.forget_pwd, null);
		view.findViewById(R.id.pb_scan);
		setContentView(view);
		hideSoftInput(view);
		btn_left = (ImageButton) view.findViewById(R.id.btn_left);
		btn_right = (Button) view.findViewById(R.id.btn_right);
		btn_right.setText(R.string.next);
		btn_right.setVisibility(View.VISIBLE);
		tv_title = (TextView) view.findViewById(R.id.tv_center);
		tv_title.setText("重置密码");
		content_layout = (LinearLayout) view
				.findViewById(R.id.forgetPwd_content_layout);
		if (forgetPwdView == null) {
			forgetPwdView = new ForgetPwdView(this);
		}
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		content_layout.removeAllViews();
		content_layout.addView(forgetPwdView, params);
	}



	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
