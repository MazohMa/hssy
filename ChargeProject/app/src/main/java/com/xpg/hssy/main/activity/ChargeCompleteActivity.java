package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.db.pojo.ChargeRecordCache;
import com.xpg.hssy.db.pojo.Key;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.User;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.ToPayDialog;
import com.xpg.hssy.dialog.WaterBlueDialogChargeFinishTip;
import com.xpg.hssy.dialog.WaterBlueDialogChargeFinishTip.DismissActivityClick;
import com.xpg.hssy.engine.CmdManager;
import com.xpg.hssy.engine.CmdManager.OnCmdListener;
import com.xpg.hssy.service.ChargeRecordSyncService;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mazoh
 * @version 2.4.0
 * @description
 */
public class ChargeCompleteActivity extends BaseActivity implements OnClickListener {


	private static final int COUNTSIZE = 10;
	private static final String BILL_FORMAT = "%.2f";
	private TextView tv_electric_quantity, tv_username, tv_start_time, tv_end_time, tv_money, tv_electric_serialNo, tv_pile_name, tv_price,
			tv_charge_time_period, tv_price_unit;
	private Button btn_ok, pay_back, pay_now;
	private RelativeLayout rl_price, rl_order_num, rl_pile_name, rl_start_time, rl_end_time, rl_charge_time_period, rl_money, rl_electric_serialNo,
			rl_bottom_ble, rl_bottom_gprs;
	private ChargeRecordCache record;
	private ChargeRecord chargeRecord;
	private List<ChargeRecord> chargeRecords;
	private int keyType = -1;
	private SharedPreferences sp;
	private String user_id = null;
	private String orderId;
	private ImageButton btn_left, btn_right;
	private WaterBlueDialogChargeFinishTip waterBlueDialogChargeFinishTip;
	private RelativeLayout rl_charge_free, rl_bottom;
	private TextView tv_order_num;
	private LinearLayout ll_record;
	private LoadingDialog loadingDialog = null;
	private boolean ISGPRS = false;
	private String pile_id = null;
	private boolean ifTocheckRepeat = false;
	private ChargeOrder chargeOrder = null;
	private boolean isOwner = false;
	private int retryType = 1;

	private int count = 1;

	private ToPayDialog toPayDialog = null;
	private Timer mTimer = new Timer();// 定时器  ;
	private android.os.Handler mHandler = new android.os.Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					if (toPayDialog != null && toPayDialog.isShowing()) {
						toPayDialog.dismiss();
					}
					break;
				case 2:

