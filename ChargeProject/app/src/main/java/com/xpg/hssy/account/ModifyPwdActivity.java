package com.xpg.hssy.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TextViewDrawableClickUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * 
 * @author Mazoh 用户注册--info 修改密码
 * 
 */
public class ModifyPwdActivity extends BaseActivity implements OnClickListener {

	private ImageButton btn_left, btn_right;
	private EditText et_cur_pwd, et_new_pwd, et_comfirm_new_pwd;
	private SharedPreferences sp;
	private String user_id;
	private Drawable drawableRight;
	private Drawable drawableLeft;
	private LoadingDialog loadingDialog = null ;
	private  User user  ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", "");
		user = DbHelper.getInstance(this).getUserDao().load(user_id) ;
		editDrawable();
	}

	private void editDrawable() {

		drawableRight = getResources().getDrawable(R.drawable.emotionstore_progresscancelbtn);
		drawableLeft = getResources().getDrawable(R.drawable.login_icon_password);

		drawableRight.setBounds(0, 0, drawableRight.getMinimumWidth(),
				drawableRight.getMinimumHeight());
		drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(),
				drawableLeft.getMinimumHeight());
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.modify_pwd_layout, null);
		setContentView(view);
		hideSoftInput(view);
		setTitle("修改密码");
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setImageResource(R.drawable.ok_icon2) ;
		et_cur_pwd = (EditText) findViewById(R.id.et_cur_pwd);
		et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
		et_comfirm_new_pwd = (EditText) findViewById(R.id.et_comfirm_new_pwd);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_left.setOnClickListener(this);
		btn_right.setOnClickListener(this);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_cur_pwd);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_new_pwd);
		TextViewDrawableClickUtil.setRightDrawableDelete(et_comfirm_new_pwd);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
			case R.id.btn_right:
				if (et_cur_pwd.getText().toString().trim().isEmpty()) {
					ToastUtil.show(this, "当前密码不能为空");
					return ;
				} else if (et_new_pwd.getText().toString().trim().isEmpty()) {
					ToastUtil.show(this, "新密码不能为空");
					return ;
				} else if (et_comfirm_new_pwd.getText().toString().trim().isEmpty()) {
					ToastUtil.show(this, "确认密码不能为空");
					return ;
				} else if (et_cur_pwd.getText().toString().length() < 6
						|| et_new_pwd.getText().toString().length() < 6
						|| et_comfirm_new_pwd.getText().toString().length() < 6) {
					ToastUtil.show(this, "密码应为6-16位的数字或字母");
					return;
				} else if(!et_comfirm_new_pwd.getText().toString().equals(et_new_pwd.getText().toString())){
					ToastUtil.show(this, "新密码两次输入不一致");
					return;
				} else if(et_cur_pwd.getText().toString().equals(et_new_pwd.getText().toString())){
					ToastUtil.show(this, "新旧密码重复，请重新设置");
					return;
				} else {
					loadingDialog = new LoadingDialog(ModifyPwdActivity.this,R.string.loading) ;
					loadingDialog.showDialog();
					WebAPIManager.getInstance().modifyUserInfoForPwd(user_id,
							et_cur_pwd.getText().toString().trim(),
							et_new_pwd.getText().toString().trim(),
							et_comfirm_new_pwd.getText().toString().trim(),
							new WebResponseHandler<User>(this) {
								@Override
								public void onStart() {
									super.onStart();
								}

								@Override
								public void onError(Throwable e) {
									super.onError(e);
									loadingDialog.dismiss();
									TipsUtil.showTips(self,e);
								}

								@Override
								public void onFailure(WebResponse<User> response) {
									super.onFailure(response);
									loadingDialog.dismiss();
									TipsUtil.showTips(self,response);
								}

								@Override
								public void onSuccess(WebResponse<User> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();
									ToastUtil.show(self, R.string.fix_success_tip);
									String refreshToken=   user.getRefreshToken()+"" ;
									String token = user.getToken() +"";
									if (response.getResultObj() != null) {
										response.getResultObj().setRefreshToken(refreshToken);
										response.getResultObj()
												.setToken(token); // 服务器返回的token为空，手动设置token
										DbHelper.getInstance(ModifyPwdActivity.this)
												.getUserDao()
												.update(response.getResultObj());
									}
									finish();
								}
							});
				}
				break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
