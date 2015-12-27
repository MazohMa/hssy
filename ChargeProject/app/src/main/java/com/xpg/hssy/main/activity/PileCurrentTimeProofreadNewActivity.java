package com.xpg.hssy.main.activity;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.easy.util.LogUtil;
import com.easy.util.ToastUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.dialog.DateAndTimePickDialog;
import com.xpg.hssy.dialog.DateAndTimePickDialog.OnChangedListener;
import com.xpg.hssy.dialog.DateAndTimePickDialog.OnOkListener;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.CmdManager;
import com.xpg.hssy.engine.CmdManager.OnCmdListener;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

/**
 * PileCurrentTimeProofreadNewActivity
 * 
 * @author Mazoh 2015 05 21
 * @category 电桩当前时间校对
 * @version 2.0
 * 
 */
public class PileCurrentTimeProofreadNewActivity extends BaseActivity {
	private static final int CURRENTTUMEFORPILE = 1;
	private static final String TIME_FORMAT_YMDHM = "yyyy-MM-dd HH:mm:ss";
	private DateAndTimePickDialog dateAndTimePickDialog;
	private Button automatic, unautomatic;
	private TextView current_time;
	private ImageButton btn_right;

	private String pileId;
	private boolean isConnectedThisPile;
	private LoadingDialog loadingDialog = null ;
	private Handler mHandler = new Handler();

	private Runnable acqCurTimeRunnable = new Runnable() {

		@Override
		public void run() {
			if (checkConnectState()) {
				CmdManager.getInstance().acqCurrentTimeInPile();
				mHandler.postDelayed(this, 1000);
			}
		}

	};

	private OnCmdListener onCmdListener = new OnCmdListener() {
		// 获取桩id
		protected void onAquirePileId(String pileId) {
			Log.i("tag","onAquirePileId:" +  pileId) ;
			if (pileId != null
					&& pileId
							.equals(PileCurrentTimeProofreadNewActivity.this.pileId)) {
				showConnectedPile();
			} else {
				showDisconnectedPile();
			}
		}

		// 设置桩当前时间
		protected void onSettingCurrentTimeInPile(boolean success) {
			if (success) {
				ToastUtil.show(self, "对时成功");
			} else {
				ToastUtil.show(self, "对时失败");
			}
		}

		/**
		 * 获取桩当前的时间回调
		 * 
		 * @param date
		 */
		@Override
		protected void onAquireCurrentTimeInPile(long date) {
			super.onAquireCurrentTimeInPile(date);
			Log.i("TAG", date + "");
			if (isConnectedThisPile) {
				current_time.setText(TimeUtil.format(date * 1000l,
						TIME_FORMAT_YMDHM) + "");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		pileId = getIntent().getStringExtra("pile_id");
		Log.i("tag","pileId" + pileId) ;
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.pile_current_time_proofread_new_layout);
		setTitle(R.string.pile_current_time_setting);
		dateAndTimePickDialog = new DateAndTimePickDialog(this);
		automatic = (Button) findViewById(R.id.automatic);
		unautomatic = (Button) findViewById(R.id.unantomatic);
		current_time = (TextView) findViewById(R.id.current_time);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		int keyType = -1;
		try {
			keyType = CmdManager.getInstance().getKey().getKeyType();
			Log.i("tag","keyType" + keyType) ;
		} catch (Exception e) {
		}
		// 如果正在连接某个桩并且是这个桩的主人，则获取一下桩id，确认是当前需要对时的桩
		if (CmdManager.getInstance().isValid() && keyType == Key.TYPE_OWNER) {
			Log.i("tag","CmdManager.getInstance().isValid() && keyType == Key.TYPE_OWNER") ;
			CmdManager.getInstance().aquirePileId();
		} else {
			Log.i("tag","CmdManager.getInstance().UNValid()") ;
			showDisconnectedPile();
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		automatic.setOnClickListener(this);
		unautomatic.setOnClickListener(this);
		mHandler.sendEmptyMessageDelayed(CURRENTTUMEFORPILE, 1000);
		dateAndTimePickDialog.setOnChangedListener(new OnChangedListener() {

			@Override
			public void onChanged(DateAndTimePickDialog tpd, int newYear,
					int newMonth, int newDay, int newHour, int newMinute,
					int newSecond, int oldYear, int oldMonth, int oldDay,
					int oldHour, int oldMinute, int oldSecond) {

			}
		});
		dateAndTimePickDialog.setOnCancelListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			}
		});

