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

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * 
 * @author Mazoh 个人信息中心---修改支付宝账号 2015 07 28
 * 
 */
public class EditAlipayNameActivity extends BaseActivity implements
		OnClickListener {

	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_title;
	private EditText et_alipayname;
	private String alipayname;
	private SharedPreferences sp;
	private String user_id;
	private User user ;
	private  LoadingDialog loadingDialog = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		alipayname = intent.getStringExtra("alipayname");
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		user = DbHelper.getInstance(this).getUserDao().load(user_id) ;
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.edit_alipayname_layout, null);
		setContentView(view);
		hideSoftInput(view);
		et_alipayname = (EditText) findViewById(R.id.et_alipayname);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		tv_title = (TextView) findViewById(R.id.tv_center);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setImageResource(R.drawable.ok_icon2);

		tv_title.setText("支付宝账号");
		et_alipayname.setText(alipayname);

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
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
			break;
		case R.id.btn_right:

			    loadingDialog = new LoadingDialog(self,R.string.loading) ;
			    loadingDialog.showDialog();
				WebAPIManager.getInstance().modifyUserInfoForalipayName(
						user_id, et_alipayname.getText().toString(),
						new WebResponseHandler<User>(this) {

							@Override
							public void onStart() {
								super.onStart();
							}

							@Override
							public void onError(Throwable e) {
								super.onError(e);
								loadingDialog.dismiss();
							}

							@Override
							public void onFailure(WebResponse<User> response) {
								super.onFailure(response);
								ToastUtil.show(EditAlipayNameActivity.this,
										response.getMessage() + "");
								loadingDialog.dismiss();

							}

							@Override
							public void onSuccess(WebResponse<User> response) {
								super.onSuccess(response);
								loadingDialog.dismiss();
								ToastUtil.show(self, R.string.fix_success_tip);
								String refreshToken=   user.getRefreshToken() + "" ;
								String token = user.getToken()+"" ;
								if (response.getResultObj() != null) {
									response.getResultObj().setRefreshToken(refreshToken);
									response.getResultObj()
											.setToken(token); // 服务器返回的token为空，手动设置token
									DbHelper.getInstance(
											EditAlipayNameActivity.this)
											.getUserDao()
											.update(response.getResultObj());
								}
								Intent intent = new Intent();
								intent.putExtra("alipayname", et_alipayname
										.getText().toString().trim());
								setResult(Activity.RESULT_OK, intent);
								finish();
								overridePendingTransition(
										R.anim.slide_right_in,
										R.anim.slide_left_out);
							}

						});
			break;

		}
	}


}
