package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.easy.manager.EasyActivityManager;
import com.easy.util.BitmapUtil;
import com.easy.util.EmptyUtil;
import com.easy.util.LogUtil;
import com.easy.util.NetWorkUtil;
import com.easy.util.ToastUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.Result;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bean.CtrlRes;
import com.xpg.hssy.bean.PileAuthentication;
import com.xpg.hssy.bean.RealTimeStatus;
import com.xpg.hssy.bt.BTChooseDialog;
import com.xpg.hssy.bt.BTConnectListener;
import com.xpg.hssy.bt.BTManager;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStatus;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.ChargeCompleteDialog;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.TimePickDialog;
import com.xpg.hssy.dialog.TimePickDialog.OnOkListener;
import com.xpg.hssy.dialog.WaterBlueDialogBluFailed;
import com.xpg.hssy.dialog.WaterBlueDialogChargeForOneSide;
import com.xpg.hssy.dialog.WaterBlueDialogChargeForTwoSide;
import com.xpg.hssy.engine.CmdManager;
import com.xpg.hssy.engine.CmdManager.OnCmdListener;
import com.xpg.hssy.main.activity.ChargeCompleteActivity;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.main.activity.MyOrderDetailActivity;
import com.xpg.hssy.main.fragment.callbackinterface.GprsBleChargeOperater;
import com.xpg.hssy.service.ChargeRecordSyncService;
import com.xpg.hssy.util.GsonUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssy.zxing.ZxingManager;
import com.xpg.hssy.zxing.camera.CameraManager;
import com.xpg.hssy.zxing.view.ViewfinderView;
import com.xpg.hssychargingpole.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class ChargeFragment extends BaseFragment implements OnClickListener, GprsBleChargeOperater {
	private static final String TAG = "ChargeFragment";
	//充电完成回调的requestCode
	private static final int TOCHARGECOMPLETE = 1;

	//充电界面的显示
	//1 连接
	//2 连接中
	//3 充电枪界面
	//4 开始充电
	//5 充电中
	//6 登陆
	//7 扫码

	private static final int STATE_CONNECT_BT = 1;
	private static final int STATE_CONNECTING_BT = 2;
	private static final int STATE_CONNECT_GUN = 3;
	private static final int STATE_START_CHARGE = 4;
	private static final int STATE_CHARGING = 5;
	public static final int MSG_GO_LOGIN = 6;
	public static final int MSG_PREPARE_CHARGING = 7;
	public static final int MSG_AFTER_CHARGING = 8;

	public static final int STATE_SHOWQR = -1;
	// errcode 为-2；
	// 4500；4506 时需要继续轮询,
	// 0就去拿充电状态
	public static final int ERRORCODE_ZERO = 0;
	public static final int ERRORCODE_TWO = -2;
	public static final int ERRORCODE_SUCCESS = 4800;
	public static final int ERRORCODE_THIRD = 4500;
	public static final int ERRORCODE_FOURTH = 4506;
	public static final int ERRORCODE_OUTOFELECTRI = 4900;
	public static final int ERRORCODE_UNOPEN = 4505;
	public static final int ERRORCODE_HADOPEN = 4501;//已经开机
	public static final int ERRORCODE_UNWAITINGSTATUS = 4502;//不是待机状态
	public static final int ERRORCODE_GUNUNLINK = 4503;//枪未连接

	public static final int FOUREIGHTERRORCODE_HADOPEN = 4801;//绝缘检测失败
	public static final int FOUREIGHTERRORCODE_UNWAITINGSTATUS = 4802;//未收到BMS充电需求
	public static final int FOUREIGHTERRORCODE_GUNUNLINK = 4803;//充电机开机失败

	//303004 正在充电中
	//303005 正在等待控制结果(需要轮询)
	public static final String PILECHARGINGSTATUS = "303004";
	public static final String PILEWAITINGFORRESULT = "303005";


	// 轮询最大次数
	public static final int RETRYTIMESNUM = 20;


	// 0表示未上线 ，
	// 1表示空闲桩，
	// 2表示只连接未充电，
	// 3表示充电中，
	// 4表示GPRS通讯中断，
	// 5表示检查中，
	// 6：表示预约，
	// 7表示故障,
	// 8 表示充电完成

	private static final int STATE_PILE_UNDISPLAY = 0;
	private static final int STATE_PILE_FREE = 1;
	private static final int STATE_PILE_LINK_UNCHARGE = 2;
	private static final int STATE_PILE_CHARGING = 3;
	private static final int STATE_PILE_DISTURB = 4;
	private static final int STATE_PILE_CHECKING = 5;
	public static final int STATE_PILE_APPOINT = 6;
	public static final int STATE_PILE_ACCIDENT = 7;
	public static final int STATE_PILE_CHARGINGCOMPLETE = 8;


	//充电类型
	//1交流
	//2直流
	private static final int INTERTYPE_AC = 1;
	private static final int INTERTYPE_DC = 2;
	//充电行为
	//1 开始充电
	//2 结束充电
	private static final int START_CHARGE_ACTION = 1;
	private static final int END_CHARGE_ACTION = 2;
	//桩类型
	// 0表示gprs桩，
	// 1表示gprs+蓝牙桩，
	// 2表示纯蓝牙桩
	public int pileType = -1;
	public static final int PILETYPE_FOR_GPRS = 0;//GPRS桩
	public static final int PILETYPE_FOR_GPRSBLE = 1;//GPRS+BLE桩
	public static final int PILETYPE_FOR_BLE = 2;//BLE桩

    private static int CONTROLLERACTION = -1 ;
	private ImageButton ib_alarm, ib_connect, ib_connect_low, ib_menu;
	private RelativeLayout rl_connect, rl_charge;
	private LinearLayout ll_output_info, ll_output_power;

	private ImageView iv_bg, iv_electric, iv_electric_high;
	private Button btn_control_charge, btn_change_bt;
	private LinearLayout ll_charge_time_count, ll_charge_soc_or_ele_count, ll_charge_status, ll_tv_soc_or_ele;

	private TextView tv_center, tv_output_voltage, tv_output_current, tv_output_power, tv_w_unit, tv_output_type, tv_time_hour, tv_time_minutes, tv_error,
			tv_soc_or_ele, tv_charging_type_tip, tv_soc_or_ele_unit, tv_charge_status;
	private TextView tv_connect_tips1, tv_connect_tips2, tv_connect_tips3;
	private SharedPreferences sp;
	private Timer refreshTimer;

	private String currentPileId;
	private BTChooseDialog mBTChooseDialog;
	private TimePickDialog timePickDialog;


	private int state;
	private byte alarmByte;

	private Animation chargeRotateAnim;
	private Animation chargeShowAnim;
	private Animation chargeHideAnim;
	private Animation connectAnim;
	private RelativeLayout rl_qrcode, rl_scan_or_ble_charge;


	private SurfaceView preview_view;
	private ViewfinderView viewfinderview;

	private ToggleButton switch_onoff;
	private RadioGroup rg_scan_ble_tab;


	private ZxingManager mZxingManager;
	private String user_id;
	private String deviceId = null;//出厂编码
	private String pileCode = null;//运行编码
	private String code = null;
	private int status = -1;
	private LoadingDialog loadingDialog = null;//加载框
	private String ownerId = null;
	private String orderId = null;
	private ConnectionChangeReceiver myReceiver = null;//网络监听
	private ChargeCompleteDialog chargeCompleteDialog = null;
	private boolean isChargingStatus = false;//s是否在充电中
	private boolean checkIfCharging = false;//是否已经检查充电
	private boolean ownerListenTenantIfCharging = false;//桩主监听租户是否在充电
	private boolean chargingError = false;
	private boolean bleorgprs = true;
	private boolean ifFinishCtrRes = false;//是否完成轮询
	private boolean ifGetCurrentStatus = false;//当前状态
	private boolean ifOofLine = false;//桩断电监听
	private boolean ifFromBleChargingDisconnectBack = false;//是否是从蓝牙充电中断开蓝牙返回
	private RadioButton ib_scan_qr_charge;
	private RadioButton ib_ble_charge;
	private SurfaceHolder surfaceHolder;
	private int retryTimes = 1;//轮询开始数
	private Timer mTimer = new Timer();// 定时器 ,充电完成 ;

	public SurfaceView getPreview_view() {
		return preview_view;
	}

	public void setPreview_view(SurfaceView preview_view) {
		this.preview_view = preview_view;
	}

	public ZxingManager getmZxingManager() {
		return mZxingManager;
	}

	public void setmZxingManager(ZxingManager mZxingManager) {
		this.mZxingManager = mZxingManager;
	}


	public ToggleButton getSwitch_onoff() {
		return switch_onoff;
	}

	public void setSwitch_onoff(ToggleButton switch_onoff) {
		this.switch_onoff = switch_onoff;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_GO_LOGIN:// 转到登录页面

					Intent login_intent = new Intent(getActivity(), LoginActivity.class);
					startActivity(login_intent);
					getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

					break;

				case 1:
					if (chargeCompleteDialog != null && chargeCompleteDialog.isShowing()) {
						chargeCompleteDialog.dismiss();
					}
					jumpIntoActivity();
					break;
				case 2:

					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
					}

			}
		}
	};
	private Runnable currentStatusRunnable = new Runnable() {

		@Override
		public void run() {
			if (isChargingStatus && isLogin()) {
				if (pileCode != null) {
					Log.i("tag", "currentStatusRunnable run");
					getchargingStatus(pileCode);
				}
			}
		}
	};
	private Runnable ctr_resRunnable = new Runnable() {

		@Override
		public void run() {
			if (!ifFinishCtrRes && isLogin()) {
				if (pileCode != null && ChargeFragment.this.retryTimes <= RETRYTIMESNUM) {
					ctrlRes(pileCode, user_id, ChargeFragment.this.retryTimes, isChargingStatus);
				}
			}
		}
	};

	private Runnable tryAutoConnectRunnable = new Runnable() {
		@Override
		public void run() {
			if (!BTManager.getInstance().isConnecting() && !BTManager.getInstance().isConnected() &&
					isLogin()) {
				mBTChooseDialog.tryAutoConnect();
			}
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.i("tag", "setUserVisibleHint");
		/*if (!isVisibleToUser) {
			if (mZxingManager != null) {
				mZxingManager.onPause();
			}
		}
		if (isVisibleToUser) {
			if (mZxingManager != null && rl_qrcode.getVisibility() == View.VISIBLE) {
				mZxingManager.onResume();
			}
		} else {
			mHandler.removeCallbacks(tryAutoConnectRunnable);
			if (switch_onoff != null && CameraManager.get() != null) {
				switch_onoff.setChecked(false);
			}
			if (mZxingManager != null) mZxingManager.onPause();
		}
		if (NetWorkUtil.isNetworkConnected(getActivity())) {
			//当还没有检查,界面可见,已经登录不在充电页面
			if (isVisibleToUser && getActivity() != null && !checkIfCharging && !isChargingStatus &&
					isLogin() && state != STATE_CHARGING &&
					state != STATE_START_CHARGE) {//检查是否充电状态
				Log.i("tag", "当还没有检查界面可见已经登录不在充电页面");
				user_id = sp.getString("user_id", null);
				removeCallbackThread(currentStatusRunnable);
				checkIfCharging(user_id);
			} else {
				if (isChargingStatus && state == STATE_CHARGING && !isLogin()) {
					checkIfCharging = false;
					isChargingStatus = false;
					removeCallbackThread(currentStatusRunnable);
					showRl_qrcode();
					int id = rg_scan_ble_tab.getCheckedRadioButtonId();
					setRadioButtonCheckToQr(id);
					setParamNull();
					if (mZxingManager != null) {
						mZxingManager.onResume();
					}
				} else {
				}
			}
		} else if (!NetWorkUtil.isNetworkConnected(getActivity()) && isVisibleToUser && !checkIfCharging && !isChargingStatus &&
				isLogin() && state != STATE_CHARGING &&
				state != STATE_START_CHARGE) {
			if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
				btn_change_bt.setActivated(true);
				if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
					refreshStatus();
				} else {
					CmdManager.getInstance().aquirePileId();//鉴权失败，重新获取桩Id去鉴权
				}
			} else {
				*//**当切换到蓝牙充电页面时，才去尝试自动连接上一次蓝牙桩 *//*
				if (getUserVisibleHint()) {
					mHandler.removeCallbacks(tryAutoConnectRunnable);
					mHandler.postDelayed(tryAutoConnectRunnable, 1000);
				}
			}
		}*/
	}

	public ChargeFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CmdManager.getInstance().addOnCmdListener(onCmdListener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initData();
		Log.i("tag", "chargingFragment  onCreateView");
		View v = initView(inflater);
		initListener();
		hideRl_qrcode();
		showConnectBT();
		return v;
	}

	private void initData() {
		sp = MainActivity.instance.getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", null);

		if (mBTChooseDialog != null) {
			mBTChooseDialog.unregisterReceiver();
		}
		mBTChooseDialog = new BTChooseDialog(getActivity());
		timePickDialog = new TimePickDialog(getActivity());
		timePickDialog.setTitle("启动时间");
		timePickDialog.setOnOkListener(new OnOkListener() {
			@Override
			public void onOk(TimePickDialog tpd, int hour, int min) {
				// 设置时间
				Calendar delayC = Calendar.getInstance();
				delayC.set(Calendar.HOUR_OF_DAY, hour);
				delayC.set(Calendar.MINUTE, min);
				while (delayC.before(Calendar.getInstance())) {
					ToastUtil.show(MainActivity.instance, "设置时间已过，将延时至明天同样时间");
					delayC.add(Calendar.DAY_OF_YEAR, 1);
				}
				delayC.set(Calendar.SECOND, 0);
				// 发送命令
				CmdManager.getInstance().delayCharge(delayC.getTime());
				//刷新状态
				refreshStatus();
			}
		});
		timePickDialog.setOnCancelListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CmdManager.getInstance().stopCharge();
			}
		});
		initAnimation();
	}

	private void initAnimation() {
		// 连接动画
		connectAnim = new AlphaAnimation(0f, 1f);
		connectAnim.setRepeatMode(Animation.REVERSE);
		connectAnim.setRepeatCount(-1);
		connectAnim.setDuration(500);

		// 充电旋转动画
		chargeRotateAnim = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		chargeRotateAnim.setInterpolator(new LinearInterpolator());
		chargeRotateAnim.setRepeatCount(-1);
		chargeRotateAnim.setDuration(2000);

		// 充电渐显动画
		chargeShowAnim = new AlphaAnimation(1f, 0f);
		chargeShowAnim.setFillAfter(true);
		chargeShowAnim.setDuration(500);

		// 充电渐隐动画
		chargeHideAnim = new AlphaAnimation(0f, 1f);
		chargeHideAnim.setFillAfter(true);
		chargeHideAnim.setDuration(500);
	}

	/**
	 * 检查GPRS充电桩设备状态
	 */

	private void checkIfCharging(final String userid) {
		WebAPIManager.getInstance().checkIfCharging(userid, new WebResponseHandler<Object>() {
			@Override
			public void onStart() {
				super.onStart();
				if (rl_qrcode.getVisibility() == View.VISIBLE) {//检查二维码页面
					mZxingManager.onPause();
				}
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (getActivity() != null) {
					loadingDialog = new LoadingDialog(getActivity(), R.string.loading);
					loadingDialog.showDialog();
				}

			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				checkIfCharging = true;
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (response == null) {
					return;
				}
				String json = response.getResult();
				JsonObject jo = null;
				try {
					jo = new JsonParser().parse(json).getAsJsonObject();
				} catch (Exception e) {
					e.printStackTrace();
					if (rl_qrcode.getVisibility() == View.VISIBLE) {//检查二维码页面
						mZxingManager.onResume();
					}
					return;
				}
				if (jo.has(KEY.JSON.JSONPILECODE)) {
					pileCode = null;
					pileCode = jo.get(KEY.JSON.JSONPILECODE).getAsString() + "";
				}
				if (jo.has(KEY.JSON.JSONORDERID)) {
					orderId = null;
					orderId = jo.get(KEY.JSON.JSONORDERID).getAsString() + "";
				}
				if (jo.has(KEY.JSON.JSONOWNERID)) {
					ownerId = null;
					ownerId = jo.get(KEY.JSON.JSONOWNERID).getAsString() + "";
					if (ownerId == null | ownerId.equals("")) {
						sp.edit().putBoolean("isOwner", false).commit();
					} else {
						if (ownerId.equals(user_id)) {
							sp.edit().putBoolean("isOwner", true).commit();
						} else {
							sp.edit().putBoolean("isOwner", false).commit();
						}
					}
				} else {
					sp.edit().putBoolean("isOwner", false).commit();
				}
				String code = response.getCode();
				//303004 正在充电中 303005 正在等待控制结果(需要轮询)
				if (code != null) {
					if (code.equals(PILECHARGINGSTATUS)) {
						Log.i("tag", "code.equals(\"303004\"))");
						checkIfCharging = true;//已经检查
						//正在充电
						if (pileCode != null && !pileCode.equals("") &&
								orderId != null && !orderId.equals("") &&
								ownerId != null && !ownerId.equals("")) {
							isChargingStatus = true;
							removeCallbackThread(currentStatusRunnable);
							showPrepareCharging(false, R.string.message_charging_getting_tip,
									R.string.power, R.string.message_charging_pile_type_tip);
							showCharging(true);
							ifGetCurrentStatus = false;
							mHandler.postDelayed(currentStatusRunnable, 500);//获取桩的实时状态信息
						} else {
							if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
								Log.i("tag", "BTManager.getInstance().isConnected()");
								btn_change_bt.setActivated(true);
								if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
									refreshStatus();
								} else {
									CmdManager.getInstance().aquirePileId();//获得桩ID，然后鉴权
								}
							} else {
								if (getUserVisibleHint()) {
									Log.i("tag", "BTManager.getInstance().disConnected()");
									mHandler.removeCallbacks(tryAutoConnectRunnable);
									mHandler.postDelayed(tryAutoConnectRunnable, 1000);
								}
							}
						}
					}
					else if (code.equals(PILEWAITINGFORRESULT)) {
						checkIfCharging = true;//已经检查
						isChargingStatus = true;
						mHandler.removeCallbacks(ctr_resRunnable);
						ifFinishCtrRes = false;//还没有完成轮询
						showPrepareCharging(false, R.string.message_charging_getting_tip,
								R.string.power, R.string.message_charging_pile_type_tip);
						showCharging(true);
						mHandler.postDelayed(ctr_resRunnable, 200);
					}
				}
				if (rl_qrcode.getVisibility() == View.VISIBLE) {//检查二维码页面
					mZxingManager.onResume();
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				checkIfCharging = false;//检查失败
				loadingDialog.dismiss();
				if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
					btn_change_bt.setActivated(true);
					if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
						refreshStatus();
					} else {
						CmdManager.getInstance().aquirePileId();
					}
				} else {
					if (getUserVisibleHint()) {
						mHandler.removeCallbacks(tryAutoConnectRunnable);
						mHandler.postDelayed(tryAutoConnectRunnable, 1000);
					}
				}
				if (rl_qrcode.getVisibility() == View.VISIBLE) {//检查二维码页面
					mZxingManager.onResume();
				}
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				checkIfCharging = true;//已经检查
				loadingDialog.dismiss();
				if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
					btn_change_bt.setActivated(true);
					if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
						refreshStatus();
					} else {
						CmdManager.getInstance().aquirePileId();
					}
				} else {
					if (getUserVisibleHint()) {
						mHandler.removeCallbacks(tryAutoConnectRunnable);
						mHandler.postDelayed(tryAutoConnectRunnable, 1000);
					}
				}
				if (rl_qrcode.getVisibility() == View.VISIBLE) {//检查二维码页面
					mZxingManager.onResume();
				}
			}
		});
	}

	private View initView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.charge_fragment, null);
		tv_center = (TextView) v.findViewById(R.id.tv_center);
		iv_bg = (ImageView) v.findViewById(R.id.iv_bg);
		ib_menu = (ImageButton) v.findViewById(R.id.btn_left);
		ib_alarm = (ImageButton) v.findViewById(R.id.btn_right);
		ib_alarm.setVisibility(View.INVISIBLE);
		rl_connect = (RelativeLayout) v.findViewById(R.id.rl_connect);
		tv_connect_tips1 = (TextView) v.findViewById(R.id.tv_connect_tips1);
		tv_connect_tips2 = (TextView) v.findViewById(R.id.tv_connect_tips2);
		tv_connect_tips3 = (TextView) v.findViewById(R.id.tv_connect_tips3);
		ib_connect_low = (ImageButton) v.findViewById(R.id.ib_connect_low);
		ib_connect = (ImageButton) v.findViewById(R.id.ib_connect);
		rl_charge = (RelativeLayout) v.findViewById(R.id.rl_charge);
		ll_charge_time_count = (LinearLayout) v.findViewById(R.id.ll_charge_time_count);
		ll_charge_soc_or_ele_count = (LinearLayout) v.findViewById(R.id.ll_charge_soc_or_ele_count);

		ll_output_power = (LinearLayout) v.findViewById(R.id.ll_output_power);
		ll_charge_time_count.setVisibility(View.GONE);
		ll_charge_soc_or_ele_count.setVisibility(View.GONE);

		tv_soc_or_ele = (TextView) v.findViewById(R.id.tv_soc_or_ele);
		tv_charging_type_tip = (TextView) v.findViewById(R.id.tv_charging_type_tip);
		tv_soc_or_ele_unit = (TextView) v.findViewById(R.id.tv_soc_or_ele_unit);
		ll_tv_soc_or_ele = (LinearLayout) v.findViewById(R.id.ll_tv_soc_or_ele);
		ll_charge_status = (LinearLayout) v.findViewById(R.id.ll_charge_status);
		tv_charge_status = (TextView) v.findViewById(R.id.tv_charge_status);
		ll_output_info = (LinearLayout) v.findViewById(R.id.ll_output_info);
		tv_output_current = (TextView) v.findViewById(R.id.tv_output_current);
		tv_output_voltage = (TextView) v.findViewById(R.id.tv_output_voltage);
		tv_output_power = (TextView) v.findViewById(R.id.tv_output_power);
		tv_output_type = (TextView) v.findViewById(R.id.tv_output_type);
		tv_w_unit = (TextView) v.findViewById(R.id.tv_w_unit);
		btn_change_bt = (Button) v.findViewById(R.id.btn_change_bt);
		iv_electric = (ImageView) v.findViewById(R.id.iv_electric);
		iv_electric_high = (ImageView) v.findViewById(R.id.iv_electric_high);
		tv_time_hour = (TextView) v.findViewById(R.id.tv_time_hour);
		tv_time_minutes = (TextView) v.findViewById(R.id.tv_time_minutes);
		btn_control_charge = (Button) v.findViewById(R.id.btn_control_charge);

		tv_error = (TextView) v.findViewById(R.id.tv_error);
		tv_center.setText(R.string.app_title);

		rl_qrcode = (RelativeLayout) v.findViewById(R.id.rl_qrcode);
		preview_view = (SurfaceView) v.findViewById(R.id.preview_view);
		rl_scan_or_ble_charge = (RelativeLayout) v.findViewById(R.id.rl_scan_or_ble_charge);
		switch_onoff = (ToggleButton) v.findViewById(R.id.switch_onoff);
		surfaceHolder = preview_view.getHolder();
		viewfinderview = (ViewfinderView) v.findViewById(R.id.viewfinderview);
		rg_scan_ble_tab = (RadioGroup) v.findViewById(R.id.rg_scan_ble_tab);
		ib_scan_qr_charge = (RadioButton) v.findViewById(R.id.ib_scan_qr_charge);
		ib_ble_charge = (RadioButton) v.findViewById(R.id.ib_ble_charge);

		mZxingManager = new ZxingManager(getActivity(), viewfinderview, preview_view);
		mZxingManager.onCreate();
		viewfinderview.setType(ViewfinderView.TYPE_2D_CODE);
		viewfinderview.setTips(null);
		return v;
	}

	//隐藏扫描二维码界面
	public void hideRl_qrcode() {
		rl_connect.setVisibility(View.VISIBLE);
		rl_scan_or_ble_charge.setVisibility(View.VISIBLE);
		rl_qrcode.setVisibility(View.GONE);
		iv_bg.setVisibility(View.VISIBLE);
		preview_view.setVisibility(View.GONE);
		hideLightView(switch_onoff);
		mZxingManager.onPause();
	}

	//打开扫描二维码界面
	public void showRl_qrcode() {
		if (state == STATE_SHOWQR) {
			return;
		}
		state = STATE_SHOWQR;
		ib_alarm.setVisibility(View.INVISIBLE);
		iv_bg.setVisibility(View.GONE);
		rl_connect.setVisibility(View.GONE);
		rl_charge.setVisibility(View.GONE);
		tv_error.setVisibility(View.GONE);
		rl_qrcode.setVisibility(View.VISIBLE);
		rl_scan_or_ble_charge.setVisibility(View.VISIBLE);
		preview_view.setVisibility(View.VISIBLE);
		showLightView(switch_onoff);
		mZxingManager.onResume();

	}

	//实例化弹出框
	public void initWaterBlueDialog(int titleId, int contentId, Integer leftTextId, Integer rightTextId, Boolean isGprsAndBle, Boolean isOwner, Boolean
			isTwoSide, Boolean contetnCanClick) {
		if (isTwoSide) {
			WaterBlueDialogChargeForTwoSide waterBlueDialogChargeForTwoSide = new WaterBlueDialogChargeForTwoSide(ChargeFragment.this, getActivity(),
					isGprsAndBle, isOwner);
			if (isOwner) {
				waterBlueDialogChargeForTwoSide.setContentStr(contentId);
				waterBlueDialogChargeForTwoSide.setRightBtnTextStr(rightTextId);
				waterBlueDialogChargeForTwoSide.setLeftBtnTextStr(leftTextId);

			} else {
				waterBlueDialogChargeForTwoSide.setTitleStr(titleId);
				waterBlueDialogChargeForTwoSide.setContentStr(contentId);
				waterBlueDialogChargeForTwoSide.setLeftBtnTextStr(leftTextId);
				waterBlueDialogChargeForTwoSide.setRightBtnTextStr(rightTextId);
			}
			waterBlueDialogChargeForTwoSide.show();
		} else {
			WaterBlueDialogChargeForOneSide waterBlueDialogChargeForOneSide = new WaterBlueDialogChargeForOneSide(ChargeFragment.this, getActivity());
			waterBlueDialogChargeForOneSide.setContentCanClick(contetnCanClick);
			waterBlueDialogChargeForOneSide.setTitleStr(titleId);
			waterBlueDialogChargeForOneSide.setContentStr(contentId);
			waterBlueDialogChargeForOneSide.setRightBtnTextStr(rightTextId);
			waterBlueDialogChargeForOneSide.show();
		}
	}

	//实例化弹出框
	public void initWaterBlueDialog(int titleId, String ownerName, String pileName, float price, String deviceName, Integer leftTextId, Integer rightTextId,
	                                Boolean isGprsAndBle, Boolean isOwner, Boolean isTwoSide) {
		if (isTwoSide) {
			WaterBlueDialogChargeForTwoSide waterBlueDialogChargeForTwoSide = new WaterBlueDialogChargeForTwoSide(ChargeFragment.this, getActivity(),
					isGprsAndBle, isOwner);
			waterBlueDialogChargeForTwoSide.setTitleStr(titleId);
			waterBlueDialogChargeForTwoSide.setLeftBtnTextStr(leftTextId);
			waterBlueDialogChargeForTwoSide.setRightBtnTextStr(rightTextId);
			waterBlueDialogChargeForTwoSide.setOwnerName(ownerName);
			waterBlueDialogChargeForTwoSide.setPileName(pileName);
			waterBlueDialogChargeForTwoSide.setPrice("￥" + String.format("%.2f", price) + "/kWh");
			waterBlueDialogChargeForTwoSide.show();
		}
	}

	private void initListener() {
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter(KEY.INTENT.ACTIONFORIGNORE));
		registerReceiver();//网络实时监听
		ib_alarm.setOnClickListener(this);
		ib_connect.setOnClickListener(this);
		btn_change_bt.setOnClickListener(this);
		btn_control_charge.setOnClickListener(this);
		switch_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				controlLight(switch_onoff);
			}
		});
		rg_scan_ble_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				switch (i) {
					case R.id.ib_scan_qr_charge:
						showRl_qrcode();
						if (BTManager.getInstance().isConnected() && mZxingManager != null) {//检查蓝牙是否连接,如果连接，则暂停二维码扫码
							mZxingManager.onPause();
						} else {
						}
						break;
					case R.id.ib_ble_charge:
						pileType = PILETYPE_FOR_BLE;//纯蓝牙桩
						hideRl_qrcode();
						showConnectBT();
						break;
				}
			}
		});
		mZxingManager.setOnResultListener(new ZxingManager.OnResultListener() {
			@Override
			public void OnResult(Result result, Bitmap barcode) {
				mZxingManager.onPause();
				switch_onoff.setChecked(false);//关闭闪关灯
				String code = result.getText().trim();//16位运行编码
				if (code == null || code.equals("")) {
					ToastUtil.show(getActivity(), R.string.format_error);
					mZxingManager.onResume();
					return;
				}
				if (NetWorkUtil.isNetworkConnected(getActivity())) {
					if (isLogin()) {
						// 定义接口，userid,扫码返回的id,优易充后台验证用户，充电桩信息访问后台操作
						user_id = sp.getString("user_id", null);
						scanInfo(user_id, code);
						return;
					} else {
						mHandler.sendEmptyMessage(MSG_GO_LOGIN);
					}
				} else {//没有网络返回扫码
					mZxingManager.onResume();
				}
			}
		});
		mBTChooseDialog.setOnConnectLinstener(new BTConnectListener() {
			@Override
			public void onDisconnected() {
				if (btn_change_bt != null) {
					stopRefreshStatus();
					btn_change_bt.setActivated(false);
					showConnectBT();
					ifFromBleChargingDisconnectBack = true;
					registerReceiver();
					currentPileId = null;
					CmdManager.getInstance().setKey(null);
				}
			}

			@Override
			public void onConnecting() {
				Log.i("tag", "onConnecting");
				showConnectingBT();
			}

			@Override
			public void onConnected() {
				Log.i("tag", "onConnected");
				alarmByte = 0;
				if (btn_change_bt != null) {
					btn_change_bt.setActivated(true);
					CmdManager.getInstance().aquirePileId();
				}
			}

			@Override
			public void onConnectTimeOut() {
				Log.i("tag", "onConnectTimeOut");
				if (btn_change_bt != null) {
					btn_change_bt.setActivated(false);
					showConnectBT();
					initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_link_ble_out_time_tip, null, R.string.message_confirm_tip, null,
							null, false, false);
				}
			}

			@Override
			public void onConnectFailed() {
				Log.i("tag", "onConnectFailed");
				if (btn_change_bt != null) {
					btn_change_bt.setActivated(false);
					showConnectBT();
					WaterBlueDialogBluFailed waterBlueDialogBluFailed = new WaterBlueDialogBluFailed(getActivity());
					waterBlueDialogBluFailed.setContent(R.string.bt_conn_fail);
					waterBlueDialogBluFailed.setTitle(R.string.message_ble_match_fail_tip);
					waterBlueDialogBluFailed.show();
				}
			}
		});
		mBTChooseDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				ib_connect.setVisibility(View.VISIBLE);
			}
		});
	}

	//扫码后台授权
	public void scanInfo(final String userid, String qrcode) {
		LogUtil.e("scanInfo", "start");
		WebAPIManager.getInstance().scanInfo(userid, qrcode, new WebResponseHandler<PileAuthentication>() {
			@Override
			public void onStart() {
				super.onStart();
				if (!getUserVisibleHint()) {
					setParamNull();
					return;
				} else {
					if (loadingDialog != null && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}
					loadingDialog = new LoadingDialog(getActivity(), R.string.loading);
					loadingDialog.showDialog();
				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				LogUtil.e("scanInfo", "onError");
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (getUserVisibleHint()) {
					if (mZxingManager != null) {
						mZxingManager.onResume();
					}
				} else {
					setParamNull();
				}
				TipsUtil.showTips(getActivity(), e);
			}

			@Override
			public void onFailure(WebResponse<PileAuthentication> response) {
				super.onFailure(response);
				LogUtil.e("scanInfo", "onFailure");
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (!getUserVisibleHint()) {
					setParamNull();
					return;
				} else {
					code = response.getCode();

					if (code.equals(WebResponse.CODE_INVALID_UNOPEN)) {
						ToastUtil.show(getActivity(), response.getMessage() + "");
						mZxingManager.onResume();
						return;
					}
					if (code.equals(WebResponse.CODE_INVALID_UNLINE)) {
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_unrequest_scan_tip, null, R.string.message_confirm_tip,
								null, null, false, false);
						return;
					}
					if (code.equals(WebResponse.CODE_INVALID_BLE)) {
						pileType = PILETYPE_FOR_BLE;//蓝牙桩
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_unsupport_scan_tip, R.string.message_cancel_tip, R.string
								.message_ble_link_tip, true, true, true, false);
						return;
					}
					if (code.equals(WebResponse.CODE_PILE_UNDISPLAY)) {//桩未发布
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_undisplay_scan_tip,
								null, R.string.message_confirm_tip,
								null, null, false, false);
						return;
					}
					//303006,"您正在充电，请勿扫描其他充电桩")
					if (code.equals(WebResponse.CODE_CHARGING_FOR_OTHER)) {
						pileType = -1;
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_charging_for_other_pile_tip,
								R.string.message_cancel_tip, R.string
										.message_confirm_tip, true, true, true, false);

						return;
					}
					//303004 正在充电中 303005 正在等待控制结果(需要轮休)
					String json = response.getResult();
					JsonObject jo = null;
					try {
						jo = new JsonParser().parse(json).getAsJsonObject();
					} catch (Exception e) {
						e.printStackTrace();
						mZxingManager.onResume();
						return;
					}
					if (jo.has(KEY.JSON.JSONPILECODE)) {
						pileCode = null;
						pileCode = jo.get(KEY.JSON.JSONPILECODE).getAsString() + "";
					}
					if (jo.has(KEY.JSON.JSONORDERID)) {
						orderId = null;
						orderId = jo.get(KEY.JSON.JSONORDERID).getAsString() + "";
					}
					if (jo.has(KEY.JSON.JSONOWNERID)) {
						ownerId = null;
						ownerId = jo.get(KEY.JSON.JSONOWNERID).getAsString() + "";
						if (ownerId == null | ownerId.equals("")) {
							sp.edit().putBoolean("isOwner", false).commit();
						} else {
							if (ownerId.equals(user_id)) {
								sp.edit().putBoolean("isOwner", true).commit();
							} else {
								sp.edit().putBoolean("isOwner", false).commit();
							}
						}
					} else {
						sp.edit().putBoolean("isOwner", false).commit();
					}
					if (code.equals(WebResponse.CODE_SCAN_HAD_UNPAID)) {
						ToastUtil.show(getActivity(), response.getMessage() + "");
						if (orderId != null) {
							Intent intentToMyOrderDetails = new Intent(getActivity(), MyOrderDetailActivity.class);
							intentToMyOrderDetails.putExtra(KEY.INTENT.ORDER_ID, orderId);
							getActivity().startActivity(intentToMyOrderDetails);
						}
						return;
					}
					if (code.equals(PILECHARGINGSTATUS)) {
						//正在充电
						if (pileCode != null && !pileCode.equals("") &&
								orderId != null && !orderId.equals("") &&
								ownerId != null && !ownerId.equals("")) {
							isChargingStatus = true;
							removeCallbackThread(currentStatusRunnable);
							showPrepareCharging(false, R.string.message_charging_getting_tip,
									R.string.power, R.string.message_charging_pile_type_tip);
							showCharging(true);
							ifGetCurrentStatus = false;
							mHandler.postDelayed(currentStatusRunnable, 1000);//获取桩的实时状态信息
						} else {
							if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
								Log.i("tag", "BTManager.getInstance().isConnected()");
								btn_change_bt.setActivated(true);
								if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
									refreshStatus();
								} else {
									CmdManager.getInstance().aquirePileId();//获得桩ID，然后鉴权
								}
							} else {
								/**当切换到蓝牙充电页面时，才去尝试自动连接上一次蓝牙桩 */
								if (getUserVisibleHint()) {
									Log.i("tag", "BTManager.getInstance().disConnected()");
									mHandler.removeCallbacks(tryAutoConnectRunnable);
									mHandler.postDelayed(tryAutoConnectRunnable, 1000);
								}
							}
						}
						return;
					}
					if (code.equals(PILEWAITINGFORRESULT)) {
						showPrepareCharging(false, R.string.message_charging_getting_tip,
								R.string.power, R.string.message_charging_pile_type_tip);
						showCharging(true);
						isChargingStatus = true;
						mHandler.removeCallbacks(ctr_resRunnable);
						ifFinishCtrRes = false;
						mHandler.postDelayed(ctr_resRunnable, 200);
						return;
					}
					TipsUtil.showTips(getActivity(), response);
				}
			}

			@Override
			public void onSuccess(WebResponse<PileAuthentication> response) {
				super.onSuccess(response);
				LogUtil.e("scanInfo", "onSuccess");
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				if (!getUserVisibleHint()) {
					return;
				}
				checkIfCharging = true;
				PileAuthentication pileAuthentication = response.getResultObj();
				if (pileAuthentication == null) {
					if (!getUserVisibleHint()) {
						return;
					} else {
						if (mZxingManager != null) {
							mZxingManager.onResume();
						}
						return;
					}
				}
				boolean isOwner = false;
				boolean isGprs_ble = false;
				User user = DbHelper.getInstance(getActivity()).getUserDao().load(user_id);
				String userName = user.getName();//用户名字
				pileType = pileAuthentication.getModuleType();//桩类型
				String ownerName = pileAuthentication.getOwnerName() + "";//业主名字
				status = pileAuthentication.getStatus();//状态
				deviceId = pileAuthentication.getDeviceId();//出厂编码
				pileCode = pileAuthentication.getPileCode();//运行编码
				String pileName = pileAuthentication.getPileName();//桩名字
				ownerId = pileAuthentication.getOwnerId();
				float price = pileAuthentication.getPrice();//价格
				String priceStr = String.format("%.2f", price);
				int pileSort = pileAuthentication.getPileType();//2为交流，1为直流
				if (!getUserVisibleHint()) {
					return;
				} else {
					if (ownerName == null | ownerName.equals("")) {
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_unoperator_tip, null, R.string.message_confirm_tip, null,
								null, false, false);
						Log.i("tag", "设备尚未运营");
						return;
					}
					if (ownerId == null | ownerId.equals("")) {
						isOwner = false;
						sp.edit().putBoolean("isOwner", false).commit();
					} else {
						if (ownerId.equals(user_id)) {
							isOwner = true;

							sp.edit().putBoolean("isOwner", true).commit();
						} else {
							isOwner = false;
							sp.edit().putBoolean("isOwner", false).commit();
						}
					}
					switch (pileType) {
						case PILETYPE_FOR_GPRS://gprs桩
							isGprs_ble = false;
							break;
						case PILETYPE_FOR_GPRSBLE://gprs蓝牙桩
							isGprs_ble = true;
							break;
						case PILETYPE_FOR_BLE://蓝牙桩
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_unsupport_scan_tip, R.string.message_cancel_tip, R
									.string.message_ble_link_tip, true, true, true, false);
							return;
						default: //默认
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_being_fixing_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
							return;

					}
					/**
					 * 0 表示未上线，1表示空闲桩，2表示只连接未充电，3表示充电中，
					 * 4表示GPRS通讯中断，5表示检查中，6：表示预约，7表示故障 8表示充电完成
					 */
					switch (status) {
						case STATE_PILE_UNDISPLAY://表示未上线
							ToastUtil.show(getActivity(), R.string.message_unline_tip);
							if (mZxingManager != null) {
								mZxingManager.onResume();
							}
							break;
						case STATE_PILE_FREE://表示空闲桩

							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_unlink_gun_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
							break;
						case STATE_PILE_LINK_UNCHARGE://表示只连接未充电
							if (isOwner) {
								initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_content_owner_gprs_tip, R.string.message_cancel_tip, R
										.string.message_charge_now_tip, isGprs_ble, isOwner, true, false);
							} else {
								initWaterBlueDialog(R.string.message_charge_pile_tip, ownerName, pileName, price, pileCode, R.string.message_cancel_tip, R.string
										.message_charge_now_tip, isGprs_ble, isOwner, true);
							}
							break;
						case STATE_PILE_CHARGING://表示充电中，当前桩被占用,如果当前用户为桩主，扫描得到的是桩被占用，则证明租户在充电，桩主通过扫码监控租户的行为
							if (isOwner) {
								if (pileCode != null && !pileCode.equals("")) {
									ownerListenTenantIfCharging = true;
									isChargingStatus = true;
									checkIfCharging = true;
									removeCallbackThread(currentStatusRunnable);
									showPrepareCharging(false, R.string.message_charging_getting_tip,
											R.string.power, R.string.message_charging_pile_type_tip);
									showCharging(true);
									btn_control_charge.setText(R.string.message_charging_stop_observer_tip);
									mHandler.postDelayed(currentStatusRunnable, 10000);//获取桩的实时状态信息
								} else {

								}
							} else {
								ownerListenTenantIfCharging = false;
								initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_be_token_tip, null, R.string.message_confirm_tip, null,
										null, false, false);
							}
							break;
						case STATE_PILE_DISTURB://GPRS通讯中断
						case STATE_PILE_CHECKING://表示检查中
						case STATE_PILE_APPOINT://表示预约
						case STATE_PILE_ACCIDENT://表示故障

							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_being_accident_tip, null, R.string.message_confirm_tip, null,
									null, false, true);
							break;
						case STATE_PILE_CHARGINGCOMPLETE://表示充电完成
							if (pileSort == 2) {
								//交流
								if (isOwner) {
									initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_content_owner_gprs_tip, R.string.message_cancel_tip, R
											.string.message_charge_now_tip, isGprs_ble, isOwner, true, false);
								} else {
									initWaterBlueDialog(R.string.message_charge_pile_tip, ownerName, pileName, price, pileCode, R.string.message_cancel_tip, R.string
											.message_charge_now_tip, isGprs_ble, isOwner, true);
								}
							} else if (pileSort == 1) {
								//直流桩,需要拔枪再插枪
								initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_finish_ple_pull_and_insert_gun_tip, null, R.string.message_confirm_tip, null,
										null, false, false);
							} else {
								initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_being_unknow_error_tip, null, R.string.message_confirm_tip, null,
										null, false, false);
							}
							break;
						default:
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_being_unknow_error_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
							break;
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	public synchronized void ctrlRes(final String pileCode, final String userid, final int retryTimes, final boolean isChargingStatus) {
		WebAPIManager.getInstance().getCtrlRes(pileCode, userid, retryTimes, new WebResponseHandler<CtrlRes>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(getActivity(), e);
//				if (isChargingStatus) {
				setParamBoolean(false);
				ChargeFragment.this.retryTimes = 1;//重置轮询次数
				removeCallbackThread(currentStatusRunnable);
				mHandler.removeCallbacks(ctr_resRunnable);
				showRl_qrcode();
				setParamNull();
				if (mZxingManager != null) {
					mZxingManager.onResume();
				}

				return;
//				}
			}

			@Override
			public void onFailure(WebResponse<CtrlRes> response) {
				super.onFailure(response);
				TipsUtil.showTips(getActivity(), response);
				return;
			}

			@Override
			public void onSuccess(WebResponse<CtrlRes> response) {
				super.onSuccess(response);
				//errcode 为-2；4500,4506 时需要继续轮询,4800就去拿充电状态，4800,4505结束充电,
				CtrlRes ctrlRes = response.getResultObj();
				int action = -1;
				int errcode = -10000;
				if (ctrlRes != null) {
					errcode = ctrlRes.getErrcode();
					action = ctrlRes.getAction();
				}
				if (action == START_CHARGE_ACTION) {
//
					if (errcode == ERRORCODE_TWO || errcode == ERRORCODE_THIRD || errcode == ERRORCODE_FOURTH) {
						if (retryTimes == RETRYTIMESNUM) {
							ToastUtil.show(getActivity(), R.string.message_charging_on_request_outtime_tip);
							ChargeFragment.this.retryTimes = 1;//重置轮询次数
							setParamBoolean(false);
							removeCallbackThread(currentStatusRunnable);
							mHandler.removeCallbacks(ctr_resRunnable);
							showRl_qrcode();
							setParamNull();
							if (mZxingManager != null) {
								mZxingManager.onResume();
							}
							return;
						}
						ifFinishCtrRes = false;
						ChargeFragment.this.retryTimes++;
						mHandler.postDelayed(ctr_resRunnable, 5000);
					} else if (errcode == ERRORCODE_SUCCESS) {
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						orderId = ctrlRes.getOrderId();
						if (orderId != null && !orderId.equals("") && pileCode != null && !pileCode.equals("")) {
							ifFinishCtrRes = true;//完成轮询
							ChargeFragment.this.retryTimes = 1;//重置轮询次数
							mHandler.postDelayed(currentStatusRunnable, 1000);//获取桩的实时状态信息
						}
					} else if (errcode == ERRORCODE_HADOPEN || errcode == ERRORCODE_UNWAITINGSTATUS || errcode == ERRORCODE_GUNUNLINK || errcode ==
							FOUREIGHTERRORCODE_HADOPEN || errcode == FOUREIGHTERRORCODE_UNWAITINGSTATUS
							|| errcode == FOUREIGHTERRORCODE_GUNUNLINK || errcode == 901 || errcode == 902) {
						ChargeFragment.this.retryTimes = 1;//重置轮询次数
						setParamBoolean(false);
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						if (errcode == ERRORCODE_HADOPEN) {
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_be_token_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
						} else if (errcode == ERRORCODE_UNWAITINGSTATUS) {
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_charging_unwaiting_status_tip, null, R.string
									.message_confirm_tip, null, null, false, false);
						} else if (errcode == ERRORCODE_GUNUNLINK) {
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_unlink_gun_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
						} else if (errcode == 901 || errcode == 902) {
							initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_charging_device_exception_unstart_tip, null, R.string.message_confirm_tip, null,
									null, false, false);
						} else {
							showRl_qrcode();
							setParamNull();
							setParamBoolean(false);
							if (mZxingManager != null) {
								mZxingManager.onResume();
							}
						}
						return;
					} else if (retryTimes == RETRYTIMESNUM) {
						ToastUtil.show(getActivity(), R.string.message_charging_on_request_outtime_tip);
						ChargeFragment.this.retryTimes = 1;//重置轮询次数
						showRl_qrcode();
						setParamBoolean(false);
						setParamNull();
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						if (mZxingManager != null) {
							mZxingManager.onResume();
						}
						return;
					} else {
						ToastUtil.show(getActivity(), R.string.message_charging_on_request_outtime_tip);
						setParamBoolean(false);
						setParamNull();
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						ChargeFragment.this.retryTimes = 1;//重置轮询次数
						showRl_qrcode();
						if (mZxingManager != null) {
							mZxingManager.onResume();
						}
						return;
					}
				} else if (action == END_CHARGE_ACTION) {
					if (errcode == ERRORCODE_TWO || errcode == ERRORCODE_THIRD || errcode == ERRORCODE_FOURTH) {
						if (retryTimes == RETRYTIMESNUM) {
							ChargeFragment.this.retryTimes = 1;//重置轮询次数
							setParamBoolean(false);
							removeCallbackThread(currentStatusRunnable);
							mHandler.removeCallbacks(ctr_resRunnable);
							showRl_qrcode();
							setParamNull();
							if (mZxingManager != null) {
								mZxingManager.onResume();
							}
							return;
						}
						ifFinishCtrRes = false;
						ifGetCurrentStatus = false;
						btn_control_charge.setEnabled(true);
						ChargeFragment.this.retryTimes++;
						mHandler.postDelayed(ctr_resRunnable, 5000);//继续充电轮询
					} else if (errcode == ERRORCODE_SUCCESS || errcode == ERRORCODE_UNOPEN) {//轮询成功或者未开机结束轮询
						ChargeFragment.this.retryTimes = 1;
						ifFinishCtrRes = true;//完成轮询
						ifGetCurrentStatus = true;//获取状态完成
						btn_control_charge.setEnabled(false);
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						orderId = ctrlRes.getOrderId();
						if (orderId != null && !orderId.equals("") && pileCode != null && !pileCode.equals("") && !EasyActivityManager.getInstance().
								has(ChargeCompleteActivity.class)) {
							jumpIntoActivity();
						}
						return;
					} else if (retryTimes >= RETRYTIMESNUM || errcode == ERRORCODE_OUTOFELECTRI) {//断电情况下处理,弹框处理
						ifOofLine = true;//是否断电
						setParamBoolean(false);
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						ChargeFragment.this.retryTimes = 1;//重置轮询次数
						initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_finish_ple_pull_gun_tip, null, R.string.message_confirm_tip,
								null, null, false, false);
						return;
					} else if (errcode == ERRORCODE_HADOPEN || errcode == ERRORCODE_UNWAITINGSTATUS || errcode == ERRORCODE_GUNUNLINK || errcode ==
							FOUREIGHTERRORCODE_HADOPEN || errcode == FOUREIGHTERRORCODE_UNWAITINGSTATUS || errcode == FOUREIGHTERRORCODE_GUNUNLINK) {
						ChargeFragment.this.retryTimes = 1;//重置轮询次数
						ChargeFragment.this.isChargingStatus = true;
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						mHandler.post(currentStatusRunnable);
						return;
					} else {//结束充电失败，可继续点击停止结束
						ChargeFragment.this.retryTimes = 1;
						setParamBoolean(true);
						btn_control_charge.setEnabled(true);
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						return;
					}
				}

			}
		});
	}

	private void setParamBoolean(boolean real) {
		if (real) {
			ifFinishCtrRes = true;
			ifGetCurrentStatus = true;
			ChargeFragment.this.isChargingStatus = true;
		} else {
			ifFinishCtrRes = false;
			ifGetCurrentStatus = false;
			ChargeFragment.this.isChargingStatus = false;
		}
	}

	//开始充电或者停止充电
	public void control(final String userid, String deviceId, final String pileCode, int action, final boolean isChargingStatus) {
		WebAPIManager.getInstance().control(userid, deviceId, pileCode, action, new WebResponseHandler<Object>() {

			@Override
			public void onStart() {
				super.onStart();
				btn_control_charge.setEnabled(false);
				if (isChargingStatus) {
					showPrepareCharging(true, R.string.message_charging_starting_tip, R.string.power, R.string.message_charging_pile_type_tip);
					showCharging(true);
				} else {
					showAfterCharging(true, R.string.message_charging_stopping_tip, R.string.power);

				}
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				btn_control_charge.setEnabled(true);
				ownerListenTenantIfCharging = false;
				ChargeFragment.this.isChargingStatus = false;
				checkIfCharging = false;
				showRl_qrcode();
				int id = rg_scan_ble_tab.getCheckedRadioButtonId();
				setRadioButtonCheckToQr(id);
				setParamNull();
				removeCallbackThread(currentStatusRunnable);
				mHandler.removeCallbacks(ctr_resRunnable);
				ifFinishCtrRes = false;
				if (mZxingManager != null) {
					mZxingManager.onResume();
				}
				TipsUtil.showTips(getActivity(), e);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				btn_control_charge.setEnabled(true);
				TipsUtil.showTips(getActivity(), response);
				ownerListenTenantIfCharging = false;
				ChargeFragment.this.isChargingStatus = false;
				checkIfCharging = false;
				showRl_qrcode();
				int id = rg_scan_ble_tab.getCheckedRadioButtonId();
				setRadioButtonCheckToQr(id);
				setParamNull();
				removeCallbackThread(currentStatusRunnable);
				mHandler.removeCallbacks(ctr_resRunnable);
				ifFinishCtrRes = false;
				if (mZxingManager != null) {
					mZxingManager.onResume();
				}
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				btn_control_charge.setEnabled(true);
				if (response == null) {
					return;
				}
				ChargeFragment.this.isChargingStatus = isChargingStatus;
				if (isChargingStatus) {//如果开始充电状态，则显示充电中界面
					showPrepareCharging(false, R.string.message_charging_getting_tip, R.string.power, R.string.message_charging_pile_type_tip);
					removeCallbackThread(currentStatusRunnable);
					mHandler.removeCallbacks(ctr_resRunnable);
					ifFinishCtrRes = false;
					mHandler.postDelayed(ctr_resRunnable, 100);
				} else {//结束充电
					showAfterCharging(false, R.string.message_charging_getting_tip, 0);
					mHandler.removeCallbacks(ctr_resRunnable);
					removeCallbackThread(currentStatusRunnable);
					ifFinishCtrRes = false;
					mHandler.postDelayed(ctr_resRunnable, 100);
				}
			}
		});
	}

	private void removeCallbackThread(Runnable realTimeStatusRunnable) {
		mHandler.removeCallbacks(realTimeStatusRunnable);
	}

	//获取充电实时状态
	public synchronized void getchargingStatus(String pileCode) {
		WebAPIManager.getInstance().getchargingStatus(pileCode, new WebResponseHandler<RealTimeStatus>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(getActivity(), e);
			}

			@Override
			public void onFailure(WebResponse<RealTimeStatus> response) {
				super.onFailure(response);
				TipsUtil.showTips(getActivity(), response);
			}


			@Override
			public void onSuccess(WebResponse<RealTimeStatus> response) {
				super.onSuccess(response);
				if(CONTROLLERACTION == END_CHARGE_ACTION){//防止点击结果充电时候充电的状态返回延迟，直接return ;
					CONTROLLERACTION = -1 ;
					removeCallbackThread(currentStatusRunnable);
					return ;
				}
				if (ifFinishCtrRes == false) {
					ifFinishCtrRes = true;
				}
				if (ifGetCurrentStatus == false) {
					ifGetCurrentStatus = true;
				}
				RealTimeStatus realTimeStatus = null;
				String json = response.getResult();
				if (json != null && !json.equals("")) {
					JsonObject jo = new JsonParser().parse(json).getAsJsonObject();
					if (jo.has(KEY.JSON.JSONRESULT)) {
						realTimeStatus = GsonUtil.createSecurityGson().fromJson(jo.get(KEY.JSON.JSONRESULT), RealTimeStatus.class);
					}
				} else {
					return;
				}
				if (realTimeStatus != null) {
					int deviceStatus = realTimeStatus.getStatus();
					int soc = realTimeStatus.getSoc();
					Double current = realTimeStatus.getCurrent();
					Double voltage = realTimeStatus.getVoltage();
					Double power = realTimeStatus.getPower();
					Double curElect = realTimeStatus.getCurElect();
					int electType = realTimeStatus.getInterType();//桩类型（）,1:交流, 2:直流
					if (curElect == null) {
						curElect = 0.00;
					}
					if (ll_tv_soc_or_ele.getVisibility() == View.GONE) {
						ll_charge_soc_or_ele_count.setVisibility(View.VISIBLE);
					}
					if (ll_tv_soc_or_ele.getVisibility() == View.GONE) {
						ll_tv_soc_or_ele.setVisibility(View.VISIBLE);
					}
					if (ll_charge_status.getVisibility() == View.VISIBLE) {
						ll_charge_status.setVisibility(View.GONE);
					}
					if (electType == INTERTYPE_DC) {
						tv_charging_type_tip.setText(R.string.message_charging_progress_tip);
						tv_soc_or_ele.setText(soc + "");
						tv_soc_or_ele_unit.setText("%");
						tv_output_type.setText(R.string.message_charging_pile_dc_name_tip);
						tv_w_unit.setText(R.string.power_unit);
						tv_output_current.setText(String.format("%.1f", current));
						tv_output_voltage.setText(String.format("%.1f", voltage));
						tv_output_power.setText(String.format("%.2f", curElect));
					} else if (electType == INTERTYPE_AC) {
						tv_charging_type_tip.setText(R.string.message_charging_ele_tip);
						tv_soc_or_ele.setText(String.format("%.2f", curElect));
						tv_soc_or_ele_unit.setText(R.string.power_unit);
						tv_output_type.setText(R.string.message_charging_pile_ac_name_tip);
						tv_w_unit.setText(R.string.power);
						tv_output_current.setText(String.format("%.1f", current));
						tv_output_voltage.setText(String.format("%.1f", voltage));
						tv_output_power.setText(String.format("%.2f", power / 1000));
					}
					switch (deviceStatus) {
						//0 未上线 1：空闲桩 ，2：只连接未充电，3：充电进行中, 4：GPRS通讯中断,5：检修中,6：预约，7：故障 8表示充电完成
						case STATE_PILE_FREE://表示空闲,跳到结算页面
						case STATE_PILE_LINK_UNCHARGE://表示只连接未充电
							if (!ownerListenTenantIfCharging && isChargingStatus && !EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								isChargingStatus = false;
								removeCallbackThread(currentStatusRunnable);
								jumpIntoActivity();
							}
							if (ownerListenTenantIfCharging && orderId == null && isChargingStatus &&
									!EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								ToastUtil.show(getActivity(), R.string.message_charging_tanent_stop_tip);
								removeCallbackThread(currentStatusRunnable);
								ownerListenTenantIfCharging = false;
								isChargingStatus = false;
								checkIfCharging = false;
								showRl_qrcode();
								int id = rg_scan_ble_tab.getCheckedRadioButtonId();
								setRadioButtonCheckToQr(id);
							}

							break;
						case STATE_PILE_CHARGINGCOMPLETE://表示充电完成
							if (!ownerListenTenantIfCharging && isChargingStatus && !EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								isChargingStatus = false;
								removeCallbackThread(currentStatusRunnable);
								showHadFinishCharging(R.string.message_charging_had_finish_tip);

								if (chargeCompleteDialog != null && chargeCompleteDialog.isShowing()) {
									chargeCompleteDialog.dismiss();
								}
								chargeCompleteDialog = new ChargeCompleteDialog(getActivity());
								chargeCompleteDialog.getTv_tip().setText(R.string.message_charging_finish_tip);
								chargeCompleteDialog.show();
								timerTask();//定时跳转，dismiss dialog
//								jumpIntoActivity();

							}
							if (ownerListenTenantIfCharging && orderId == null && isChargingStatus &&
									!EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								ToastUtil.show(getActivity(), R.string.message_charging_tanent_stop_tip);
								removeCallbackThread(currentStatusRunnable);
								ownerListenTenantIfCharging = false;
								isChargingStatus = false;
								checkIfCharging = false;
								showRl_qrcode();
								int id = rg_scan_ble_tab.getCheckedRadioButtonId();
								setRadioButtonCheckToQr(id);
							}
							break;
						case STATE_PILE_UNDISPLAY://表示未上线
						case STATE_PILE_DISTURB://表示GPRS通讯中断
						case STATE_PILE_CHECKING://表示设备检修中
						case STATE_PILE_APPOINT://表示预约
						case STATE_PILE_ACCIDENT://表示设备故障，弹出设备故障提示
							if (!ownerListenTenantIfCharging && isChargingStatus && !EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								chargingError = true;
								isChargingStatus = false;
								removeCallbackThread(currentStatusRunnable);
								initWaterBlueDialog(R.string.message_charge_title_tip, R.string.message_device_error_tip, null, R.string.message_confirm_tip,
										null, null, false, false);
							}
							if (ownerListenTenantIfCharging && isChargingStatus &&
									!EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
								ToastUtil.show(getActivity(), R.string.message_charging_device_exception_tip);
								removeCallbackThread(currentStatusRunnable);
								ownerListenTenantIfCharging = false;
								isChargingStatus = false;
								checkIfCharging = false;
								showRl_qrcode();
								int id = rg_scan_ble_tab.getCheckedRadioButtonId();
								setRadioButtonCheckToQr(id);
							}
							break;
						case STATE_PILE_CHARGING://表示充电中,充电中不做任何的处理
							if (!isChargingStatus) {
								isChargingStatus = true;
							}
							break;
					}
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (isChargingStatus) {
					mHandler.postDelayed(currentStatusRunnable, 5000);
				}
			}
		});
	}

	public void timerTask() {
		//创建定时线程执行更新任务
		if (mTimer == null) {
			mTimer = new Timer();// 定时器  ;
		}
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (chargeCompleteDialog != null && chargeCompleteDialog.isShowing()) {
					Log.i("tag", "// 向Handler发送消息");
					mHandler.sendEmptyMessage(1);// 向Handler发送消息
				} else {
					Log.i("tag", "// 向Handler发送消息停止继续执行");
					mHandler.sendEmptyMessage(2);// 向Handler发送消息停止继续执行
				}
			}
		}, 2000, 1000);// 定时任务
	}

	private void jumpIntoActivity() {

		//结束充电后，设置点击为false
		btn_control_charge.setEnabled(false);
		removeCallbackThread(currentStatusRunnable);
		//跳到充电完成
		if (orderId != null && pileCode != null && getActivity() != null) {
			Intent intent = new Intent(getActivity(), ChargeCompleteActivity.class);
			intent.putExtra(KEY.INTENT.IS_ISOWNER, sp.getBoolean("isOwner", false));
			intent.putExtra(KEY.INTENT.ORDER_ID, orderId);
			intent.putExtra(KEY.INTENT.ISGPRS, true);
			intent.putExtra(KEY.INTENT.PILE_ID, ChargeFragment.this.pileCode);
			getActivity().startActivityForResult(intent, TOCHARGECOMPLETE);
			setParamNull();
			System.gc();
		}


	}

	/*void setParamNull(String... args) {
		for (String arg : args) {
			if (arg != null) {
				arg = null;
			}
		}
	}*/

	@Override
	public void onStart() {
		super.onStart();
		if (bleorgprs) {
			if (mZxingManager != null && rl_qrcode.getVisibility() == View.VISIBLE) {
				mZxingManager.onResume();
			} else {
				if (mZxingManager != null) mZxingManager.onPause();
			}
			mHandler.removeCallbacks(tryAutoConnectRunnable);
			if (NetWorkUtil.isNetworkConnected(getActivity())) {
				//当还没有检查,已经登录不在充电页面
				if (getActivity() != null && !checkIfCharging && !isChargingStatus &&
						isLogin() && state != STATE_CHARGING &&
						state != STATE_START_CHARGE) {//检查是否充电状态
					Log.i("tag", "当还没有检查界面可见已经登录不在充电页面");
					user_id = sp.getString("user_id", null);
					removeCallbackThread(currentStatusRunnable);
					checkIfCharging(user_id);
				} else {
					if (isChargingStatus && state == STATE_CHARGING && !isLogin()) {
						checkIfCharging = false;
						isChargingStatus = false;
						removeCallbackThread(currentStatusRunnable);
						showRl_qrcode();
						int id = rg_scan_ble_tab.getCheckedRadioButtonId();
						setRadioButtonCheckToQr(id);
						setParamNull();
						if (mZxingManager != null) {
							mZxingManager.onResume();
						}
					}
				}
			} else if (!NetWorkUtil.isNetworkConnected(getActivity()) && !checkIfCharging && !isChargingStatus &&
					isLogin() && state != STATE_CHARGING &&
					state != STATE_START_CHARGE) {
				if (BTManager.getInstance().isConnected()) {//检查蓝牙是否连接
					btn_change_bt.setActivated(true);
					if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
						refreshStatus();
					} else {
						CmdManager.getInstance().aquirePileId();//鉴权失败，重新获取桩Id去鉴权
					}
				} else {
					mHandler.removeCallbacks(tryAutoConnectRunnable);
					mHandler.postDelayed(tryAutoConnectRunnable, 1000);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("tag", "onResume");
		if (!bleorgprs) {
			if (BTManager.getInstance().isConnected() &&
					(state == STATE_CHARGING || state == STATE_START_CHARGE)) {//检查蓝牙是否连接
				btn_change_bt.setActivated(true);
				if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
					refreshStatus();
				} else {
					CmdManager.getInstance().aquirePileId();
				}
			} else {
//			当切换到蓝牙充电页面，蓝牙未连接并且时从连接蓝牙方式发送停止指令进去充电完成页面返回调用onResume时，才去尝试自动连接上一次蓝牙桩
				mHandler.removeCallbacks(tryAutoConnectRunnable);
				mHandler.postDelayed(tryAutoConnectRunnable, 1000);
			}
		}
		if (isChargingStatus && state == STATE_CHARGING && !isLogin()) {
			checkIfCharging = false;
			isChargingStatus = false;
			removeCallbackThread(currentStatusRunnable);
			showRl_qrcode();
			setParamNull();
		} else {
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i("tag", "onPause");
		stopRefreshStatus();
		if (mZxingManager != null) {
			mZxingManager.onPause();//二维码暂停扫码
		}
		CameraManager.get().closeDriver();
	}

	private void unregisterReceiver() {
		if (myReceiver != null) {
			getActivity().unregisterReceiver(myReceiver);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("tag", "onDestroy");
		unregisterReceiver();
		CmdManager.getInstance().removeOnCmdListener(onCmdListener);
		mBTChooseDialog.unregisterReceiver();
		if (broadcastReceiver != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
		stopRefreshStatus();
		if (mZxingManager != null) {
			mZxingManager.onDestroy();
		}
		if (mTimer != null) {
			mTimer.cancel();// 程序退出时cancel timer
		}
		mHandler.removeCallbacksAndMessages(null);//清除所有跟当前handler相关的Runnable和Message,防止内存泄露
	}

	@Override
	public String getFragmentName() {
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_control_charge:
				if (isChargingStatus && !ownerListenTenantIfCharging) {//表示gprs桩正在充电
					if (ifFinishCtrRes && ifGetCurrentStatus) {//只有完成轮询以及拿到状态记录的时候才能点击去结束充电
						control(user_id, deviceId, pileCode, CONTROLLERACTION = END_CHARGE_ACTION, !isChargingStatus);
						//结束充电
					} else {
//轮询未结束的处理逻辑
					}
					return;
				}
				if (isChargingStatus && ownerListenTenantIfCharging) {//表示桩主正在监听租户充电中，跳回到扫码页面
					removeCallbackThread(currentStatusRunnable);
					ownerListenTenantIfCharging = false;
					isChargingStatus = false;
					checkIfCharging = false;
					showRl_qrcode();
					int id = rg_scan_ble_tab.getCheckedRadioButtonId();
					setRadioButtonCheckToQr(id);
					return;
				}
				switch (state) {
					case STATE_START_CHARGE:
						CmdManager.getInstance().startCharge();
						Log.i("tag", "CmdManager.getInstance().startCharge();");
						if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
							refreshStatus();
						} else {
							CmdManager.getInstance().aquirePileId();
						}
						break;
					case STATE_CHARGING:
						CmdManager.getInstance().stopCharge();
						break;
					default:
						break;
				}
				break;
			case R.id.ib_connect:
				if (state != STATE_CONNECT_BT) {
					break;
				}
			case R.id.btn_change_bt:
			/*
			 * 判断用户是否登录
			 */
				boolean is_Login = isLogin();
				if (is_Login) {
					/*
					切换设备
					if (BTManager.getInstance().isConnected() && CmdManager.getInstance().isValid()) {
						return;
					}*/
					Log.i("tag", "click btn_change_bt");
					mBTChooseDialog.show();//蓝牙方式搜索才showdialog
//					}
				} else {// 返回登录页面
					if (MainActivity.instance != null) {
						MainActivity.instance.sendMessage(MainActivity.MSG_GO_LOGIN, null);
					}
					ib_connect.setVisibility(View.VISIBLE);
				}

				break;
			case R.id.btn_left:
				if (MainActivity.instance != null) {
					MainActivity.instance.sendMessage(MainActivity.MSG_GO_MY_HOMESETTING, null);
				}
				break;
			case R.id.btn_right:
				if (!CmdManager.getInstance().isValid()) {
					return;
				}
				// 编辑时间
				timePickDialog.setTime(System.currentTimeMillis());
				timePickDialog.show();
				break;
		}

	}

	private void hideLightView(ToggleButton view) {
		view.setChecked(false);
		view.setVisibility(View.INVISIBLE);
	}

	private void showLightView(ToggleButton view) {
		view.setVisibility(View.VISIBLE);
	}

	private void controlLight(ToggleButton view) {
		try {
			android.hardware.Camera camera = CameraManager.get().openDriver(surfaceHolder);
			android.hardware.Camera.Parameters parameters = camera.getParameters();
			if (view.isChecked()) {
				CameraManager.get().turnOn(parameters);
			} else {
				CameraManager.get().turnOff(parameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isLogin() {
		if (sp == null) {
			sp = MainActivity.instance.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean("isLogin", false);
	}

	private void showAfterCharging(boolean isStartingNotGaining, int content, int param) {
		if (ll_charge_time_count.getVisibility() == View.VISIBLE) {
			ll_charge_time_count.setVisibility(View.GONE);
		}
		if (ll_charge_soc_or_ele_count.getVisibility() == View.GONE) {
			ll_charge_soc_or_ele_count.setVisibility(View.VISIBLE);
		}
		if (ll_tv_soc_or_ele.getVisibility() == View.VISIBLE) {
			ll_tv_soc_or_ele.setVisibility(View.GONE);
		}
		if (ll_charge_status.getVisibility() == View.GONE) {
			ll_charge_status.setVisibility(View.VISIBLE);
		}
		if (ll_output_power.getVisibility() == View.GONE) {
			ll_output_power.setVisibility(View.VISIBLE);
		}
		tv_charge_status.setText(content);
		if (isStartingNotGaining) {
			tv_w_unit.setText(param);
		}
		System.gc();
	}

	private void showPrepareCharging(boolean isStartingNotGaining, int content, int unit, int type) {
		if (ll_charge_time_count.getVisibility() == View.VISIBLE) {
			ll_charge_time_count.setVisibility(View.GONE);
		}
		if (ll_charge_soc_or_ele_count.getVisibility() == View.GONE) {
			ll_charge_soc_or_ele_count.setVisibility(View.VISIBLE);
		}
		if (ll_tv_soc_or_ele.getVisibility() == View.VISIBLE) {
			ll_tv_soc_or_ele.setVisibility(View.GONE);
		}
		if (ll_charge_status.getVisibility() == View.GONE) {
			ll_charge_status.setVisibility(View.VISIBLE);
		}
		if (ll_output_power.getVisibility() == View.GONE) {
			ll_output_power.setVisibility(View.VISIBLE);
		}
		if (isStartingNotGaining) {
			tv_output_type.setText(type);
			tv_w_unit.setText(unit);
		}
		//初始化数据
		tv_output_current.setText("0.0");
		tv_output_voltage.setText("0.0");
		tv_output_power.setText("0.00");


		tv_charge_status.setText(content);
		System.gc();
	}

	private void showBlePrepareCharging(int unit) {
		if (ll_output_power.getVisibility() == View.GONE) {
			ll_output_power.setVisibility(View.VISIBLE);
		}
		if (ll_charge_time_count.getVisibility() == View.GONE) {
			ll_charge_time_count.setVisibility(View.VISIBLE);
		}
		if (ll_charge_soc_or_ele_count.getVisibility() == View.VISIBLE) {
			ll_charge_soc_or_ele_count.setVisibility(View.GONE);
		}
		tv_w_unit.setText(unit);
		System.gc();
	}

	private void showHadFinishCharging(int content) {
		if (ll_charge_time_count.getVisibility() == View.VISIBLE) {
			ll_charge_time_count.setVisibility(View.GONE);
		}
		if (ll_charge_soc_or_ele_count.getVisibility() == View.GONE) {
			ll_charge_soc_or_ele_count.setVisibility(View.VISIBLE);
		}
		if (ll_tv_soc_or_ele.getVisibility() == View.VISIBLE) {
			ll_tv_soc_or_ele.setVisibility(View.GONE);
		}
		if (ll_charge_status.getVisibility() == View.GONE) {
			ll_charge_status.setVisibility(View.VISIBLE);
		}
		if (ll_output_power.getVisibility() == View.GONE) {
			ll_output_power.setVisibility(View.VISIBLE);
		}
		iv_electric.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		setImageResource(iv_electric_high, R.drawable.home_yuan_bg3);
		setImageResource(iv_electric, R.drawable.home_yuan_bg2);
		iv_electric_high.startAnimation(null);
		iv_electric.startAnimation(null);
		tv_charge_status.setText(content);
		System.gc();
	}

	private void showConnectBT() {
		hideLightView(switch_onoff);
		int id = rg_scan_ble_tab.getCheckedRadioButtonId();
		setRadioButtonCheckToBle(id);
		if (state == STATE_CONNECT_BT) {
			return;
		}
		state = STATE_CONNECT_BT;
		tv_center.setText(R.string.app_title);
		iv_bg.setVisibility(View.VISIBLE);
		iv_bg.setImageResource(R.drawable.bg_blue);
		ib_alarm.setVisibility(View.INVISIBLE);
		rl_connect.setVisibility(View.VISIBLE);//连接
		rl_charge.setVisibility(View.GONE);//充电
		rl_scan_or_ble_charge.setVisibility(View.VISIBLE);
		rl_qrcode.setVisibility(View.GONE);

		tv_connect_tips1.setText(R.string.please_connect_bluetooth);
		tv_connect_tips2.setText(R.string.tap_bluetooth_icon);
		tv_connect_tips3.setText(R.string.connect_bluetooth);
		tv_connect_tips3.setVisibility(View.VISIBLE);
		recycleImageResource(iv_electric);
		recycleImageResource(iv_electric_high);
		setImageResource(ib_connect_low, R.drawable.connect_bluetooth_low);
		setImageResource(ib_connect, R.drawable.connect_bluetooth);
		ib_connect_low.setAnimation(null);
		ib_connect.setAnimation(null);
		ib_connect.setEnabled(true);
		hideWarringMessage();
		System.gc();
	}

	private void showConnectingBT() {
		int id = rg_scan_ble_tab.getCheckedRadioButtonId();
		setRadioButtonCheckToBle(id);
		if (state == STATE_CONNECTING_BT) {
			return;
		}
		state = STATE_CONNECTING_BT;
		tv_center.setText(R.string.app_title);
		iv_bg.setVisibility(View.VISIBLE);
		iv_bg.setImageResource(R.drawable.bg_blue);
		ib_alarm.setVisibility(View.INVISIBLE);
		rl_connect.setVisibility(View.VISIBLE);
		rl_charge.setVisibility(View.GONE);
		rl_scan_or_ble_charge.setVisibility(View.GONE);
		rl_qrcode.setVisibility(View.GONE);

		tv_connect_tips1.setText(R.string.please_connect_bluetooth);
		tv_connect_tips2.setText(R.string.please_wait);
		tv_connect_tips3.setText(R.string.connecting);
		tv_connect_tips3.setVisibility(View.VISIBLE);

		recycleImageResource(iv_electric);
		recycleImageResource(iv_electric_high);
		setImageResource(ib_connect_low, R.drawable.connect_bluetooth_low);
		setImageResource(ib_connect, R.drawable.connect_bluetooth);
		ib_connect_low.setAnimation(null);
		ib_connect.startAnimation(connectAnim);
		ib_connect.setEnabled(true);

		hideWarringMessage();
		System.gc();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	private void showConnectGun() {
		if (state == STATE_CONNECT_GUN) {
			return;
		}
		Log.i("tag", "showConnectGun===============");
		state = STATE_CONNECT_GUN;
		if (currentPileId != null && !currentPileId.equals("")) {
			Pile pile = DbHelper.getInstance(getActivity()).getPileDao().load(currentPileId);
			tv_center.setText(pile == null ? getText(R.string.app_title) : pile.getPileNameAsString());
		}
		iv_bg.setVisibility(View.VISIBLE);
		iv_bg.setImageResource(R.drawable.bg_blue);
		ib_alarm.setVisibility(View.INVISIBLE);
		rl_connect.setVisibility(View.VISIBLE);
		ib_connect.setVisibility(View.VISIBLE);
		rl_charge.setVisibility(View.GONE);
		rl_qrcode.setVisibility(View.GONE);

		tv_connect_tips1.setText(R.string.please_connect_pile);
		tv_connect_tips2.setText(R.string.insert_gun);
		tv_connect_tips3.setVisibility(View.INVISIBLE);

		recycleImageResource(iv_electric);
		recycleImageResource(iv_electric_high);
		setImageResource(ib_connect_low, R.drawable.connect_icon_off);
		setImageResource(ib_connect, R.drawable.connect_icon_on);

		ib_connect_low.setAnimation(null);
		ib_connect.startAnimation(connectAnim);

		ib_connect.setEnabled(false);

		hideWarringMessage();
		System.gc();
	}

	private void showStartCharge() {
		if (state == STATE_START_CHARGE) {
			return;
		}
		state = STATE_START_CHARGE;
		if (currentPileId != null && !currentPileId.equals("")) {
			Pile pile = DbHelper.getInstance(getActivity()).getPileDao().load(currentPileId);
			tv_center.setText(pile == null ? getText(R.string.app_title) : pile.getPileNameAsString());
		}
		iv_bg.setImageResource(R.drawable.home_bg);
		ib_alarm.setVisibility(View.VISIBLE);
		rl_charge.setVisibility(View.VISIBLE);
		btn_change_bt.setVisibility(View.VISIBLE);
		ll_charge_time_count.setVisibility(View.VISIBLE);
		ll_charge_soc_or_ele_count.setVisibility(View.GONE);
		tv_w_unit.setText(R.string.power_unit);
		rl_connect.setVisibility(View.GONE);
		rl_scan_or_ble_charge.setVisibility(View.GONE);
		rl_qrcode.setVisibility(View.GONE);
		ll_output_info.setVisibility(View.GONE);//输出参数

		recycleImageResource(ib_connect_low);
		recycleImageResource(ib_connect);
		setImageResource(iv_electric_high, R.drawable.home_yuan_bg3);
		setImageResource(iv_electric, R.drawable.home_yuan_bg2);
		iv_electric_high.setAnimation(null);
		tv_time_hour.setText("00");
		tv_time_minutes.setText("00");

		btn_change_bt.setActivated(true);
		btn_control_charge.setText(R.string.start_charge);
		btn_control_charge.setEnabled(true);

		System.gc();
	}

	private void showCharging(boolean bleorgprs) {
		this.bleorgprs = bleorgprs;
		if (state == STATE_CHARGING) {
			return;
		}
		state = STATE_CHARGING;
		if (currentPileId != null && !currentPileId.equals("")) {
			Pile pile = DbHelper.getInstance(getActivity()).getPileDao().load(currentPileId);
			tv_center.setText(pile == null ? getText(R.string.app_title) : pile.getPileNameAsString());
		}
		iv_bg.setVisibility(View.VISIBLE);
		iv_bg.setImageResource(R.drawable.home_bg);
		ib_alarm.setVisibility(View.INVISIBLE);
		rl_charge.setVisibility(View.VISIBLE);
		ll_output_info.setVisibility(View.VISIBLE);
		rl_connect.setVisibility(View.GONE);
		rl_scan_or_ble_charge.setVisibility(View.GONE);
		rl_qrcode.setVisibility(View.GONE);
		btn_change_bt.setVisibility(View.GONE);

		recycleImageResource(ib_connect_low);
		recycleImageResource(ib_connect);
		// 硬件加速
		iv_electric.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		setImageResource(iv_electric_high, R.drawable.home_yuan_bg3);
		setImageResource(iv_electric, R.drawable.home_yuan_bg2);
		iv_electric_high.startAnimation(chargeShowAnim);
		iv_electric.startAnimation(chargeRotateAnim);
		tv_time_hour.setText("00");
		tv_time_minutes.setText("00");

		btn_control_charge.setText(R.string.stop_charge);
		btn_control_charge.setEnabled(true);
		System.gc();
	}

	private void setImageResource(ImageView iv, int resId) {
		iv.setImageBitmap(BitmapUtil.get(iv.getContext(), resId));
	}

	private void recycleImageResource(ImageView iv) {
		BitmapUtil.recycle(iv);
	}


	private void showError() {
		showWarringMessage(getResources().getString(R.string.charge_error));
	}

	private void hideWarringMessage() {
		tv_error.setVisibility(View.GONE);
	}

	private void showWarringMessage(String mesage) {
		tv_error.setText(mesage);
		tv_error.setVisibility(View.VISIBLE);
	}

	private OnCmdListener onCmdListener = new OnCmdListener() {
		/**
		 * 获取桩Id返回
		 */
		@Override
		protected void onAquirePileId(String pileId) {
			Log.v(TAG, "connecting PileId: " + pileId);
			if (CmdManager.getInstance().isValid()) {
				Log.v(TAG, "已经鉴权过了，不需要再获取pileid: " + pileId);
				return;
			}
			currentPileId = pileId;
			// 获取对应的key
			Key key = null;
			user_id = sp.getString("user_id", null);
			List<Key> keys = DbHelper.getInstance(getActivity()).getKeyDao().loadAll();
			if (!EmptyUtil.isEmpty(keys) && !EmptyUtil.isEmpty(pileId) && !EmptyUtil.isEmpty(user_id)) {
				Map<Integer, Key> keysMap = new HashMap<Integer, Key>();
				for (Key tempKey : keys) {
					Log.v(TAG, "UserId: " + tempKey.getUserId());
					Log.v(TAG, "PileId: " + tempKey.getPileId());
					Log.v(TAG, "KeyType: " + tempKey.getKeyType());
					Log.v(TAG, "Key: " + tempKey.getKey());
					// 当前用户在当前连接的桩的家人或主人key
					if (user_id.equals(tempKey.getUserId()) && pileId.equals(tempKey.getPileId())) {
						keysMap.put(tempKey.getKeyType(), tempKey);
					}
				}
				if (keysMap.size() > 0) {
					Iterator it = keysMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry entry = (Map.Entry) it.next();
						Integer keyInt = (Integer) entry.getKey();
						Key value = (Key) entry.getValue();
						if (keyInt == Key.TYPE_ORDER && value.getStartTime() < System.currentTimeMillis() && value.getEndTime() > System
								.currentTimeMillis()) {
							key = value;
							break;
						} else if (keyInt == Key.TYPE_OWNER) {
							key = value;
							break;
						} else if (keyInt == Key.TYPE_FAMILY) {
							key = value;
						}
					}
				}
			}
			if (key != null) {
				// 获取到key
				Log.e(TAG, "match key: " + key.getKey());
				CmdManager.getInstance().setKey(key);
				if (key.getKeyType() == Key.TYPE_OWNER) {
					// 主人key，需要鉴权
					Log.e(TAG, "开始鉴权");
					CmdManager.getInstance().authenticate();
				} else {
					// 其他key，验证key的有效性
					Log.e(TAG, "开始验证Key的有效性");
					CmdManager.getInstance().checkLegality();
				}
			} else {
				// 获取不到对应的key，断开连接
				ToastUtil.show(getActivity(), R.string.control_no_permission);
				BTManager.getInstance().disconnect();
				CmdManager.getInstance().setKey(null);
				showConnectBT();
			}
		}

		/**
		 * 鉴权返回
		 */
		@Override
		protected void onAuthenticate(boolean success) {
			if (success) {
				// 鉴权成功，开始控制电桩
				Log.e("onAuthenticate", "鉴权成功");
				// 刷新状态
				refreshStatus();
				showPileType();
				// 同步家人列表（新版已经不需要同步家人了）
				// syncFamily();
				// 下载历史记录
				Intent service = new Intent(getActivity(), ChargeRecordSyncService.class);
				service.putExtra("pileId", currentPileId);
				service.putExtra("mode", ChargeRecordSyncService.MODE_DOWNLOAD_AND_UPLOAD);
				getActivity().startService(service);
			} else {
				// 鉴权失败，断开连接				ToastUtil.show(getActivity(), R.string.control_no_permission);
				Log.e(TAG, "鉴权失败");
				BTManager.getInstance().disconnect();
				CmdManager.getInstance().setKey(null);
				showConnectBT();
			}
		}

		/**
		 * 验证key有效性返回
		 */
		@Override
		protected void onCheckLegality(boolean success) {
			if (success) {
				// key有效，开始控制电桩
				Log.e(TAG, "验证成功");
				refreshStatus();
				showPileType();
				Intent service = new Intent(getActivity(), ChargeRecordSyncService.class);
				service.putExtra("pileId", currentPileId);
				service.putExtra("mode", ChargeRecordSyncService.MODE_DOWNLOAD_AND_UPLOAD);
				getActivity().startService(service);
			} else {
				// key无效，断开连接
				ToastUtil.show(getActivity(), R.string.control_no_permission);
				Log.e(TAG, "验证失败");
				BTManager.getInstance().disconnect();
				CmdManager.getInstance().setKey(null);
				showConnectBT();
			}
		}

		@Override
		protected void onStopCharge(boolean success) {
			super.onStopCharge(success);
			if (success && !EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
				Log.i("tag", "onStopCharge====");
				if (CmdManager.getInstance().isValid()) {//鉴权成功，重新刷状态
					refreshStatus();
				}
				Intent intent = new Intent(getActivity(), ChargeCompleteActivity.class);
				startActivity(intent);
			} else {
				return;
			}
		}

		/**
		 * 获取设备状态返回
		 */
		@Override
		protected void onAcqDeviceState(final PileStatus status) {
			Log.i("tag", "获取设备状态返回 onAcqDeviceState");
			// 工作状态
			switch (status.getChargeStatus()) {
				// 待机
				case PileStatus.STATUS_IDLE: {
//					// 如果是从充电中变成待机，而且上条命令是停止状态，则证明是人为停止充电，显示结算页面
//					if (state == STATE_CHARGING && status.getCmdStatus() == PileStatus
//							.STATUS_CMD_STOP_CHARGE && !EasyActivityManager.getInstance().has
//							(ChargeCompleteActivity.class)) {
//						Intent intent = new Intent(getActivity(), ChargeCompleteActivity.class);
//						startActivity(intent);
//					}
					// 连接状态
					if (status.isGunConnected() && (status.isCarConnected() || status.isCarReady())) {
						if (state == STATE_CHARGING) {
							showStartCharge();
							iv_electric_high.startAnimation(chargeHideAnim);
						} else {
							showStartCharge();
						}
					} else {
						showConnectGun();
					}
					// 预约时间
					if (status.getDelayStatus() == PileStatus.STATUS_DELAYING) {
						Log.i("tag", "延迟充电PileStatus.STATUS_DELAYING");
						String timeString = TimeUtil.format(status.getDelayTime(), "HH:mm");
						btn_control_charge.setText(timeString + "启动");
						btn_control_charge.setEnabled(false);
						timePickDialog.setLeftBtnText("取消定时");
					} else {
						btn_control_charge.setText(R.string.start_charge);
						btn_control_charge.setEnabled(true);
						timePickDialog.setLeftBtnText("取消");
					}
					break;
				}
				// 正在充电
				case PileStatus.STATUS_CHARGING: {
					Log.i("tag", "蓝牙桩正在充电");
					if (state != STATE_CHARGING) {
						showBlePrepareCharging(R.string.power_unit);//蓝牙桩显示充电时间,暂时这样处理，日后要优化
					}
					showCharging(false);
					// 获取实时充电状态
					CmdManager.getInstance().acqChargingState();
					break;
				}
				default:
					ToastUtil.show(getActivity(), "设备处于未知状态");
					showConnectGun();
					break;
			}

			// 告警
			if (status.isVoltageOver()) {
			} else if (status.isVoltageOver()) {
				showWarringMessage(getResources().getString(R.string.charge_voltage_over));
			} else if (status.isVoltageLower()) {
				showWarringMessage(getResources().getString(R.string.charge_voltage_lower));
			} else if (status.isCurrentOver()) {
				showWarringMessage(getResources().getString(R.string.charge_current_over));
			} else if (status.isCurrentLeak()) {
				showWarringMessage(getResources().getString(R.string.charge_current_leak));
			} else if (status.isCurrentShort()) {
				showWarringMessage(getResources().getString(R.string.charge_current_short));
			} else if (status.isError()) {
				showError();
			} else {
				hideWarringMessage();
			}
			if (alarmByte != status.getAlarmByte()) {
				WebAPIManager.getInstance().uploadPileWarring(currentPileId, status.getAlarmByte(), new WebResponseHandler<PileStatus>() {

					@Override
					public void onFailure(WebResponse<PileStatus> response) {
						super.onFailure(response);
					}

					@Override
					public void onSuccess(WebResponse<PileStatus> response) {
						super.onSuccess(response);
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
				alarmByte = status.getAlarmByte();
			}
		}

		/**
		 * 实时充电信息返回
		 */
		@Override
		protected void onAcqChargingState(float voltage, float current, float amount, float power, int min) {
			tv_output_current.setText(String.format("%.1f", current));
			tv_output_voltage.setText(String.format("%.1f", voltage));
			tv_output_power.setText(String.format("%.2f", power));
			tv_time_hour.setText("" + (min / 60 < 10 ? "0" : "") + (min / 60));
			tv_time_minutes.setText("" + (min % 60 < 10 ? "0" : "") + (min % 60));
		}

		/**
		 * 指令超时
		 */
		@Override
		protected void onTimeOut() {
			ToastUtil.show(getActivity(), "电桩暂无响应，已断开连接");
		}
	};

	private void showPileType() {
		Pile pile = DbHelper.getInstance(getActivity()).getPileDao().load(currentPileId);
		Log.i("TAG", "currentPileId:" + currentPileId + "");
		if (pile == null) {
			WebAPIManager.getInstance().getPileById(currentPileId, new WebResponseHandler<Pile>(getActivity()) {

				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onError(Throwable e) {
					super.onError(e);
					tv_output_type.setText("未知");
				}

				@Override
				public void onFailure(WebResponse<Pile> response) {
					super.onFailure(response);
					tv_output_type.setText("未知");
				}

				@Override
				public void onSuccess(WebResponse<Pile> response) {
					super.onSuccess(response);
					Pile pile = response.getResultObj();
					if (pile == null) {
						tv_output_type.setText("未知");
					} else {
						DbHelper.getInstance(ChargeFragment.this.getActivity()).insertPile(pile);
						tv_output_type.setText(pile.getTypeAsString());
					}
				}

			});
		} else {
			tv_output_type.setText(pile.getTypeAsString());
		}
	}

	private void refreshStatus() {
		stopRefreshStatus();

		refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (BTManager.getInstance().isConnected()) {
					CmdManager.getInstance().acqDeviceState();
				} else {
					stopRefreshStatus();
				}
			}
		}, 0, 1000);
	}

	private void stopRefreshStatus() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	//弹出框处理回调
	@Override
	public void clickDialog(boolean isGprsAndBle, boolean isOwner, boolean isRightOperater) {
		Log.i("tag", "clickDialog");
		callBackForTwoSide(isGprsAndBle, isOwner, isRightOperater);
	}

	//弹出框处理回调
	@Override
	public void clickDialog() {
		//oneSide 确认监听
		callBackForOneSide();
	}

	private void callBackForTwoSide(boolean isGprsAndBle, boolean isOwner, boolean isRightOperater) {
		if (pileType == PILETYPE_FOR_BLE) {//如果属于蓝牙桩
			Log.i("tag","pileType == PILETYPE_FOR_BLE") ;
			if (isRightOperater) {//确定
				int id = rg_scan_ble_tab.getCheckedRadioButtonId();
				setRadioButtonCheckToBle(id);
			} else {//取消
			}
			setParamNull();
			if (mZxingManager != null) {
				mZxingManager.onResume();
			}
		} else {
			Log.i("tag","pileType != PILETYPE_FOR_BLE") ;
			if (isRightOperater) {//确定
				if (code != null && code.equals(WebResponse.CODE_CHARGING_FOR_OTHER)) {
					if (user_id != null) {
						code = null;
						Log.i("tag", "checkIfCharging for two side");
						removeCallbackThread(currentStatusRunnable);
						mHandler.removeCallbacks(ctr_resRunnable);
						checkIfCharging(user_id);
					}
					return;
				}
				else if (status != -1 && status == STATE_PILE_LINK_UNCHARGE) {
					status = -1;
					if (user_id != null && pileCode != null) {//gprs桩充电需要的是运行编码，运行编码就是桩ID
						Log.i("tag", "isRightOperater callBackForTwoSide to control");
						control(user_id, deviceId, pileCode, CONTROLLERACTION = START_CHARGE_ACTION, true);//开始充电
					} else {
						if (getUserVisibleHint()) {
							if (mZxingManager != null) {
								mZxingManager.onResume();
							}
						}
					}
					return;

				}
			} else {//取消
				setParamNull();
				if (getUserVisibleHint()) {
					if (mZxingManager != null) {
						mZxingManager.onResume();
					}
				}

			}
		}
	}

	private void callBackForOneSide() {
		if (ownerListenTenantIfCharging || ifOofLine) {
			ownerListenTenantIfCharging = false;
			ifOofLine = false;
			showRl_qrcode();
			int id = rg_scan_ble_tab.getCheckedRadioButtonId();
			setRadioButtonCheckToQr(id);
			setParamNull();
			if (mZxingManager != null) {
				mZxingManager.onResume();
			}
			return;
		}
		if (chargingError && !EasyActivityManager.getInstance().has(ChargeCompleteActivity.class)) {
			jumpIntoActivity();
			chargingError = false;
		} else {
			setParamNull();
			showRl_qrcode();
			int id = rg_scan_ble_tab.getCheckedRadioButtonId();
			setRadioButtonCheckToQr(id);
			if (mZxingManager != null) {
				mZxingManager.onResume();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("tag", "onActivityResult");
		if (resultCode != Activity.RESULT_OK) {
			return;
		} else {
			if (requestCode == TOCHARGECOMPLETE) {
				showRl_qrcode();
				int id = rg_scan_ble_tab.getCheckedRadioButtonId();
				setRadioButtonCheckToQr(id);
			}
		}
	}

	public void setViewType() {
		if (state == STATE_CHARGING || state == STATE_CONNECTING_BT || state == STATE_CONNECT_GUN || state == STATE_START_CHARGE) {
			return;
		} else {
			if (!ib_scan_qr_charge.isChecked()) {
				ib_scan_qr_charge.setChecked(true);
				showRl_qrcode();
			}
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("tag","paid onReceive") ;
			isChargingStatus = false;
			checkIfCharging = false;
			showRl_qrcode();
			int id = rg_scan_ble_tab.getCheckedRadioButtonId();
			setRadioButtonCheckToQr(id);
			setParamNull();
			if (mZxingManager != null) {
				mZxingManager.onResume();
			}

		}
	};

	private void setRadioButtonCheckToQr(int id) {
		switch (id) {
			case R.id.ib_scan_qr_charge:
				break;
			case R.id.ib_ble_charge:
				ib_scan_qr_charge.setChecked(true);//如果之前选中的是蓝牙的radioButton,则选中扫码
				break;
		}
	}

	private void setRadioButtonCheckToBle(int id) {
		switch (id) {
			case R.id.ib_scan_qr_charge:
				ib_ble_charge.setChecked(true);
				break;
			case R.id.ib_ble_charge:
				break;
		}
	}

	private void setParamNull() {
		if (ChargeFragment.this.deviceId != null) {
			ChargeFragment.this.deviceId = null;
		}
		if (ChargeFragment.this.pileCode != null) {
			ChargeFragment.this.pileCode = null;
		}
		if (ChargeFragment.this.orderId != null) {
			ChargeFragment.this.orderId = null;
		}
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		myReceiver = new ConnectionChangeReceiver();
		getActivity().registerReceiver(myReceiver, filter);
	}

	class ConnectionChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
				ToastUtil.show(getActivity(), R.string.message_charging_current_offline_tip);
				Log.i("tag", "disconnectivityManager=====");
				if (state == STATE_CHARGING && isChargingStatus && isLogin()) {
					setParamNull();
					setParamBoolean(false);
					removeCallbackThread(currentStatusRunnable);
					mHandler.removeCallbacks(ctr_resRunnable);
					showConnectBT();
//					showRl_qrcode();
					if (checkIfCharging) {
						checkIfCharging = false;
					}
					if (isChargingStatus) {
						isChargingStatus = false;
					}
					if (ifFinishCtrRes) {
						ifFinishCtrRes = false;
					}
					return;
				}
			} else {
				Log.i("tag", "connectivityManager=====");
				if (!BTManager.getInstance().isConnected() && ifFromBleChargingDisconnectBack && state == STATE_CONNECT_BT && isLogin()) {
					removeCallbackThread(currentStatusRunnable);
					mHandler.removeCallbacks(ctr_resRunnable);
					if (ifFromBleChargingDisconnectBack) {
						ifFromBleChargingDisconnectBack = false;
					}
					if (checkIfCharging) {
						checkIfCharging = false;
					}
					if (isChargingStatus) {
						isChargingStatus = false;
					}
					if (ifFinishCtrRes) {
						ifFinishCtrRes = false;
					}
					if (isLogin()) {
						checkIfCharging(user_id);
					}
					return;
				}
			}
		}
	}
}
