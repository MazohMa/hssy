package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easy.util.AppInfo;
import com.easy.util.SPFile;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.util.FileUtils;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.util.UpdateUtil;
import com.xpg.hssy.web.WebAPI;
import com.xpg.hssychargingpole.BuildConfig;
import com.xpg.hssychargingpole.R;

/**
 * Created by black-Gizwits on 2015/09/17.
 */
public class SystemSettingActivity extends BaseActivity implements OnClickListener {

	private static final long MAX_CLICK_INTERVAL = 500;
	private static final long MAX_CLICK_TIMES = 6;

	private ImageButton btn_left;
	private RelativeLayout rl_message_notification;
	private RelativeLayout rl_clear_cache;
	private RelativeLayout rl_account_bind;
	//	private RelativeLayout rl_payway;
//	private TextView tv_payway;
	private TextView tv_cache_size;
	private ImageView iv_save_traffic_switch;
	private RelativeLayout rl_version_update;
	private TextView tv_version_name;
	private View vi_version_touch;

	private SPFile sp;
	private boolean isSaveTraffic;

	private long lastClickTime;
	private int clickTimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_system_setting);
		super.onCreate(savedInstanceState);
		sp = new SPFile(this, "config");
		isSaveTraffic = sp.getBoolean(KEY.CONFIG.SAVE_TRAFFIC, false);
		init();
	}

	private void init() {
		initView();
		initListener();
//		registerReceiver(updatePaywayCast, new IntentFilter(KEY.ACTION.ACTION_UPDATE_PAYWAY));
	}

	private void initView() {
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		TextView tv_center = (TextView) findViewById(R.id.tv_center);
		rl_message_notification = (RelativeLayout) findViewById(R.id.rl_message_notification);
		rl_clear_cache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
		tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
		rl_account_bind = (RelativeLayout) findViewById(R.id.rl_account_bind);
//		rl_payway = (RelativeLayout) findViewById(R.id.rl_payway);
//		tv_payway = (TextView) findViewById(R.id.tv_payway);
		iv_save_traffic_switch = (ImageView) findViewById(R.id.iv_save_traffic_switch);
		rl_version_update = (RelativeLayout) findViewById(R.id.rl_version_update);
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		vi_version_touch = findViewById(R.id.vi_version_touch);
		long size = ImageLoaderManager.getInstance().getCacheSize();
		tv_cache_size.setText(FileUtils.formatFileSize(size));
		tv_center.setText(R.string.system_setting);
		iv_save_traffic_switch.setSelected(isSaveTraffic);
		tv_version_name.setText(BuildConfig.VERSION_NAME);
//		String userId = sp.getString("user_id", null);
//		if (EmptyUtil.notEmpty(userId)) {
//			SPFile spFile = new SPFile(self, userId);
//			int type = spFile.getInt(KEY.INTENT.KEY_PAYWAY,     MyConstant.PAYWAY_NONE);
//			switch (type) {
//				case MyConstant.PAYWAY_BAIFUBAO:
//					tv_payway.setText(R.string.payway_baifubao);
//					break;
//				case MyConstant.PAYWAY_ALIPAY:
//					tv_payway.setText(R.string.payway_alipay);
//					break;
//				default:
//					tv_payway.setText(R.string.not_select_payway);
//					break;
//			}
//		}

	}

	private void initListener() {
		btn_left.setOnClickListener(this);
		rl_message_notification.setOnClickListener(this);
		rl_clear_cache.setOnClickListener(this);
		rl_account_bind.setOnClickListener(this);
		iv_save_traffic_switch.setOnClickListener(this);
		rl_version_update.setOnClickListener(this);
		vi_version_touch.setOnClickListener(this);
//		rl_payway.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left: {
				onBackPressed();
				break;
			}
			case R.id.rl_message_notification: {
				Intent intent = new Intent(this, MessageNotificationSettingActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.rl_clear_cache: {
				ImageLoaderManager.getInstance().clearCache();
				long size = ImageLoaderManager.getInstance().getCacheSize();
				tv_cache_size.setText(FileUtils.formatFileSize(size));
				break;
			}
			case R.id.rl_account_bind: {
				Intent intent = new Intent(this, BindAccountActivity.class);
				startActivity(intent);
				break;
			}
			case R.id.iv_save_traffic_switch: {
				isSaveTraffic = !isSaveTraffic;
				iv_save_traffic_switch.setSelected(isSaveTraffic);
				ImageLoaderManager.getInstance().setIsSaveTraffic(isSaveTraffic);
				sp.put(KEY.CONFIG.SAVE_TRAFFIC, isSaveTraffic);
				break;
			}
			case R.id.rl_version_update: {
				UpdateUtil.manualUpdate(this, new UmengUpdateListener() {
					@Override
					public void onUpdateReturned(int updateStatus, UpdateResponse updateResponse) {
						switch (updateStatus) {
							case UpdateStatus.Yes: // has update
								tv_version_name.setText(getString(R.string.system_setting_new_version) + updateResponse.version);
								break;
							case UpdateStatus.No: // has no update
								tv_version_name.setText(R.string.system_setting_version_last);
								break;
							case UpdateStatus.NoneWifi: // none wifi
								break;
							case UpdateStatus.Timeout: // time out
								break;
						}
					}
				});
				break;
			}
			case R.id.vi_version_touch: {
				long curTime = System.currentTimeMillis();

				if (curTime - lastClickTime > MAX_CLICK_INTERVAL) {
					clickTimes = 0;
				}
				clickTimes++;
				lastClickTime = curTime;

				if (clickTimes >= MAX_CLICK_TIMES) {
					StringBuilder sb = new StringBuilder();
					sb.append("内部版本号：" + AppInfo.getVersionCode(self) + "\n");
					sb.append("服务器地址：" + WebAPI.BASE_URL);
					Toast.makeText(self, sb.toString(), Toast.LENGTH_LONG).show();
					clickTimes = 0;
				}
				break;
			}
//			case R.id.rl_payway:
//				new SelectPaywayDialog(self).show();
//				break;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		try{
//			unregisterReceiver(updatePaywayCast);
//		}catch (Exception e){}
	}

//	private BroadcastReceiver updatePaywayCast = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			int payway = intent.getIntExtra(KEY.INTENT.KEY_PAYWAY, MyConstant.PAYWAY_NONE);
//			switch(payway){
//				case MyConstant.PAYWAY_BAIFUBAO:
//					tv_payway.setText(R.string.payway_baifubao);
//					break;
//				case MyConstant.PAYWAY_ALIPAY:
//					tv_payway.setText(R.string.payway_alipay);
//					break;
//				default:
//					tv_payway.setText(R.string.not_select_payway);
//					break;
//			}
//		}
//	};
}
