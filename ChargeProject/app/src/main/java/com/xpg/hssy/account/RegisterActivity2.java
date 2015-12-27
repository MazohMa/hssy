package com.xpg.hssy.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.LoginInfo;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.LoginDialog;
import com.xpg.hssy.util.TextViewDrawableClickUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh 用户注册--info
 */
public class RegisterActivity2 extends BaseActivity implements OnClickListener {

	private EditText et_phone, et_valid;
	private Button get_valid_num, next;
	private TimeCount time;
	private String phone;
	private String verify;
	private ImageButton btn_left, btn_right;
	private LoadingDialog loadingDialog = null ;
	private LoginInfo loginInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		loginInfo = (LoginInfo) getIntent().getSerializableExtra("logininfo");
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.register_step_one_layout, null);
		setContentView(view);
		hideSoftInput(view);
		if(loginInfo != null){
			setTitle("绑定手机号");
		}else {
			setTitle(R.string.user_register);
			findViewById(R.id.ll_bind_function).setVisibility(View.GONE);
			((TextView)findViewById(R.id.tv_tips1)).setText("");
		}
		time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
		et_phone = (EditText) findViewById(R.id.et_phone);
		String phone = getIntent().getStringExtra(LoginDialog.KEY_PHONE);
		if(phone != null){
			et_phone.setText(phone);
		}
		et_valid = (EditText) findViewById(R.id.et_valid);
		get_valid_num = (Button) findViewById(R.id.get_valid_num);
		next = (Button) findViewById(R.id.next);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		get_valid_num.setText("获取验证码");
		get_valid_num.setClickable(true);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		get_valid_num.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		next.setOnClickListener(this);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_phone);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_valid);
	}

	public void cleanData() {
		if (et_phone != null) {
			et_phone.setText("");
		}
		if (et_valid != null) {
			et_valid.setText("");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		cleanData() ;
		et_phone.clearFocus();
		et_valid.clearFocus();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(!TextUtils.isEmpty(et_valid.getText())){
			et_valid.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.get_valid_num:
				phone = et_phone.getText().toString();
				if (TextUtils.isEmpty(phone)
						||phone.length() != 11
						||!phone.subSequence(0, 1).equals("1")) {
					ToastUtil.show(this, "请输入正确的手机号");
					return;
				} else if (phone != null && phone.length() == 11) {
					getVerifyCode();
				}
				break;
			case R.id.btn_left:
				finish();
				break;
			case R.id.next:
				phone = et_phone.getText().toString();
				verify = et_valid.getText().toString();

				if (phone == null || "".equals(phone)
						|| !phone.subSequence(0, 1).equals("1")) {
					ToastUtil.show(this, "手机号不能为空");
					return;
				} else if (phone.length() != 11) {
					ToastUtil.show(this, "请输入11位手机号");
					return;
				} else if (verify == null || "".equals(verify)) {
					ToastUtil.show(this, "验证码不能为空");
					return;
				} else {
					WebAPIManager.getInstance().checkVerifyCode(phone, verify, new
							WebResponseHandler<Object>() {
								@Override
								public void onStart() {
									super.onStart();
									loadingDialog = new LoadingDialog(RegisterActivity2.this,R.string.loading) ;
									loadingDialog.showDialog();
								}

								@Override
								public void onError(Throwable e) {
									super.onError(e);
									TipsUtil.showTips(self, e);
									loadingDialog.dismiss();
								}

								@Override
								public void onFailure(WebResponse<Object> response) {
									super.onFailure(response);
									loadingDialog.dismiss();
									TipsUtil.showTips(RegisterActivity2.this,response);
									// 弹出提醒对话框
//									if(response.getCode().equals(WebResponse.CODE_VALIDATION_CODE_ERROR)) {
//										ToastUtil.show(self, "请输入正确的验证码");
//									}else if(response.getCode().equals(WebResponse.CODE_VALIDATION_CODE_EXPIRED)){
//										ToastUtil.show(self, "验证码已过期，请重新获取");
//									}
								}

								@Override
								public void onSuccess(WebResponse<Object> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();
									if(time != null ){
										time.onFinish();
									}
									Intent intent = new Intent(self, RegisterInfoActivity.class);
									intent.putExtra("phone", phone);
									intent.putExtra("verify", verify);
									if(loginInfo != null){
										intent.putExtra("logininfo", loginInfo);
									}
									startActivity(intent);
//							finish();
								}
							});
				}
			default:
				break;
		}
	}

	private void getVerifyCode() {
		loadingDialog = new LoadingDialog(RegisterActivity2.this,R.string.loading_verify_code) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().getRegisterVerifyCode(phone, new WebResponseHandler<Object>
				(RegisterActivity2.this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
						TipsUtil.showTips(self,response);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(self, "验证码已发送");
				time.start();// 开始计时
			}

			@Override
			public void onFinish() {
				super.onFinish();
				try {
					loadingDialog.dismiss();
				} catch (Exception e) {
				}
			}

		});
	}

	/**
	 * @author Mazoh 计时器
	 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			get_valid_num.setText("获取验证码");
			get_valid_num.setEnabled(true);
			et_valid.setText("");
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			get_valid_num.setEnabled(false);
			get_valid_num.setText(millisUntilFinished / 1000 + "s");
		}
	}
}
