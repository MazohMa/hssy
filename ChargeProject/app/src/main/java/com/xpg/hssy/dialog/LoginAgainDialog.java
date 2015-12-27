package com.xpg.hssy.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;

import com.easy.manager.EasyActivityManager;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssychargingpole.R;

public class LoginAgainDialog extends BaseDialog {
	private static LoginAgainDialog self;
	private Context context;
	private SharedPreferences sp;

	// private String phone;

	public static LoginAgainDialog getInstance(Context context) {
		if (self == null) {
			self = new LoginAgainDialog(context);
		} else {
			// 不是同一个context，则将上一个取消掉，显示最新的context
			if (self.getContext() != context) {
				try {
					self.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
				self = new LoginAgainDialog(context);
			}
		}
		return self;
	}

	private LoginAgainDialog(Context context) {
		super(context);
		this.context = context;
		setContentView(R.layout.water_blue_dialog_title);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		// String user_id = sp.getString("user_id", "");
		// User user =
		// DbHelper.getInstance(context).getUserDao().load(user_id);// 数据库中获取
		// phone = user.getPhone();
		setCancelable(false);
	}

	public void initBroadcast() {
		// 注册一个广播，这个广播主要是用来获取service sendbroad过来的充电费用
		Intent intentBroadcast = new Intent(KEY.INTENT.ACTIONFORIGNORE);
		context.sendBroadcast(intentBroadcast);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.btn_ok:
				afterLogoutForLoginAgain();

				Intent intent = new Intent(context, LoginActivity.class);
				intent.putExtra("isLoginOuted", true);
				context.startActivity(intent);
				break;
			case R.id.btn_cancel:
				afterLogoutForIgnore();
//			// 注册一个广播，这个广播主要是用来获取service sendbroad过来的充电费用
//			Intent intentBroadcasts = new Intent(KEY.INTENT.ACTIONFORIGNORE);
//			context.sendBroadcast(intentBroadcasts);
				break;
			default:
				break;
		}
	}

	private void afterLogoutForLoginAgain() {
		Editor ed = sp.edit();
		ed.putBoolean("isLogin", false);
		ed.putString("user_id", "");
		ed.putString(MyConstant.BT_MAC_LAST, "");
		ed.commit();
		BTManager.getInstance().disconnect();
		if (context instanceof MainActivity) {
			Log.i("tag", "afterLogout with the context of MainActivity.instance");
			// 当用户停留在“我”的页面，弹出重新登录后，需要清掉头像和用户名
			((MainActivity) context).sendMessage(MainActivity.MSG_REFRESH_VIEW, null);
		} else {
			Log.i("tag", "afterLogout with the context of instance");

		}
	}

	private void afterLogoutForIgnore() {
		Editor ed = sp.edit();
		ed.putBoolean("isLogin", false);
		ed.putString("user_id", "");
		ed.putString(MyConstant.BT_MAC_LAST, "");
		ed.commit();
		BTManager.getInstance().disconnect();
		if (context instanceof MainActivity) {
			Log.i("tag", "afterLogout with the context of MainActivity.instance");
			// 当用户停留在“我”的页面，弹出重新登录后，需要清掉头像和用户名
			((MainActivity) context).sendMessage(MainActivity.MSG_REFRESH_VIEW, null);
		} else {
			// 当用户停留在mainActivity context 管理范围以外，弹出重新登录后，重新启动app

			Log.i("tag", "afterLogout with the context of instance");
			EasyActivityManager.getInstance().finishAll();
			Intent _intent = new Intent(context,
					MainActivity.class);
			_intent.putExtra("isIgnore", true);
			context.startActivity(_intent);
		}
	}
}
