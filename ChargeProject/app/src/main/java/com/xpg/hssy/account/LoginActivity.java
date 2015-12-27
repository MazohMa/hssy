package com.xpg.hssy.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.easy.manager.EasyActivityManager;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.LoginInfo;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.LoginDialog;
import com.xpg.hssy.dialog.LoginFailDialog;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;
import com.xpg.hssychargingpole.shareapi.ShareApiManager;

import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * @author Mazoh 用户登录
 */
public class LoginActivity extends BaseActivity implements OnClickListener, TextWatcher {

	private EditText et_phone, et_pwd;
	private Button btn_login;
	private Button btn_forgetPwd, btn_createAccount;// 忘记密码，创建新用户
	private SharedPreferences sp;
	private ImageButton btn_left, btn_right;
	private TextView tv_center;
	private boolean isMainActivity = false;
	private boolean isLoginOuted = false;
	private String last_user_id;
	private String lastPhone;
	private ImageView iv_qq_login;
	private ImageView iv_weixin_login;
	private LoadingDialog loadingDialog = null;
	private LoginInfo loginInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		Intent intent = getIntent();
		if (intent != null) {
			isMainActivity = intent.getBooleanExtra("isMainActivity", false);
			isLoginOuted = intent.getBooleanExtra("isLoginOuted", false);
		}
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.login_layout, null);
		setContentView(view);
		hideSoftInput(view);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_createAccount = (Button) findViewById(R.id.btn_createAccount);
		btn_forgetPwd = (Button) findViewById(R.id.btn_forgetPwd);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_center = (TextView) findViewById(R.id.tv_center);
		tv_center.setText(R.string.app_title);
		iv_qq_login = (ImageView) findViewById(R.id.iv_qq_login);
		iv_weixin_login = (ImageView) findViewById(R.id.iv_weixin_login);
		// 自动填写最后一次登录的手机号
		last_user_id = sp.getString("last_user_id", null);
		if (last_user_id != null) {
			User user = DbHelper.getInstance(self).getUserDao().load(last_user_id);
			if (user != null) {
				lastPhone = user.getPhone();
				et_phone.setText(lastPhone + "");
			}
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_login.setOnClickListener(this);
		btn_forgetPwd.setOnClickListener(this);
		btn_createAccount.setOnClickListener(this);
		iv_qq_login.setOnClickListener(this);
		iv_weixin_login.setOnClickListener(this);
		et_phone.addTextChangedListener(this);
		et_pwd.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		final String pushId = JPushInterface.getRegistrationID(self);
		final WebResponseHandler<User> responseHandler = new WebResponseHandler<User>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				doAfterSuccess(response);
				loadingDialog.setMsg(R.string.logining);
				loadingDialog.showDialog();
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);
				if (response.getCode().equals("10003")) {
					// 进行绑定操作
					if (loginInfo != null) {
						Intent data = new Intent(LoginActivity.this, RegisterActivity2.class);
						data.putExtra("logininfo", loginInfo);
						startActivity(data);
					}
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
			}
		};
		switch (v.getId()) {
			case R.id.btn_login:
				login();
				break;
			case R.id.btn_forgetPwd:
				Intent forgetPwdIntent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
				startActivity(forgetPwdIntent);
				overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				break;
			case R.id.btn_createAccount:
				Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity2.class);
				startActivity(registerIntent);
				break;
			case R.id.iv_qq_login:
				loadingDialog = new LoadingDialog(LoginActivity.this, R.string.logining);
				loadingDialog.showDialog();
				ShareApiManager.qqLogin(self, new ShareApiManager.Listener<LoginInfo>() {
					@Override
					public void onComplete(LoginInfo loginInfo) {
						LoginActivity.this.loginInfo = loginInfo;
						Log.e("getToken", loginInfo.getToken());
						Log.e("getNickName", loginInfo.getNickName());
						Log.e("getUserId", loginInfo.getUserId());
						Log.e("getGender", loginInfo.getGender() + "");
						Log.e("getUserType", loginInfo.getUserType() + "");
						WebAPIManager.getInstance().login(loginInfo.getUserId(), loginInfo.getUserType(), null, loginInfo.getToken(), pushId, responseHandler);
					}
				});
				break;
			case R.id.iv_weixin_login: {
				loadingDialog = new LoadingDialog(LoginActivity.this, R.string.waiting);
				loadingDialog.showDialog();
				ShareApiManager.wechatLogin(self, new ShareApiManager.Listener<LoginInfo>() {
					@Override
					public void onComplete(LoginInfo loginInfo) {
						LoginActivity.this.loginInfo = loginInfo;
						Log.e("getToken", loginInfo.getToken());
						Log.e("getNickName", loginInfo.getNickName());
						Log.e("getUserId", loginInfo.getUserId());
						Log.e("getGender", loginInfo.getGender() + "");
						Log.e("getUserType", loginInfo.getUserType() + "");
						WebAPIManager.getInstance().login(loginInfo.getUserId(), loginInfo.getUserType(), null, loginInfo.getToken(), pushId, responseHandler);
					}
				});
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	private void login() {
		// 存登录状态进sharepreference
		String phone = et_phone.getText().toString().trim();
		String pwd = et_pwd.getText().toString();
		String pushId = JPushInterface.getRegistrationID(self);
		if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pwd)) {
			new LoginFailDialog(this).show();
			return;
		}
		loadingDialog = new LoadingDialog(LoginActivity.this, R.string.logining);
		loadingDialog.showDialog();
		WebAPIManager.getInstance().login(1, phone, pwd, pushId, new WebResponseHandler<User>(this) {
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
				loadingDialog.dismiss();
				// 弹出提醒对话框
				if (response.getCode().equals(WebResponse.CODE_ACCOUNT_NOT_EXISTED) && et_phone.getText().toString().trim().length() == 11) {
					LoginDialog.getInstance(LoginActivity.this, et_phone.getText().toString()).show();
				} else {
					TipsUtil.showTips(self, response);
				}
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				loadingDialog.dismiss();
				doAfterSuccess(response);
			}
		});

	}

	//登成功后
	private void doAfterSuccess(WebResponse<User> response) {
		User user = response.getResultObj();
		String token = user.getToken();
		String user_id = user.getUserid();

		// TODO jump into mainActivity when clicking the
		// loginout button
		if (isMainActivity) {
			EasyActivityManager.getInstance().finishAll();
			Intent _intent = new Intent(LoginActivity.this, MainActivity.class);
			LoginActivity.this.startActivity(_intent);
			isMainActivity = false;
		}
		if (isLoginOuted) {
			if (lastPhone == null) {
				finish();
			}
			if (lastPhone != null && lastPhone.equals(et_phone.getText().toString().trim())) {
				finish();
			} else if (lastPhone != null && !lastPhone.equals(et_phone.getText().toString().trim())) {
				EasyActivityManager.getInstance().finishAll();
				Intent _intent = new Intent(LoginActivity.this, MainActivity.class);
				_intent.putExtra("isIgnore", true);
				LoginActivity.this.startActivity(_intent);
			}
		} else {
			finish();
		}
		WebAPIManager.getInstance().setAccessToken(token); //
		// WebAPIManager中保存token，后台需要用到
		DbHelper.getInstance(LoginActivity.this).getUserDao().deleteAll();
		DbHelper.getInstance(LoginActivity.this).getPileDao().deleteAll();
		DbHelper.getInstance(LoginActivity.this).getUserDao().insertOrReplace(user); // 数据库中插入user，方便以后查询
		Editor editor1 = sp.edit();
		editor1.putString("last_user_id", user_id); // 保存用户ID，用于记住账号
		editor1.putString("user_id", user_id); // 保存用户ID
		editor1.putBoolean("isLogin", true);// 登录保存状态判定

		editor1.commit();
		getOwnPile(user_id); // 服务器获取个人桩，存进数据库
		bindPushId(user_id);// 绑定推送
	}

	private void bindPushId(String user_id) {
		// 绑定pushId
		WebAPIManager.getInstance().bindPushId(user_id, JPushInterface.getRegistrationID(self), 1, new WebResponseHandler<Object>() {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				Log.d("--MainActivity--", "[pushId] 成功绑定 :" + JPushInterface.getRegistrationID(self));
			}

		});
	}

	private void getOwnPile(final String user_id) {
		WebAPIManager.getInstance().getOwnPile(user_id, new WebResponseHandler<List<Pile>>(this) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
			}

			@Override
			public void onFailure(WebResponse<List<Pile>> response) {
			}

			@Override
			public void onSuccess(WebResponse<List<Pile>> response) {
				super.onSuccess(response);
				List<Pile> piles = response.getResultObj();
				if (piles != null) {
					DbHelper.getInstance(LoginActivity.this).insertInTxPile(piles);
					Log.i("TAG", piles.toString());
				}
			}

		});
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		if (!TextUtils.isEmpty(et_phone.getText()) && !TextUtils.isEmpty(et_pwd.getText())) {
			btn_login.setEnabled(true);
		} else {
			btn_login.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable editable) {

	}
}
