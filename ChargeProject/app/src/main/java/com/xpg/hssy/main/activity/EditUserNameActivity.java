package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh 个人信息中心---修改用户名
 *         2015 07 28
 */
public class EditUserNameActivity extends BaseActivity implements OnClickListener {

	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_title;
	private EditText et_username;
	private String username;
	private SharedPreferences sp;
	private String user_id;
	private String token;
	private LoadingDialog loadingDialog = null;
	private User user ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		user = DbHelper.getInstance(this).getUserDao().load(user_id) ;
//		token = WebAPIManager.getInstance().getAccessToken();

	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.edit_username_layout, null);
		setContentView(view);
		hideSoftInput(view);
		et_username = (EditText) findViewById(R.id.et_username);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		tv_title = (TextView) findViewById(R.id.tv_center);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setImageResource(R.drawable.ok_icon2);
		tv_title.setText("用户名");
		et_username.setText(username);

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
				finish();
				overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
				break;
			case R.id.btn_right:
				if (!isValid()) {
					return;
				}
				loadingDialog = new LoadingDialog(self, R.string.loading);
				loadingDialog.showDialog();
				WebAPIManager.getInstance().modifyUserInfoForName(user_id, et_username.getText()
						.toString().trim(), new WebResponseHandler<User>(this) {

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
						TipsUtil.showTips(self, e);
					}

					@Override
					public void onFailure(WebResponse<User> response) {
						super.onFailure(response);
						loadingDialog.dismiss();
						TipsUtil.showTips(self, response);
					}

					@Override
					public void onSuccess(WebResponse<User> response) {
						super.onSuccess(response);
						loadingDialog.dismiss();
						ToastUtil.show(self, R.string.fix_success_tip);
						String refreshToken=   user.getRefreshToken() ;
						String token = user.getToken() ;
						if (response.getResultObj() != null) {
							response.getResultObj().setRefreshToken(refreshToken);
							response.getResultObj()
									.setToken(token); // 服务器返回的token为空，手动设置token
							DbHelper.getInstance(EditUserNameActivity.this).getUserDao()
									.update(response.getResultObj());
						}
						Intent intent = new Intent();
						intent.putExtra("username", et_username.getText().toString().trim
								());
						setResult(Activity.RESULT_OK, intent);
						finish();
						overridePendingTransition(R.anim.slide_right_in, R.anim
								.slide_left_out);
					}

				});
		}
	}

	private boolean isValid() {
		String newUserName = et_username.getText().toString().trim();
		if (EmptyUtil.isEmpty(newUserName)) {
			ToastUtil.show(this, getString(R.string.tips_username_null));
			return false;
		} else if (newUserName.length() < 2) {
			ToastUtil.show(this, R.string.tips_username_length);
			return false;
		}

		return true;
	}
}
