package com.xpg.hssy.main.activity.launch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.easy.util.BitmapUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssychargingpole.R;

public class WelcomeActivity extends BaseActivity {
	public static final String SETTING_LAUNCH_TIMES = "launch_times";
	public static final int TIME_WELCOME_DELAY = 1000;

	private SharedPreferences sp;
	private int mLaunchTimes;// 计算启动场景

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initEvent();
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		mLaunchTimes = sp.getInt(SETTING_LAUNCH_TIMES, 0);
		mLaunchTimes++;

		Editor editor = sp.edit();
		editor.putInt(SETTING_LAUNCH_TIMES, mLaunchTimes);
		editor.commit();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.welcome_activity);
	}

	@Override
	protected void initEvent() {
		handler.sendEmptyMessageDelayed(mLaunchTimes, TIME_WELCOME_DELAY);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}
			if (msg.what == 1) {
				Intent guideIntent = new Intent(WelcomeActivity.this,
						GuideActivity2.class);
				startActivity(guideIntent);
			} else {
				Intent mainIntent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(mainIntent);
			}
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapUtil.recycle((ImageView) findViewById(R.id.bg));
	}
}