		dateAndTimePickDialog.setOnOkListener(new OnOkListener() {

			@Override
			public void onOk(DateAndTimePickDialog tpd, int year, int month,
					int day, int hour, int minute, int second) {
				Log.i("TAG", "YEAR:" + year + "MONTH:" + month + "DAY:" + day
						+ "HOUR:" + hour + "MINUTE:" + minute + "SECOND:"
						+ second);
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month - 1, day, hour, minute, second);

				Date date = calendar.getTime();
				if (date != null) {
					Log.i("TAG", date.toString());
					// 手动设置桩时间日期
					CmdManager.getInstance().setTimeInPile(date);
				} else {
					ToastUtil.show(self, "设置日期出错");
				}
			}
		});
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.automatic:
			if (!checkConnectState()) {
				return;
			}
			autoSetTime();
			break;
		case R.id.unantomatic:
			if (!checkConnectState()) {
				return;
			}
			dateAndTimePickDialog.show();
			break;
		default:
			break;
		}

	}

	private void autoSetTime() {
		WebAPIManager.getInstance().setCurrentTimeForPileAutomatic(
				new WebResponseHandler<String>(this) {

					@Override
					public void onStart() {
						super.onStart();
						loadingDialog = new LoadingDialog(self,R.string.loading) ;
						loadingDialog.showDialog();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
						TipsUtil.showTips(self, e);
					}

					@Override
					public void onFailure(WebResponse<String> response) {
						super.onFailure(response);
						loadingDialog.dismiss();
						TipsUtil.showTips(self, response);
					}

					@Override
					public void onSuccess(WebResponse<String> response) {
						super.onSuccess(response);
						loadingDialog.dismiss();
						String json = response.getResult();
						JsonObject jo = new JsonParser().parse(json)
								.getAsJsonObject();
						if (jo.has("curTime")) {
							long curtime = jo.get("curTime").getAsLong();
							Log.i("TAG", "curTime:" + curtime);
							Calendar currentCalendar = Calendar.getInstance();
							currentCalendar.setTimeInMillis(curtime);
							Date date = currentCalendar.getTime();
							CmdManager.getInstance().setTimeInPile(date);
						} else {
						}
					}

				});
	}

	@Override
	protected void onRightBtn(View v) {
		super.onRightBtn(v);
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
	}

	private boolean checkConnectState() {
		boolean connected = CmdManager.getInstance().isValid()
				&& isConnectedThisPile;
		LogUtil.e(tag, "isConnectedThisPile: " + isConnectedThisPile);
		if (!connected) {
			showDisconnectedPile();
			ToastUtil.show(this, "蓝牙连接已断开");
		}
		return connected;
	}

	@Override
	protected void onStart() {
		super.onStart();
		CmdManager.getInstance().addOnCmdListener(onCmdListener);
		if (isConnectedThisPile) {
			mHandler.post(acqCurTimeRunnable);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		CmdManager.getInstance().removeOnCmdListener(onCmdListener);
		mHandler.removeCallbacks(acqCurTimeRunnable);
	}

	private void showDisconnectedPile() {
		isConnectedThisPile = false;
		current_time.setText("未连接该桩");
		automatic.setEnabled(false);
		unautomatic.setEnabled(false);
		mHandler.removeCallbacks(acqCurTimeRunnable);
	}

	private void showConnectedPile() {
		isConnectedThisPile = true;
		automatic.setEnabled(true);
		unautomatic.setEnabled(true);
		mHandler.post(acqCurTimeRunnable);
	}
}
