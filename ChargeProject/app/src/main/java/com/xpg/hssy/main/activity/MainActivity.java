package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.easy.util.BadgeUtil;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.engine.CmdManager;
import com.xpg.hssy.main.fragment.ChargeFragment;
import com.xpg.hssy.main.fragment.FocusFragment;
import com.xpg.hssy.main.fragment.MeFragment;
import com.xpg.hssy.main.fragment.PileFindFragment;
import com.xpg.hssy.service.ChargeRecordSyncService;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.util.UpdateUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssy.zxing.ZxingManager;
import com.xpg.hssychargingpole.MyApplication;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

	private ChargeFragment chargeFragment;
	private PileFindFragment pileFindFragment;
	private FocusFragment focusFragment;
	private MeFragment meFragment;
	private BaseFragment currentFragment;
	private FrameLayout fl_main_content;
	private RadioGroup rg_pager_tab;
	private RadioButton rb_charge_tab;
	private RadioButton rb_me_tab;
	private RadioButton rb_search_tab;
	private SharedPreferences sp;
	private String user_id;
	public static MainActivity instance;
	public static final int MSG_SWITCH_CHARGE_FRAGMENT = 0;
	public static final int MSG_SWITCH_SEARCH_FRAGMENT = 1;
	public static final int MSG_SWITCH_ME_FRAGMENT = 2;
	public static final int MSG_GO_CHARGE_COMPLETE = 3;
	public static final int MSG_GO_MY_HOMESETTING = 4;
	public static final int MSG_GO_LOGIN = 5;
	public static final int MSG_REFRESH_KEY = 7;
	public static final int MSG_REQUEST_MAP = 10;
	public static final int REQUEST_MAP = 10;
	public static final int MSG_REFRESH_VIEW = 11;
	public static final int REQUEST_FINISH = 101;
	public static final int REQUEST_MY_ORDER = 102;
	public static final int RESULT_FIND_PILE = 103;
	private boolean isIgnore = false;

	private Handler mainHandler;


	@Override
	protected void onResume() {
		super.onResume();
		if (isLogin()) {
			try {
				user_id = sp.getString("user_id", null);
				currentUser = DbHelper.getInstance(this).getUserDao().load(user_id);
				WebAPIManager.getInstance().setAccessToken(currentUser.getToken());

				startRefreshKey();
				Intent intent = new Intent(self, ChargeRecordSyncService.class);
				intent.putExtra("mode", ChargeRecordSyncService.MODE_UPLOAD_ALL);
				startService(intent);
			} catch (Exception e) {
				// 用户丢失，重置登录状态
				Editor editor = sp.edit();
				editor.putBoolean("isLogin", false);
				editor.commit();
			}
		} else {
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		stopRefreshKey();
	}
	// 获取登录用户拥有的key
	private WebResponseHandler<List<Key>> keyHandler = new WebResponseHandler<List<Key>>("KeyHandler") {

		@Override
		public void onError(Throwable e) {
			stopRefreshKey();
			TipsUtil.showTips(self, e);
		}

		@Override
		public void onFailure(WebResponse<List<Key>> response) {
			super.onFailure(response);
			stopRefreshKey();
			TipsUtil.showTips(self, response);
		}

		@Override
		public void onSuccess(WebResponse<List<Key>> response) {
			super.onSuccess(response);
			// 清空与原来的key
			DbHelper.getInstance(self).getKeyDao().deleteAll();
			List<Key> keys = response.getResultObj();
			if (EmptyUtil.isEmpty(keys)) {
				return;
			}
			// 保存到数据库
			DbHelper.getInstance(self).getKeyDao().insertInTx(keys);
			// 同步家人列表（新版已经不需要同步家人了）
			// chargeFragment.syncFamily();
			List<String> orderIds = new ArrayList<String>();
			for (Key key : keys) {
				if (key.getUserId().equals(user_id) && key.getOrderId() != null) {


					Log.v("tag", "KeyType: " + key.getKeyType());
					Log.v("tag", "Key: " + key.getKey());
					Log.i("tag", key.getOrderId());
					Log.i("tag",key.getKeyType()+ "") ;
					orderIds.add(key.getOrderId());
				} else {
				}
			}
			if (orderIds.size() > 0) {
				// 通过遍历key,获得orderId，若orderId不为空，服务器请求获得order，存进本地数据库
				List<String> unexistOrderIds = DbHelper.getInstance(self).getUnexistOrderId(orderIds);
				if (unexistOrderIds.size() > 0) {
					for (String orderId : unexistOrderIds) {
						Log.i("tag", "user_id:" + user_id);
						WebAPIManager.getInstance().getOrderByOrderId(user_id, orderId, new WebResponseHandler<ChargeOrder>(self) {

							@Override
							public void onError(Throwable e) {
								super.onError(e);
								TipsUtil.showTips(self, e);
							}

							@Override
							public void onFailure(WebResponse<ChargeOrder> response) {
								super.onFailure(response);
								TipsUtil.showTips(self, response);
							}

							@Override
							public void onSuccess(WebResponse<ChargeOrder> response) {
								super.onSuccess(response);
								ChargeOrder chargeOrder = response.getResultObj();
								if (chargeOrder != null) {
									Log.i("tag", chargeOrder.getUserid() + "main");
									DbHelper.getInstance(self).getChargeOrderDao().insertOrReplace(response.getResultObj());
								}
							}

						});
					}
				}
			}

		}

		@Override
		public void onFinish() {
			if (refreshing) mainHandler.sendEmptyMessageDelayed(MSG_REFRESH_KEY, 10000);// 10秒刷新key
		}
	};

	// 线程消息处理
	public void sendMessage(int what, Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.obj = obj;
		mainHandler.sendMessage(msg);
	}

	/*
	 * 按两次后退键退出程序
	 */
	@Override
	public void onBackPressed() {
		setExitable(true);
		super.onBackPressed();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isIgnore) {
		} else {
			// 打开app第一次进入主页时，检查更新
			if (!MyApplication.getInstance().isFirstUpdated()) {
				UpdateUtil.firstUpdate(this);
			}
		}
		loadUnReadMessageNum();
		registerReceiver(updateUI, new IntentFilter(KEY.ACTION.ACTION_IMMEDIATELY_CHARGE));
	}

	public void loadUnReadMessageNum() {
		if (isLogin()) {
			String user_id = sp.getString("user_id", "");
			getUnreadMessage(user_id);
			// 未读消息数量
		} else {
			setUnreadMsgNum(0);
		}
	}

	private void getUnreadMessage(String userId) {
		WebAPIManager.getInstance().getUnreadMessageNum(userId, new WebResponseHandler<String>(this) {

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(self, e);
			}

			@Override
			public void onFailure(WebResponse<String> response) {
				super.onFailure(response);
				TipsUtil.showTips(self, response);
			}

			@Override
			public void onSuccess(WebResponse<String> response) {
				super.onSuccess(response);
				LogUtils.e("MainActivity", "response:" + response);
				try {
					setUnreadMsgNum(Integer.parseInt(response.getResult()));
				} catch (Exception e) {
					setUnreadMsgNum(0);
				}
			}

		});
	}

	private void setUnreadMsgNum(int num) {
		if (num > 0) {
			BadgeUtil.setBadgeCount(this, num);
		} else {
			BadgeUtil.resetBadgeCount(this);
		}
	}


	@Override
	protected void initData() {
		super.initData();
		mainHandler = new MainHandler();
		instance = this;
		sp = getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
		Intent intent = getIntent();
		if (intent != null) {
			isIgnore = intent.getBooleanExtra("isIgnore", false);
		}
		//初始化蓝牙
		BTManager.getInstance().init(this);
		CmdManager.getInstance().init();

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.main_activity);
		fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
		rg_pager_tab = (RadioGroup) findViewById(R.id.rg_pager_tab);
		rb_charge_tab = (RadioButton) findViewById(R.id.rb_charge_tab);
		rb_me_tab = (RadioButton) findViewById(R.id.rb_me_tab);
		rb_search_tab = (RadioButton) findViewById(R.id.rb_search_tab);

		chargeFragment = new ChargeFragment();
		pileFindFragment = new PileFindFragment();
		focusFragment = new FocusFragment();
		meFragment = new MeFragment();
		setScrollable(false);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		rg_pager_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.rb_charge_tab: {
						showFragment(chargeFragment);
						break;
					}
					case R.id.rb_search_tab: {
						showFragment(pileFindFragment);
						break;
					}
					case R.id.rb_focus_tab: {
						showFragment(focusFragment);
						break;
					}
					case R.id.rb_me_tab: {
						showFragment(meFragment);
						break;
					}
				}
			}
		});
		rb_search_tab.setChecked(true);


	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("tag", "requestCode>>>>>" + requestCode + "\t\t resultCode>>>>>" + resultCode);

		if (resultCode == RESULT_FIND_PILE) {
			rb_search_tab.setChecked(true);
		}
		if (currentFragment != null) {
			currentFragment.onActivityResult(requestCode, resultCode, data);
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_FINISH:
					rb_search_tab.setChecked(true);
					break;
				default:
					break;
			}
		}
	}

	private boolean isLogin() {
		if (sp == null) {
			sp = MainActivity.instance.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean("isLogin", false);

	}

	private User currentUser;
	private boolean refreshing;

	private void startRefreshKey() {
		stopRefreshKey();
		refreshing = true;
		mainHandler.sendEmptyMessage(MSG_REFRESH_KEY);
	}

	private void stopRefreshKey() {
		refreshing = false;
		mainHandler.removeMessages(MSG_REFRESH_KEY);

	}

	public void setScrollable(boolean b) {
	}


	private BroadcastReceiver updateUI = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(KEY.ACTION.ACTION_IMMEDIATELY_CHARGE)) {
				rb_charge_tab.setChecked(true);
				Log.i("tag", "onReceive=========");
				mainHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (currentFragment != null && currentFragment instanceof ChargeFragment)

							((ChargeFragment) currentFragment).setViewType();
						LogUtils.e("MianActivity", "updateUI");
					}
				}, 100);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopRefreshKey();
		mainHandler.removeCallbacksAndMessages(null) ;
		BTManager.getInstance().destroy();
		stopService(new Intent(this, ChargeRecordSyncService.class));
		try {
			unregisterReceiver(updateUI);
		} catch (Exception e) {
		}
		System.gc();
	}

	private void showFragment(BaseFragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if (currentFragment != null) {
			if (currentFragment instanceof ChargeFragment) {
				if (currentFragment.isAdded()) {
					View view = ((ChargeFragment) currentFragment).getPreview_view() ;
					ZxingManager zxingManager = ((ChargeFragment) currentFragment).getmZxingManager() ;
					if (view != null) {
						if (view.getVisibility() == View.VISIBLE) {
							view.setVisibility(View.INVISIBLE);
							if (zxingManager != null) {
								zxingManager.onPause();
							}
						}
					}
					transaction.hide(currentFragment);
				}
			} else {
				currentFragment.setUserVisibleHint(false);
				transaction.remove(currentFragment);
			}
		}
		if (fragment instanceof ChargeFragment) {
			if (fragment.isAdded()) {
				transaction.show(fragment);
				View view = ((ChargeFragment) fragment).getPreview_view() ;
				View viewSwitch = ((ChargeFragment) fragment).getSwitch_onoff() ;
				ZxingManager zxingManager = ((ChargeFragment) fragment).getmZxingManager() ;
				if (view != null) {
					if (view.getVisibility() == View.GONE
							|| view.getVisibility() == View.INVISIBLE) {
						view.setVisibility(View.VISIBLE);
						if (zxingManager != null) {
							zxingManager.onResume();
						}
						if (viewSwitch != null
								&& ((ChargeFragment) fragment).getState() == -1) {
							((ToggleButton)viewSwitch).setChecked(false);
						}
					}
				}
			} else {
				transaction.add(R.id.fl_main_content, fragment).show(fragment);
			}
		} else {
			transaction.add(R.id.fl_main_content, fragment);
		}
		transaction.commitAllowingStateLoss();
//		大致意思是说我使用的 commit方法是在Activity的onSaveInstanceState()之后调用的，这样会出错，因为
//		onSaveInstanceState方法是在该Activity即将被销毁前调用，来保存Activity数据的，如果在保存玩状态后
//		再给它添加Fragment就会出错。解决办法就是把commit（）方法替换成 commitAllowingStateLoss()就行
//		了，其效果是一样的
		currentFragment = fragment;
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (currentFragment instanceof ChargeFragment) {

				} else {
					currentFragment.setUserVisibleHint(true);
				}
			}
		});

	}

	private static class MainHandler extends Handler {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_SWITCH_CHARGE_FRAGMENT:

					break;
				case MSG_SWITCH_SEARCH_FRAGMENT:
					instance.rb_search_tab.setChecked(true);
					break;
				case MSG_SWITCH_ME_FRAGMENT:

					break;
				case MSG_GO_CHARGE_COMPLETE:
					break;
				case MSG_GO_MY_HOMESETTING:// 主页设置页面
					break;
				case MSG_GO_LOGIN:// 转到登录页面

					Intent login_intent = new Intent(instance, LoginActivity.class);
					instance.startActivity(login_intent);
					instance.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

					break;

				case MSG_REFRESH_KEY:// 刷新key
					if (instance.refreshing) WebAPIManager.getInstance().getKey(instance.currentUser.getPhone(), 1, instance.keyHandler);
					break;
				case MSG_REQUEST_MAP:

					Intent intentPileInfo = new Intent();
					intentPileInfo.putExtra("pile", (Pile) msg.obj);
					instance.currentFragment.onActivityResult(REQUEST_MAP, Activity.RESULT_OK, intentPileInfo);
					break;
				case MSG_REFRESH_VIEW:
					if (instance.currentFragment == instance.meFragment) instance.meFragment.loadUnReadMessageNum();
					break;

			}
		}
	}
}
