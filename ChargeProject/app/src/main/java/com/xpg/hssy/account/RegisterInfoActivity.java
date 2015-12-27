package com.xpg.hssy.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.LoginInfo;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.TextUtil;
import com.xpg.hssy.util.TextViewDrawableClickUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author Mazoh 用户注册
 */
public class RegisterInfoActivity extends BaseActivity implements OnClickListener {

	/**
	 * 页面类型，根据跳进来的intent进行判断，默认为1
	 * 1：默认的注册页面
	 * 2：第三方后完善资料页面
	 */
	private int pageType = 1;

	private EditText et_phone, et_pwd, et_nickname;
	private TextView read_bt;
	private Button next;
	private String phone;
	private String verify;
	private RadioGroup sex_tab_new;
	private int gender = 1; // 1为男，2为女
	private SharedPreferences sp;
	private ImageButton btn_left, btn_right;
	private LoadingDialog loadingDialog = null;
	private Intent data;
	private LoginInfo loginInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		data = getIntent();
		phone = data.getStringExtra("phone");
		verify = data.getStringExtra("verify");
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view;
		//这里判断是否第三方注册
		if ((loginInfo = (LoginInfo) data.getSerializableExtra("logininfo")) != null) {
			view = LayoutInflater.from(this).inflate(R.layout.register_info_third_acc_layout, null);
			setContentView(view);
			hideSoftInput(view);
			et_pwd = (EditText) findViewById(R.id.et_pwd);
			et_nickname = (EditText) findViewById(R.id.et_nickname);
			read_bt = (TextView) findViewById(R.id.read_bt);
			next = (Button) findViewById(R.id.next);
			btn_left = (ImageButton) findViewById(R.id.btn_left);
			btn_right = (ImageButton) findViewById(R.id.btn_right);
			et_nickname.setText(loginInfo.getNickName() + "");
			next.setText("完成绑定");
			setTitle("设置密码");
			btn_right.setVisibility(View.INVISIBLE);
			pageType = 2;
		} else {
			view = LayoutInflater.from(this).inflate(R.layout.register_info_layout, null);
			setContentView(view);
			hideSoftInput(view);
			setTitle(R.string.user_register);
			et_phone = (EditText) findViewById(R.id.et_phone);
			et_pwd = (EditText) findViewById(R.id.et_pwd);
			et_nickname = (EditText) findViewById(R.id.et_nickname);
			read_bt = (TextView) findViewById(R.id.read_bt);
			next = (Button) findViewById(R.id.next);
			sex_tab_new = (RadioGroup) findViewById(R.id.sex_tab_new);
			btn_left = (ImageButton) findViewById(R.id.btn_left);
			btn_right = (ImageButton) findViewById(R.id.btn_right);
			et_phone.setText(phone);
			btn_right.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		next.setOnClickListener(this);
		read_bt.setOnClickListener(this);
		if (loginInfo == null) {
			sex_tab_new.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					// 获取变更后的选中项的ID
					int radioButtonId = arg0.getCheckedRadioButtonId();
					// 根据ID获取RadioButton的实例
					RadioButton rb = (RadioButton) RegisterInfoActivity.this.findViewById(radioButtonId);
					// 更新文本内容，以符合选中项
					gender = rb.getText().toString().equals("男") ? 1 : 2;
				}
			});
		}
		TextViewDrawableClickUtil.setRightDrawableDelete(et_pwd);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_nickname);
	}

	private void register(Activity context) {
		String pwd = et_pwd.getText().toString();

		if (TextUtils.isEmpty(pwd)) {
			ToastUtil.show(self, "请设置登录密码");
			return;
		}
		if (TextUtil.isChinese(pwd)) {
			ToastUtil.show(self, "密码不能为中文");
			return;
		}
		if (pwd.length() < 6 || pwd.length() > 16) {
			ToastUtil.show(self, "密码长度为6到16位");
			return;
		}

		switch (pageType) {
			case 1: {
				// 用户类型，手机用户类型为1，注册逻辑
				String nickname = et_nickname.getText().toString();
				String phone_login = et_phone.getText().toString();
				if (TextUtils.isEmpty(phone_login)) {
					ToastUtil.show(self, "手机号不能为空");
					return;
				}
				if (phone_login.length() != 11) {
					ToastUtil.show(self, "手机号码为11位");
					return;
				}
				if (TextUtils.isEmpty(nickname)) {
					ToastUtil.show(self, "用户名不能为空");
					return;
				}
				register(context, nickname, verify, phone_login, pwd, gender);
				break;
			}
			case 2: {
				Log.e("verify", verify);
				String nickname = et_nickname.getText().toString();
				if (TextUtils.isEmpty(nickname)) {
					ToastUtil.show(self, "用户名不能为空");
					return;
				}
				loginInfo.setNickName(nickname);
				register(context, verify, loginInfo.getToken(), loginInfo.getUserId(), loginInfo.getUserType(), phone, pwd, loginInfo.getNickName(), loginInfo
						.getGender());
				break;
			}
		}
	}

	/*
	         * 注册
             */
	private void register(Activity context, String nickname, String verifyCode, final String phone_login, final String pwd, int gender) {
		WebAPIManager.getInstance().register(verifyCode, 1, phone_login, pwd, nickname, gender, new WebResponseHandler<User>(context) {

			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(RegisterInfoActivity.this, R.string.loading_register);
				loadingDialog.showDialog();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);// 网络请求失败
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);// 注册失败

				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);// 注册成功

				login(phone_login, pwd);// 注册之后登录
				Intent intent = new Intent(RegisterInfoActivity.this, RegisterActivitySuccessfully.class);
				RegisterInfoActivity.this.startActivity(intent);
				finish();
			}

		});
	}

	//完善信息并注册
	private void register(Activity context, String checkCode, String thirdAccToken, String thirdUid, int userType, final String phone, final String password,
	                      String name, int gender) {
		WebAPIManager.getInstance().registerThirdAcc(thirdAccToken, checkCode, thirdUid, userType, phone, password, name, gender, new WebResponseHandler<User>
				(context) {

			@Override
			public void onStart() {
				super.onStart();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				loadingDialog = new LoadingDialog(RegisterInfoActivity.this, R.string.loading_register);
				loadingDialog.showDialog();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);// 网络请求失败
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<User> response) {
				super.onFailure(response);// 注册失败
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);// 注册成功
				// 注册之后登录
				if (loginInfo.getUserAvaterUrl() != null) {
					login(phone, password, loginInfo.getUserAvaterUrl());
				} else {
					login(phone, password);
				}
				Intent intent = new Intent(RegisterInfoActivity.this, RegisterActivitySuccessfully.class);
				RegisterInfoActivity.this.startActivity(intent);
				finish();
			}

		});
	}

	/*
	 * 注册之后登录
	 */
	private void login(String phone_login, String pwd) {
		WebAPIManager.getInstance().login(1, phone_login, pwd, new WebResponseHandler<User>(this) {

			@Override
			public void onSuccess(WebResponse<User> response) {
				super.onSuccess(response);
				Editor editor1 = sp.edit();

				User user = response.getResultObj();
				String token = user.getToken();
				String user_id = user.getUserid();
				editor1.putString("user_id", user_id); // 保存用户ID
				editor1.commit();
				WebAPIManager.getInstance().setAccessToken(token); // WebAPIManager中保存token，后台需要用到
				DbHelper.getInstance(RegisterInfoActivity.this).getUserDao().insertOrReplace(user); // 数据库中插入user，方便以后查询
				Editor editor = sp.edit();
				editor.putBoolean("isLogin", true);// 登录保存状态判定
				editor.commit();
				getOwnPile(user_id);// 服务器获得pile，插入本地数据库
			}

			private void getOwnPile(String user_id) {
				WebAPIManager.getInstance().getOwnPile(user_id, new WebResponseHandler<List<Pile>>(RegisterInfoActivity.this) {

					@Override
					public void onSuccess(WebResponse<List<Pile>> response) {
						super.onSuccess(response);
						List<Pile> piles = response.getResultObj();
						if (piles != null) {
							DbHelper.getInstance(RegisterInfoActivity.this).insertInTxPile(piles);
						}
						Editor editor = sp.edit();
						editor.putBoolean("isLogin", true);// 登录保存状态判定
						editor.commit();
					}

				});
			}

		});
	}

	private void login(String phone_login, String pwd, final String avaterUrl) {
		if (TextUtils.isEmpty(avaterUrl)) {
			login(phone_login, pwd);
		} else {
			WebAPIManager.getInstance().login(1, phone_login, pwd, new WebResponseHandler<User>(this) {

				@Override
				public void onSuccess(WebResponse<User> response) {
					super.onSuccess(response);
					Editor editor1 = sp.edit();

					User user = response.getResultObj();
					String token = user.getToken();
					String user_id = user.getUserid();
					editor1.putString("user_id", user_id); // 保存用户ID
					editor1.commit();
					WebAPIManager.getInstance().setAccessToken(token); // WebAPIManager中保存token，后台需要用到
					DbHelper.getInstance(RegisterInfoActivity.this).getUserDao().insertOrReplace(user); // 数据库中插入user，方便以后查询
					Editor editor = sp.edit();
					editor.putBoolean("isLogin", true);// 登录保存状态判定
					editor.commit();
					getOwnPile(user_id);// 服务器获得pile，插入本地数据库
					Log.e("avaterUrl", avaterUrl + "");
					uploadUserAvater(user_id, avaterUrl);
				}

				private void getOwnPile(String user_id) {
					WebAPIManager.getInstance().getOwnPile(user_id, new WebResponseHandler<List<Pile>>(RegisterInfoActivity.this) {

						@Override
						public void onSuccess(WebResponse<List<Pile>> response) {
							super.onSuccess(response);
							List<Pile> piles = response.getResultObj();
							if (piles != null) {
								DbHelper.getInstance(RegisterInfoActivity.this).insertInTxPile(piles);
							}
							Editor editor = sp.edit();
							editor.putBoolean("isLogin", true);// 登录保存状态判定
							editor.commit();
						}

					});
				}

				private void uploadUserAvater(final String userId, final String avaterUrl) {
					ImageLoaderManager.getInstance().loadImage(avaterUrl, new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String s, View view) {

						}

						@Override
						public void onLoadingFailed(String s, View view, FailReason failReason) {

						}

						@Override
						public void onLoadingComplete(String s, View view, Bitmap bitmap) {
							HashMap<String, Object> fileAndKey = new HashMap<>();
							File file = ImageLoaderManager.getInstance().getCacheFile(avaterUrl);
							if (file!=null&&file.exists()) {
								fileAndKey.put("avatarUrl", file.getAbsoluteFile().toString());
								Log.e("avaterUrl", file.getAbsoluteFile().toString());
							} else {
								Log.e("avaterUrl", "file not found");
							}
							WebAPIManager.getInstance().modifyUserInfoForAvatar(userId, fileAndKey, new WebResponseHandler<User>() {
								@Override
								public void onSuccess(WebResponse<User> response) {
									super.onSuccess(response);
									User user = DbHelper.getInstance(self).getUserDao().load(userId);
									String refreshToken = user.getRefreshToken() + "";
									String token = user.getToken() + "";
									if (response.getResultObj() != null) {
										response.getResultObj().setRefreshToken(refreshToken);
										response.getResultObj().setToken(token); // 服务器返回的token为空，手动设置token
										DbHelper.getInstance(self).getUserDao().update(response.getResultObj());
									}
								}
							});
						}

						@Override
						public void onLoadingCancelled(String s, View view) {

						}
					});
				}

			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.next:
				register(this);
			case R.id.read_bt:

			default:
				break;

		}
	}

}
