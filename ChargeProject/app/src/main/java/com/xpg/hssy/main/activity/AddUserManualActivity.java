package com.xpg.hssy.main.activity;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * AddUserManualActivity
 *
 * @author Mazoh
 * @version 2.0.0
 * @Description 手动添加家人
 * @createDate 2015年04月16日
 */
public class AddUserManualActivity extends BaseActivity {
	private String pile_id;
	private SharedPreferences sp;
	private EditText shoudongadd_user_et_phone;
	private Pile pile;
	private String user_id;
    private LoadingDialog loadingDialog = null ;
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		pile_id = getIntent().getStringExtra("pile_id");
		pile = DbHelper.getInstance(AddUserManualActivity.this).getPileDao().load(pile_id);
		user_id = sp.getString("user_id", null);
		//		currentUser = DbHelper.getInstance(this).getUserDao().load(user_id);
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(R.layout.authority_add_user, null);
		setContentView(view);
		hideSoftInput(view);
		shoudongadd_user_et_phone = (EditText) findViewById(R.id.shoudongadd_user_et_phone);
		setTitle("手动添加用户");
	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRightBtn(View v) {
		String phone = shoudongadd_user_et_phone.getText().toString();
		if (TextUtils.isEmpty(phone) || phone.length() != 11) {
			ToastUtil.show(AddUserManualActivity.this, R.string.phone_format_error);
			return;
		} else {
			if (!isNetworkConnected()) {
				ToastUtil.show(AddUserManualActivity.this, R.string.network_no_connection);
				return;
			}
			addFamily(phone); // 添加用户
		}
	}

	// 获取登录用户拥有的key
	//	private WebResponseHandler<List<Key>> keyHandler = new WebResponseHandler<List<Key>>(
	//			"KeyHandler") {
	//
	//		@Override
	//		public void onFailure(WebResponse<List<Key>> response) {
	//			super.onFailure(response);
	//		}
	//
	//		@Override
	//		public void onSuccess(WebResponse<List<Key>> response) {
	//			super.onSuccess(response);
	//			DbHelper.getInstance(self).getKeyDao().deleteAll();
	//			List<Key> keys = response.getResultObj();
	//			if (EmptyUtil.isEmpty(keys)) {
	//				return;
	//			}
	//			// 保存到数据库
	//			DbHelper.getInstance(self).getKeyDao().insertInTx(keys);
	//		}
	//
	//		@Override
	//		public void onFinish() {
	//			Intent intent = new Intent();
	//			intent.putExtra("user", user);
	//			setResult(Activity.RESULT_OK, intent);
	//			finish();
	//		}
	//	};

	private void addFamily(String phone) {
		loadingDialog = new LoadingDialog(AddUserManualActivity.this,R.string.loading) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().addFamily(pile_id, phone, new WebResponseHandler<User>(this) {

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
						pile = DbHelper.getInstance(AddUserManualActivity.this).getPileDao().load
								(pile_id);
						if (pile.getFamilyNumber() == null) {
							pile.setFamilyNumber(1);
						} else {
							pile.setFamilyNumber(pile.getFamilyNumber() + 1);
						}
						DbHelper.getInstance(self).insertPile(pile);
						ToastUtil.show(AddUserManualActivity.this, "添加成功");
						setResult(Activity.RESULT_OK);
						finish();
					}

				});
	}

}