					if (mTimer != null) {
						mTimer.cancel();
						mTimer = null;
					}

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ISGPRS) {
		} else {
			// 获取最新一条充电记录
			CmdManager.getInstance().acqHistoryLast();
		}
	}

	@Override
	protected void initData() {
		super.initData();
		sp = getSharedPreferences("config", Context.MODE_MULTI_PROCESS);
		user_id = sp.getString("user_id", null);
		Intent intent = getIntent();
		isOwner = intent.getBooleanExtra("isOwner", false);
		orderId = intent.getStringExtra(KEY.INTENT.ORDER_ID);
		pile_id = intent.getStringExtra(KEY.INTENT.PILE_ID);
		ISGPRS = intent.getBooleanExtra(KEY.INTENT.ISGPRS, false);

	}

	public void showUIforFamily() {
		rl_charge_free.setVisibility(View.VISIBLE);
		rl_electric_serialNo.setVisibility(View.VISIBLE);
		rl_start_time.setVisibility(View.VISIBLE);
		rl_end_time.setVisibility(View.VISIBLE);
		rl_charge_time_period.setVisibility(View.VISIBLE);
		ll_record.setVisibility(View.VISIBLE);
		rl_bottom.setVisibility(View.VISIBLE);

		rl_money.setVisibility(View.GONE);
		rl_price.setVisibility(View.GONE);
		rl_order_num.setVisibility(View.GONE);
		rl_pile_name.setVisibility(View.GONE);

	}

	public void hideUI() {
		rl_electric_serialNo.setVisibility(View.GONE);
		rl_start_time.setVisibility(View.GONE);
		rl_end_time.setVisibility(View.GONE);
		rl_charge_time_period.setVisibility(View.GONE);
		rl_bottom_ble.setVisibility(View.GONE);
		rl_bottom_gprs.setVisibility(View.GONE);

		rl_money.setVisibility(View.GONE);
		rl_price.setVisibility(View.GONE);
		rl_order_num.setVisibility(View.GONE);
		rl_pile_name.setVisibility(View.GONE);
		ll_record.setVisibility(View.GONE);
		rl_bottom.setVisibility(View.GONE);
	}

	public void showUIforTenant() {
		rl_charge_free.setVisibility(View.VISIBLE);
		rl_start_time.setVisibility(View.VISIBLE);
		rl_end_time.setVisibility(View.VISIBLE);
		rl_charge_time_period.setVisibility(View.VISIBLE);
		rl_money.setVisibility(View.VISIBLE);
		rl_price.setVisibility(View.VISIBLE);
		rl_order_num.setVisibility(View.VISIBLE);
		rl_pile_name.setVisibility(View.VISIBLE);
		rl_bottom.setVisibility(View.VISIBLE);

		rl_electric_serialNo.setVisibility(View.GONE);
		ll_record.setVisibility(View.GONE);
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_complete);
		setTitle("充电完成");
		rl_electric_serialNo = (RelativeLayout) findViewById(R.id.rl_electric_serialNo);
		rl_price = (RelativeLayout) findViewById(R.id.rl_price);
		rl_order_num = (RelativeLayout) findViewById(R.id.rl_order_num);
		rl_pile_name = (RelativeLayout) findViewById(R.id.rl_pile_name);
		rl_start_time = (RelativeLayout) findViewById(R.id.rl_start_time);
		rl_end_time = (RelativeLayout) findViewById(R.id.rl_end_time);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		rl_charge_time_period = (RelativeLayout) findViewById(R.id.rl_charge_time_period);
		rl_money = (RelativeLayout) findViewById(R.id.rl_money);
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_charge_time_period = (TextView) findViewById(R.id.tv_charge_time_period);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_pile_name = (TextView) findViewById(R.id.tv_pile_name);
		tv_electric_quantity = (TextView) findViewById(R.id.tv_electric_quantity);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_electric_serialNo = (TextView) findViewById(R.id.tv_electric_serialNo);
		tv_price_unit = (TextView) findViewById(R.id.tv_price_unit);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		pay_back = (Button) findViewById(R.id.pay_back);
		pay_now = (Button) findViewById(R.id.pay_now);

		ll_record = (LinearLayout) findViewById(R.id.ll_record);
		rl_charge_free = (RelativeLayout) findViewById(R.id.rl_charge_free);
		rl_bottom_ble = (RelativeLayout) findViewById(R.id.rl_bottom_ble);
		rl_bottom_gprs = (RelativeLayout) findViewById(R.id.rl_bottom_gprs);
		hideUI();// 隐藏UI
		if (ISGPRS) {
			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			loadingDialog = new LoadingDialog(self, R.string.loading);
			loadingDialog.showDialog();
			if (user_id != null && orderId != null && pile_id != null) {
				getOrder(user_id, orderId, pile_id, retryType);
			}
		} else {
			rl_bottom.setVisibility(View.VISIBLE);
			rl_bottom_ble.setVisibility(View.VISIBLE);
			rl_bottom_gprs.setVisibility(View.GONE);
			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
			loadingDialog = new LoadingDialog(self, R.string.loading);
			loadingDialog.showDialog();
		}
	}

	private Runnable orderChargeRunnable = new Runnable() {

		@Override
		public void run() {
			if (ifTocheckRepeat && retryType <= COUNTSIZE) {
				getOrder(user_id, orderId, pile_id, retryType);
			} else {
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			}
		}
	};


	private void getOrder(final String user_id, String orderId, String pileCode, int retryTimes) {
		WebAPIManager.getInstance().getOrder(user_id, orderId, retryTimes, pileCode, new WebResponseHandler<ChargeOrder>() {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				ifTocheckRepeat = false;
				mHandler.removeCallbacks(orderChargeRunnable);
			}

			@Override
			public void onFailure(WebResponse<ChargeOrder> response) {
				super.onFailure(response);
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				ifTocheckRepeat = false;
				mHandler.removeCallbacks(orderChargeRunnable);
			}

			@Override
			public void onSuccess(WebResponse<ChargeOrder> response) {
				super.onSuccess(response);
				if (response == null) {
					ifTocheckRepeat = false;
					mHandler.removeCallbacks(orderChargeRunnable);
					return;
				}

				chargeOrder = response.getResultObj();
				if (chargeOrder == null) {//订单为空
					ifTocheckRepeat = false;
					mHandler.removeCallbacks(orderChargeRunnable);
					return;
				}
				chargeRecords = chargeOrder.getChargeList();
				int orderType = chargeOrder.getOrderType();
				if (chargeRecords != null && chargeRecords.size() > 0) {//充电记录不为空
					if (loadingDialog != null && loadingDialog.isShowing()) {
						loadingDialog.dismiss();
					}
					ifTocheckRepeat = false;
					mHandler.removeCallbacks(orderChargeRunnable);

					//这里得到的充电记录list 的size为1
					chargeRecord = chargeRecords.get(0);
					if (orderType == 1 || orderType == 2 && chargeRecord != null) {
						showUIforFamily();
						rl_bottom.setVisibility(View.VISIBLE);
						rl_bottom_ble.setVisibility(View.VISIBLE);
						rl_bottom_gprs.setVisibility(View.GONE);


						tv_electric_serialNo.setText(chargeRecord.getSequence() + "");
						tv_electric_quantity.setText(String.format("%.2f", chargeRecord.getQuantity() / 100f));
						tv_start_time.setText(TimeUtil.format(chargeRecord.getStartTime(), "HH:mm"));
						tv_end_time.setText(TimeUtil.format(chargeRecord.getEndTime(), "HH:mm"));
						long time_period = (chargeRecord.getEndTime() - chargeRecord.getStartTime()) / 1000; // 共计秒数
						initTimeForCharge(time_period, tv_charge_time_period);
					} else {


						rl_bottom.setVisibility(View.GONE);
						rl_bottom_ble.setVisibility(View.GONE);
						rl_bottom_gprs.setVisibility(View.VISIBLE);


						rl_charge_free.setVisibility(View.VISIBLE);
						rl_start_time.setVisibility(View.VISIBLE);
						rl_end_time.setVisibility(View.VISIBLE);
						rl_charge_time_period.setVisibility(View.VISIBLE);
						rl_money.setVisibility(View.VISIBLE);
						rl_price.setVisibility(View.VISIBLE);
						rl_order_num.setVisibility(View.VISIBLE);
						rl_pile_name.setVisibility(View.VISIBLE);
						rl_electric_serialNo.setVisibility(View.GONE);
						ll_record.setVisibility(View.VISIBLE);




						tv_order_num.setText(ChargeCompleteActivity.this.orderId + "");
						CalculateUtil.infusePrice(tv_money, chargeOrder.getServicePay());
						tv_electric_quantity.setText(String.format("%.2f", chargeRecord.getQuantity() / 100f));
						tv_start_time.setText(TimeUtil.format(chargeRecord.getStartTime(), "HH:mm"));
						tv_end_time.setText(TimeUtil.format(chargeRecord.getEndTime(), "HH:mm"));
						long time_period = (chargeRecord.getEndTime() - chargeRecord.getStartTime()) / 1000; // 共计秒数
						initTimeForCharge(time_period, tv_charge_time_period);
						tv_price.setText(String.format("%.2f", chargeRecord.getChargePrice()) + "");
						tv_price_unit.setText(R.string.rmb_symbol);
						tv_pile_name.setText(chargeOrder.getPileName() + "");

						User user = DbHelper.getInstance(ChargeCompleteActivity.this).getUserDao().load(user_id);
						boolean contractFlag = user.getContractFlag();
						String priceStr = String.format(BILL_FORMAT, chargeOrder.getServicePay()) + "";
						if (priceStr.equals("") || priceStr.equals("0.00")) {
							rl_bottom.setVisibility(View.VISIBLE);
							rl_bottom_ble.setVisibility(View.VISIBLE);
							rl_bottom_gprs.setVisibility(View.GONE);
						} else {
							rl_bottom_ble.setVisibility(View.GONE);
							rl_bottom_gprs.setVisibility(View.VISIBLE);
							if (contractFlag) {//true代表绑定百度支付的代扣功能
								rl_bottom.setVisibility(View.GONE);
								//请求代扣接口
								payMoney(chargeRecord.getId());//充电记录id为参数
							} else {//没有绑定，显示bottom底部的按钮，手动支付
								rl_bottom.setVisibility(View.VISIBLE);
							}
						}
					}
				} else {//充电记录为空
					if (retryType == COUNTSIZE) {
						mHandler.removeCallbacks(orderChargeRunnable);
						if (orderType == 1 || orderType == 2) {//家人桩主
							initDialog(true);
						} else {//租户
							initDialog(false);
						}
						return;
					}
					Log.i("tag", "轮询次数: " + retryType);
					retryType++;
					ifTocheckRepeat = true;
					mHandler.postDelayed(orderChargeRunnable, 3000);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void payMoney(long chargeRecordId) {
		WebAPIManager.getInstance().payMoney(chargeRecordId, new WebResponseHandler<Object>() {
			@Override
			public void onStart() {
				super.onStart();
				if (toPayDialog != null && toPayDialog.isShowing()) {
					toPayDialog.dismiss();
				}
				toPayDialog = new ToPayDialog(ChargeCompleteActivity.this);
				toPayDialog.getTv_tip().setText(R.string.pay_baidu_pocket);
				toPayDialog.getTv_soso().setText(". . .");
				toPayDialog.show();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				rl_bottom.setVisibility(View.VISIBLE);
				rl_bottom_ble.setVisibility(View.GONE);
				pay_now.setText(R.string.pay_other_way);

				//代扣失败
				if (toPayDialog != null && toPayDialog.isShowing()) {
					toPayDialog.getTv_tip().setText(R.string.baidu_pocket);
					toPayDialog.getTv_soso().setText(R.string.pay_fail);
					timerTask();//定时跳转，dismiss dialog
				}
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				rl_bottom.setVisibility(View.VISIBLE);
				rl_bottom_ble.setVisibility(View.GONE);
				pay_now.setText(R.string.pay_other_way);
				String code = response.getCode();
				if (code != null && code.equals(WebResponse.ENOUGH_CONTRACT_VALIDATE)) {
					ToastUtil.show(self, response.getMessage() + "");
					if (user_id != null) {//用户同步绑定百度代扣
						User user = DbHelper.getInstance(self).getUserDao().load(user_id);
						if (user != null) {
							user.setContractFlag(false);//解绑
							DbHelper.getInstance(self).getUserDao().update(user);
						}
					}
				}
				//代扣失败
				if (toPayDialog != null && toPayDialog.isShowing()) {
					toPayDialog.getTv_tip().setText(R.string.baidu_pocket);
					toPayDialog.getTv_soso().setText(R.string.pay_fail);
					timerTask();//定时跳转
				}
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				if (toPayDialog != null && toPayDialog.isShowing()) {
					toPayDialog.dismiss();
				}
				Intent intent = new Intent(ChargeCompleteActivity.this, PaidCompleteForRecordActivity.class);
				intent.putExtra("orderId", ChargeCompleteActivity.this.orderId);
				intent.putExtra("servicePay", chargeOrder.getServicePay());
				intent.putExtra("quantity", String.format("%.2f", chargeRecord.getQuantity() / 100f));
				intent.putExtra("startTime", TimeUtil.format(chargeRecord.getStartTime(), "HH:mm"));
				intent.putExtra("endTime", TimeUtil.format(chargeRecord.getEndTime(), "HH:mm"));
				intent.putExtra("time_period", (chargeRecord.getEndTime() - chargeRecord.getStartTime()) / 1000);
				intent.putExtra("chargePrice", String.format("%.2f", chargeRecord.getChargePrice()) + "");
				intent.putExtra("pileName", chargeOrder.getPileName() + "");
				ChargeCompleteActivity.this.startActivity(intent);
				ChargeCompleteActivity.this.finish();

			}
		});
	}

	public void timerTask() {
		//创建定时线程执行更新任务
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (toPayDialog != null && toPayDialog.isShowing()) {
					mHandler.sendEmptyMessage(1);// 向Handler发送消息
				} else {
					mHandler.sendEmptyMessage(2);// 向Handler发送消息停止继续执行
				}
			}
		}, 1000, 1000);// 定时任务
	}

	private void initDialog(boolean isOwner) {
		if (waterBlueDialogChargeFinishTip != null && waterBlueDialogChargeFinishTip.isShowing()) {
			waterBlueDialogChargeFinishTip.dismiss();
			waterBlueDialogChargeFinishTip = null;
		}
		waterBlueDialogChargeFinishTip = new WaterBlueDialogChargeFinishTip(self);
		if (isOwner) {
			waterBlueDialogChargeFinishTip.setContent("抱歉，获取充电信息失败，如有疑问，请您联系客服 400-655-6620");
		} else {
			waterBlueDialogChargeFinishTip.setContent("抱歉，获取充电信息失败，请您稍后在“我的订单”里对充电订单进行支付，如有疑问，请您联系客服 400-655-6620");
		}
		waterBlueDialogChargeFinishTip.show();
		waterBlueDialogChargeFinishTip.setDismissActivityClick(new DismissActivityClick() {
			@Override
			public void dismiss() {
				Intent intent = getIntent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_ok.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		pay_back.setOnClickListener(this);
		pay_now.setOnClickListener(this);
		if (ISGPRS) {
		} else {
			CmdManager.getInstance().addOnCmdListener(ocl);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				if (ISGPRS) {
					Intent intent = getIntent();
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					if (keyType == Key.TYPE_FAMILY_OLD || keyType == Key.TYPE_FAMILY || keyType == Key.TYPE_OWNER) {
						this.finish();
					} else {
						WaterBlueDialogChargeFinishTip waterBlueDialogChargeFinish = new WaterBlueDialogChargeFinishTip(self);
						waterBlueDialogChargeFinish.setDismissActivityClick(new DismissActivityClick() {
							@Override
							public void dismiss() {
								finish();
							}
						});
						waterBlueDialogChargeFinish.show();
					}
				}
				break;
			case R.id.btn_ok:
				if (ISGPRS) {
					Intent intent = getIntent();
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					if (keyType == Key.TYPE_FAMILY_OLD || keyType == Key.TYPE_FAMILY || keyType == Key.TYPE_OWNER) {
						this.finish();
					} else {
						WaterBlueDialogChargeFinishTip waterBlueDialogChargeFinish = new WaterBlueDialogChargeFinishTip(self);
						waterBlueDialogChargeFinish.setDismissActivityClick(new DismissActivityClick() {
							@Override
							public void dismiss() {
								finish();
							}
						});
						waterBlueDialogChargeFinish.show();
					}
				}
				break;
			case R.id.pay_back:
				Intent intent = getIntent();
				setResult(Activity.RESULT_OK, intent);
				finish();
				break;
			case R.id.pay_now:
				if (chargeOrder != null) {
					goPay(chargeOrder);
				}
				break;
			default:
				break;
		}
	}

	private void goPay(ChargeOrder chargeOrder) {

		List<ChargeRecord> chargeRecords = chargeOrder.getChargeList();
		StringBuffer ids = null;
		if (chargeRecords != null && chargeRecords.size() > 0) {
			Intent pay_intent = new Intent(this, ChoicePayWayActivity.class);
			ids = new StringBuffer();
			for (int i = 0; i < chargeRecords.size(); i++) {
				if (i == chargeRecords.size() - 1) {
					ids.append(chargeRecords.get(i).getId().toString());
					break;
				}
				ids.append(chargeRecords.get(i).getId().toString() + ",");
			}

			Log.i("ids", ids.toString());
			pay_intent.putExtra("id", ids.toString());
			startActivity(pay_intent);
			overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			Intent intent = getIntent();
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}

	private OnCmdListener ocl = new OnCmdListener() {
		/**
		 * 历史记录返回
		 */
		@Override
		protected void onAcqHistoryById(ChargeRecordCache record) {
			if (record == null) {
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
				return;
			}
			ChargeCompleteActivity.this.record = record;

			// 获得桩名
			String id = record.getPileId();
			orderId = record.getOrderId();
			tv_order_num.setText(orderId + "");
			keyType = CmdManager.getInstance().getKey().getKeyType();
			ChargeOrder chargeOrder = DbHelper.getInstance(self).getChargeOrderDao().load(orderId);
			if (chargeOrder != null) {
				Log.i("tag", " chargeOrderID" + chargeOrder.getOrderId());
				Log.i("tag", " getUserid" + chargeOrder.getUserid());
				Log.i("tag", " CurrrentgetUserid" + user_id);
			}
			// TODO 很有可能存在脏数据以及硬件问题，导致主人桩或家人桩也有订单号，但是正确的订单号一定是10开头
			if (chargeOrder == null || !chargeOrder.getUserid().equals(user_id)) {
				//主人、家人
				// 家人桩或主人桩
				showUIforFamily();
				Log.i("tag", "  家人桩或主人桩");
				if (loadingDialog != null && loadingDialog.isShowing()) {
					loadingDialog.dismiss();
				}
			} else {
				Log.i("tag", " 租户");
				// 租户
				showUIforTenant();
				if (isNetworkConnected()) {
					WebAPIManager.getInstance().getOrderByOrderId(user_id, orderId, new WebResponseHandler<ChargeOrder>(self) {
						@Override
						public void onStart() {
							super.onStart();
							if (loadingDialog != null && loadingDialog.isShowing()) {
								loadingDialog.dismiss();
							}
							loadingDialog = new LoadingDialog(self, R.string.loading);
							loadingDialog.showDialog();
						}

						@Override
						public void onError(Throwable e) {
							super.onError(e);
							Log.i("tag", "tag order onError");
							loadChargePirceFromLocal();
						}

						@Override
						public void onFailure(WebResponse<ChargeOrder> response) {
							super.onFailure(response);
							Log.i("tag", "tag order onFailure");
							loadChargePirceFromLocal();
						}

						@Override
						public void onSuccess(WebResponse<ChargeOrder> response) {
							super.onSuccess(response);
							loadingDialog.dismiss();
							Log.i("tag", "tag order onSuccess");
							if (response.getResultObj() != null) {
								ChargeOrder order = response.getResultObj();
								float servicePay = order.getServicePay();
								CalculateUtil.infusePrice(tv_money, servicePay);
							} else {
								tv_money.setText("获取单价失败");
//                                        tv_cost_unit.setText("");
							}
						}
					});
				} else {
					Log.i("tag", " 家人或桩主");
					loadChargePirceFromLocal();
				}

			}
			Pile pile = DbHelper.getInstance(self).getPileDao().load(id);
			if (pile != null) {
				tv_pile_name.setText(pile.getPileNameAsString() + "");
			} else {
				tv_pile_name.setText("未知桩名");
			}
			tv_electric_serialNo.setText(record.getSequence() + "");
			User user = DbHelper.getInstance(self).getUserDao().load(record.getUserid());
			if (user != null) {
				tv_username.setText(user.getName());
			} else {
				tv_username.setText("未知用户");
			}
			tv_electric_quantity.setText(String.format("%.2f", record.getQuantity() / 100f));
			tv_start_time.setText(TimeUtil.format(record.getStartTime(), "HH:mm"));
			tv_end_time.setText(TimeUtil.format(record.getEndTime(), "HH:mm"));
			long time_period = (record.getEndTime() - record.getStartTime()) / 1000; // 共计秒数
			initTimeForCharge(time_period, tv_charge_time_period);

			// 注册一个广播，这个广播主要是用来获取service sendbroad过来的充电费用
			IntentFilter filter = new IntentFilter("data.broadcast.actionForChargePrice");
			registerReceiver(broadcastReceiver, filter);
			// 保存
//			DbHelper.getInstance(self).getChargeRecordCacheDao().insertOrReplace(record);
			// 上传
			Intent intent = new Intent(self, ChargeRecordSyncService.class);
			intent.putExtra("tv_electric_serialNo", record.getSequence());
			startService(intent);
			Intent broadCastIntent = new Intent();
			broadCastIntent.setAction(ChargeRecordSyncService.RECEIVE_ACTION_IMMEDIATELE_SYN_RECORD_CACHE);
			broadCastIntent.putExtra(ChargeRecordSyncService.KEY_PER_SYN_RECORD_CACHE, record);
			sendBroadcast(broadCastIntent);
		}


	};


	private void loadChargePirceFromLocal() {
		ChargeOrder order = DbHelper.getInstance(self).getChargeOrderDao().load(orderId);
		if (order == null) {
			tv_money.setText(R.string.load_price_fail);
		} else {
			float servicePay = order.getServicePay();
			CalculateUtil.infusePrice(tv_money, servicePay);
		}
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mTimer != null) {
			mTimer.cancel();// 程序退出时cancel timer
		}
		mHandler.removeCallbacks(orderChargeRunnable);
		CmdManager.getInstance().removeOnCmdListener(ocl);
		if (ISGPRS) {
		} else {
			unregisterReceiver(broadcastReceiver);
		}
		System.gc();
	}

	private void initTimeForCharge(long time_period, View view) {
		if (time_period < 60) {
			if (view instanceof TextView) {
				((TextView) view).setText(time_period + "秒");
			}
			return;
		}
		int MM = (int) time_period / 60; // 共计分钟数
		if (MM < 60) {
			if (MM < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText("0" + MM + "分");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(MM + "分");
				}
			}
		} else {
			int hh = (int) MM / 60; // 共计小时数
			int min = (int) MM % 60; // 截取分钟
			if (min < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + "0" + min + "分" + "");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + min + "分" + "");
				}
			}

		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 更新充电费用
			boolean ifSuccess = intent.getBooleanExtra(KEY.INTENT.SUCCESS, false);
			if (ifSuccess) {
				String chargePrice = intent.getStringExtra(KEY.INTENT.CHARGE_PRICE);
				if (TextUtils.isEmpty(chargePrice) || "null".equals(chargePrice)) {
					chargePrice = "0.00";
				}
				tv_price.setText(CalculateUtil.formatDefaultNumber(Double.valueOf(chargePrice)));
				tv_price_unit.setText(R.string.rmb_symbol);
			} else {
				ChargeOrder order = DbHelper.getInstance(self).getChargeOrderDao().load(orderId);
				if (order == null) {
					tv_price.setText("获取费用失败");
					tv_price_unit.setText("");
				} else {
					if (order.getServicePay() == null || record.getQuantity() == null) {
						tv_price.setText("0.00");
						tv_price_unit.setText(R.string.rmb_symbol);
					} else {
						tv_price.setText(CalculateUtil.formatDefaultNumber(record.getQuantity() / 100f * order.getServicePay()));
						tv_price_unit.setText(R.string.rmb_symbol);
					}
				}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("TAG", "onKeyDown" + "");
		if (ISGPRS) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Intent intent = getIntent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		}
		return true;
	}

}
