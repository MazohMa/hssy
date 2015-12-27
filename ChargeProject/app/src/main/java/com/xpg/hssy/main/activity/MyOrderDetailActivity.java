package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.easy.util.IntentUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.OrderInfoView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * @author Mazoh 我的订单管理详情页
 */
public class MyOrderDetailActivity extends BaseActivity {
	private static final String TAG = "MyOrderDetailActivity";
	private final static String START_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	private final static String END_TIME_FORMAT = "HH:mm";
	private ImageButton btn_left;
	private ImageButton btn_right;
	private TextView tv_center;
	private ChargeOrder order;
	private List<ChargeRecord> mList;
	private String orderId;
	private String phoneNumber = "";
	private Button btn_sub;
	private TextView order_no_tvs; // 订单号
	private TextView display_status_tvs;// 订单状态
	private final static int COMMANDINTENT = 5;
	private BDLocation mLocation = null;
	private RelativeLayout rl_order_details;
	private LinearLayout ll_order_info;
	private LayoutInflater mInflater;
	private boolean isUser = true;
	private StringBuffer ids = null;
	private Button btn_left_detail;
	private RelativeLayout ll_btn_content;
	private LoadingDialog loadingDialog = null;

	private void getLocation() {
		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String cityStr = location.getCity();
				if (cityStr != null) {
					mLocation = location;
				}
			}

		});
	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_sub.setOnClickListener(this);
		btn_left_detail.setOnClickListener(this);
	}

	@Override
	protected void initUI() {
		super.initUI();
		mInflater = LayoutInflater.from(this);
		View view = mInflater.inflate(R.layout.myorder_item_detail_layout, null);
		setContentView(view);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_center = (TextView) findViewById(R.id.tv_center);
		tv_center.setText("订单详情");
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_left.setOnClickListener(this);

		btn_left_detail = (Button) findViewById(R.id.btn_left_detail);
		btn_sub = (Button) findViewById(R.id.btn_sub);

		order_no_tvs = (TextView) findViewById(R.id.tv_order_number);
		display_status_tvs = (TextView) findViewById(R.id.tv_order_status_message);

		rl_order_details = (RelativeLayout) findViewById(R.id.rl_order_details);
		ll_order_info = (LinearLayout) findViewById(R.id.ll_order_info);
		ll_btn_content = (RelativeLayout) findViewById(R.id.ll_btn_content);

		rl_order_details.setVisibility(View.INVISIBLE);
		btn_sub.setVisibility(View.INVISIBLE);
		btn_left_detail.setVisibility(View.INVISIBLE);
//        order = (ChargeOrder) getIntent().getExtras().getSerializable("order");
		orderId = getIntent().getExtras().getString(KEY.INTENT.ORDER_ID, "0");

	}

	void init(String user_id, String owner_id) {
		//判断是否属于桩主还是租户
		if (!"".equals(user_id) && owner_id != null && !"".equals(owner_id)) {
			if (user_id.equals(owner_id)) {
				isUser = false;
			} else {
				isUser = true;
			}
		}

		order_no_tvs.setText(orderId);
		switch (order.getAction()) {
			case ChargeOrder.ACTION_WATTING:
				display_status_tvs.setText("待确认");
				btn_sub.setBackgroundResource(R.drawable.shape_btn_red);
				if (!isUser) {//桩主
					btn_left_detail.setVisibility(View.VISIBLE);
					btn_sub.setVisibility(View.VISIBLE);
					btn_left_detail.setBackgroundResource(R.drawable.shape_btn_red);
					btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
					btn_left_detail.setText("拒绝");
					btn_sub.setText("同意");
				} else {//租户
					btn_left_detail.setVisibility(View.GONE);
					btn_sub.setVisibility(View.VISIBLE);
					btn_sub.setText("取消");
				}
				break;
			case ChargeOrder.ACTION_CONFIRM:
				display_status_tvs.setText(R.string.status_confrimed);
				btn_left_detail.setVisibility(View.GONE);
				btn_sub.setVisibility(View.VISIBLE);
				if (isUser) {
					btn_sub.setText("导航");
					btn_sub.setBackgroundResource(R.drawable.shape_blue);
				} else {
					btn_sub.setText("联系对方");
					btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
				}
				break;
			case ChargeOrder.ACTION_REJECT:
				if (!isUser) {
					display_status_tvs.setText("已拒绝");
					btn_left_detail.setVisibility(View.GONE);
					btn_sub.setVisibility(View.GONE);
				} else {
					display_status_tvs.setText(R.string.owner_reject);
					btn_left_detail.setVisibility(View.GONE);
					btn_sub.setText("重新预约");
					btn_sub.setVisibility(View.VISIBLE);
					btn_sub.setBackgroundResource(R.drawable.shape_btn_red);
				}
				break;
			case ChargeOrder.ACTION_CANCEL:
				display_status_tvs.setText("已取消");
				btn_left_detail.setVisibility(View.GONE);
				btn_sub.setVisibility(View.VISIBLE);
				if (isUser) {
					btn_sub.setText("重新预约");
					btn_sub.setBackgroundResource(R.drawable.shape_btn_red);
				} else {
					btn_sub.setText("联系对方");
					btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
					btn_sub.setVisibility(View.GONE);
				}
				break;
			case ChargeOrder.ACTION_COMPLETE:
				display_status_tvs.setText("待付款");
				btn_left_detail.setVisibility(View.GONE);
				btn_sub.setVisibility(View.VISIBLE);
				btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
				if (!isUser) {
					btn_sub.setText("联系对方");
				} else {
					btn_sub.setText("去付款");
				}
				break;
			case ChargeOrder.ACTION_PAIED:
				display_status_tvs.setText("已付款");
				btn_left_detail.setVisibility(View.GONE);
				if (!isUser) {
					ll_btn_content.setVisibility(View.GONE);
				} else {
					ll_btn_content.setVisibility(View.VISIBLE);
					btn_sub.setVisibility(View.VISIBLE);
					btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
					btn_sub.setText("评价分享");
				}
				break;
			case ChargeOrder.ACTION_TIMEOUT:
				display_status_tvs.setText("已过期");
				btn_left_detail.setVisibility(View.GONE);
				btn_sub.setVisibility(View.VISIBLE);
				if (!isUser) {
					btn_sub.setText("联系对方");
					btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
				} else {
					btn_sub.setText("重新预约");
					btn_sub.setBackgroundResource(R.drawable.shape_btn_red);
				}

				break;
			case ChargeOrder.ACTION_COMMANDED:
				display_status_tvs.setText("已完成");
				btn_left_detail.setVisibility(View.GONE);
				btn_sub.setVisibility(View.GONE);
				break;
			default:
				break;
		}
		View view;


		//TODO begin common part
		if (isUser) {
			view = OrderInfoView.createDetailItemTop(true, this, order.getPileName(), order.getContactPhone(), order.getLocation());
			view.findViewById(R.id.iv_order_info_phone).setOnClickListener(this);
			view.findViewById(R.id.iv_order_info_location).setOnClickListener(this);
			ll_order_info.addView(view);
		} else {
			view = OrderInfoView.createDetailItemTop(false, this, order.getTenantName(), order.getTenantPhone());
			view.findViewById(R.id.iv_order_info_phone).setOnClickListener(this);
			ll_order_info.addView(view);
		}


		Long startTime = order.getStartTime();
		Long endTime = order.getEndTime();
		if (startTime != null && endTime != null) {

			view = OrderInfoView.createTextItemMid(mInflater, "预约时段", createTimeString(startTime, endTime));
			ll_order_info.addView(view);
		}
		Float servicePrice = order.getServicePay();
		if (servicePrice != null) {
			view = OrderInfoView.createTextItemMid(mInflater, "充电价格", CalculateUtil.formatPirce(servicePrice));
			ll_order_info.addView(view);
		}
		Long orderTime = order.getOrderTime();
		if (orderTime != null) {
			view = OrderInfoView.createTextItemMid(mInflater, "订单生成", TimeUtil.format(orderTime, START_TIME_FORMAT));
			ll_order_info.addView(view);
		}
		View space = OrderInfoView.createTextItem(mInflater, "", "");
		ll_order_info.addView(space);
		//TODO end common part


//TODO 充电信息 充电记录不为空情况，桩主租户都要显示
		if (mList == null || mList.size() == 0) {//充电记录为null或者为零的情况
			//订单在已确认状态下,过了预约时间,但是又没有充电记录
			if (order.getAction() == ChargeOrder.ACTION_CONFIRM && null != endTime && System.currentTimeMillis() > endTime) {
				view = OrderInfoView.createTextItemTitle(mInflater, "充电信息");
				ll_order_info.addView(view);
				ll_order_info.addView(OrderInfoView.createTextItem(getLayoutInflater(), "", getString(R.string.out_of_charge_time_order_tips)));
			}
			return;
		}
		//充电记录不为null的情况
		view = OrderInfoView.createTextItemTitle(mInflater, "充电信息");
		ll_order_info.addView(view);
		Long sTime;
		Long eTime;
		Long difTime = 0L;
		float quantity = 0;
		float chPrice = 0;
		float payPrice = 0;
		for (ChargeRecord chargeRecord : mList) {
			sTime = chargeRecord.getStartTime();
			eTime = chargeRecord.getEndTime();
			difTime += (eTime - sTime);
			quantity += chargeRecord.getQuantity() / 100f;
			if (chargeRecord.getChargePrice() != null) {
				chPrice += chargeRecord.getChargePrice();
				if (chargeRecord.getPayTime() != null) {
					payPrice += chargeRecord.getChargePrice();
				}
			}
		}

		//TODO BEGIN充电记录条数等于1情况
		if (mList.size() == 1) {
			ChargeRecord onlyRecord = mList.get(0);
			view = OrderInfoView.createTextItemMid(mInflater, "充电时段", createTimeString(onlyRecord.getStartTime(), onlyRecord.getEndTime()));
			ll_order_info.addView(view);
			view = OrderInfoView.createTextItemMid(mInflater, "充电时长", TimeUtil.initTime(difTime / 1000));
			ll_order_info.addView(view);

			view = OrderInfoView.createTextItemMid(mInflater, "充电电量", CalculateUtil.formatQuantity(quantity));
			ll_order_info.addView(view);

			view = OrderInfoView.createTextItemMid(mInflater, "充电费用", CalculateUtil.formatBill(chPrice));
			ll_order_info.addView(view);

			//TODO END充电记录条数等于1情况
		} else if (mList.size() > 1) {
			//充电记录条数大于1情况
			for (int i = 0; i < mList.size(); i++) {
				String timeString = "";
				if (mList.get(i).getStartTime() != null && mList.get(i).getEndTime() != null) {
					timeString = createTimeString(mList.get(i).getStartTime(), mList.get(i).getEndTime());
				}
				String quantity1 = CalculateUtil.formatQuantity(mList.get(i).getQuantity() / 100f);
				float bill = ((int) (mList.get(i).getChargePrice() * 100)) / 100f;
				View item = OrderInfoView.createChargeDetailItemForOrder(mInflater, i + 1, timeString, quantity1, CalculateUtil.formatBill(bill));
				ll_order_info.addView(item);
			}
			// 总费用
			ll_order_info.addView(OrderInfoView.createLine(mInflater));
			View view_bill = OrderInfoView.createTextItemBillForPileOwner(mInflater, "  总  费  用", CalculateUtil.formatBill(chPrice));
			ll_order_info.addView(view_bill);
			String btn_text = this.getResources().getString(R.string.user_all_pay);
		}


		//TODO BEGIN 存在充电记录都需要显示的部分,非已确认部分
		if (order.getAction() != ChargeOrder.ACTION_CONFIRM) {
			view = OrderInfoView.createTextItemTitle(mInflater, "付款信息");
			ll_order_info.addView(view);
			view = OrderInfoView.createTextItemMid(mInflater, "已付费用", CalculateUtil.formatBill(payPrice));
			ll_order_info.addView(view);
			Long payTime = order.getPayTime();
			if (payTime != null) {
				view = OrderInfoView.createTextItemMid(mInflater, "付款时间", TimeUtil.format(payTime, START_TIME_FORMAT));
				ll_order_info.addView(view);
			}
			View spaces = OrderInfoView.createTextItem(mInflater, "", "");
			ll_order_info.addView(spaces);
			if (order.getAction() == ChargeOrder.ACTION_COMMANDED) {
				view = OrderInfoView.createCommand(mInflater, isUser ? "我的评价" : "对方评价", order.getPileLevel(), order.getEvaluateDetial() == null ? "暂无评论" :
						order.getEvaluateDetial());
				ll_order_info.addView(view);
				View spacess = OrderInfoView.createTextItem(mInflater, "", "");
				ll_order_info.addView(spacess);
			}
		}
		//TODO END
	}

	@NonNull
	private String createTimeString(Long startTime, Long endTime) {
		String start = TimeUtil.format(startTime, START_TIME_FORMAT);
		String end = TimeUtil.format(endTime, END_TIME_FORMAT);
		end = end.equals("00:00") ? "24:00" : end;
		return start + "-" + end;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
			overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.iv_order_info_phone:
				String phone = "";
				if (isUser) {
					phone = order.getContactPhone();
					Log.i("tag", phone + "");
				} else {
					phone = order.getTenantPhone();
					Log.i("tag", phone + "");
				}
				if (!TextUtils.isEmpty(phone)) {
					IntentUtil.openTelephone(self, phone);
				} else {
					Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
					self.startActivity(intent);
				}
				break;
			case R.id.btn_left_detail:
				if (!isUser) {//桩主
					switch (order.getAction()) {
						case ChargeOrder.ACTION_WATTING: {
							loadingDialog = new LoadingDialog(self, R.string.loading);
							loadingDialog.showDialog();
							WebAPIManager.getInstance().rejectOrder(order.getOrderId(), new WebResponseHandler<ChargeOrder>(self) {
								@Override
								public void onError(Throwable e) {
									super.onError(e);
									loadingDialog.dismiss();
									TipsUtil.showTips(self, e);
								}

								@Override
								public void onFailure(WebResponse<ChargeOrder> response) {
									super.onFailure(response);
									loadingDialog.dismiss();
									TipsUtil.showTips(self, response);
									display_status_tvs.setText(R.string.status_canceled);
									btn_sub.setVisibility(View.GONE);
									btn_left_detail.setVisibility(View.GONE);
								}

								@Override
								public void onSuccess(WebResponse<ChargeOrder> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();
									order.setAction(response.getResultObj().getAction());
									display_status_tvs.setText("已拒绝");
									btn_left_detail.setVisibility(View.GONE);
									btn_sub.setVisibility(View.GONE);
//                                            btn_sub.setText("联系对方");
//                                            btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);
								}


							});
						}
					}
				} else {//租户
					//btn_left_detail为gone
				}
				break;

			case R.id.btn_sub:
				Intent i = new Intent();
				i.putExtra(KEY.INTENT.PILE_ID, order.getPileId());
				if (!isUser) {
					//桩主
					switch (order.getAction()) {
						case ChargeOrder.ACTION_WATTING:
							loadingDialog = new LoadingDialog(self, R.string.loading);
							loadingDialog.showDialog();
							WebAPIManager.getInstance().agreeOrder(order.getOrderId(), new WebResponseHandler<ChargeOrder>(self) {
								@Override
								public void onError(Throwable e) {
									super.onError(e);
									loadingDialog.dismiss();
									TipsUtil.showTips(self, e);
								}

								@Override
								public void onFailure(WebResponse<ChargeOrder> response) {
									super.onFailure(response);
									loadingDialog.dismiss();

									TipsUtil.showTips(self, response);
									display_status_tvs.setText(R.string.status_canceled);
									btn_sub.setVisibility(View.GONE);
									btn_left_detail.setVisibility(View.GONE);
								}

								@Override
								public void onSuccess(WebResponse<ChargeOrder> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();
									order.setAction(response.getResultObj().getAction());
									display_status_tvs.setText("已确定");
									btn_left_detail.setVisibility(View.GONE);
									btn_sub.setText("联系对方");
									btn_sub.setBackgroundResource(R.drawable.shape_btn_water_blue);

								}
							});
							break;


						// 这些状态下都是联系车主
						case ChargeOrder.ACTION_REJECT:
						case ChargeOrder.ACTION_CONFIRM:
						case ChargeOrder.ACTION_CANCEL:
						case ChargeOrder.ACTION_COMPLETE:
						case ChargeOrder.ACTION_PAIED:
						case ChargeOrder.ACTION_TIMEOUT:
							String phoneNumber = order.getTenantPhone();
							// 打开拨号功能;
							if (!TextUtils.isEmpty(phoneNumber)) {
								IntentUtil.openTelephone(self, phoneNumber);
							} else {
								Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
								self.startActivity(intent);
							}
							break;
						default:
							break;

					}
				} else {//租户
					switch (order.getAction()) {
						case ChargeOrder.ACTION_COMPLETE: {
							goPay();
							break;
						}
						case ChargeOrder.ACTION_PAIED: {
							goCommand();
							break;
						}
						case ChargeOrder.ACTION_WATTING:
							cancelOrder();
							break;
						case ChargeOrder.ACTION_CONFIRM:
							toNavigation();
							break;
						case ChargeOrder.ACTION_REJECT:
						case ChargeOrder.ACTION_CANCEL:
						case ChargeOrder.ACTION_TIMEOUT:
							Intent intentToPileInfo1 = new Intent(self, PileInfoNewActivity.class);
							intentToPileInfo1.putExtra(KEY.INTENT.PILE_ID, order.getPileId());
							self.startActivity(intentToPileInfo1);
							break;
						default:
							break;
					}

				}
				break;
			case R.id.iv_order_info_location:
				toNavigation();
				break;
			default:
				break;
		}
	}

	private void toNavigation() {
		Intent intent = new Intent(self, OrderNavigationActivity.class);
		intent.putExtra("pileId", order.getPileId());
		intent.putExtra("latitude", order.getLatitude());
		intent.putExtra("longitude", order.getLongitude());
		self.startActivity(intent);
	}

	/*
	 * 去评价
	 */
	private void goCommand() {
		Intent _intent = new Intent(MyOrderDetailActivity.this, MultipleEvaluateActivity.class);

		_intent.putExtra(KEY.INTENT.ORDER_ID, order.getOrderId());
		_intent.putExtra(KEY.INTENT.PILE_ID, order.getPileId());
//		_intent.putExtra(KEY.INTENT.USER_ID, order.getUserid());
		_intent.putExtra(KEY.INTENT.PILE_NAME, order.getPileName());
		/**
		 * temp 目前只有私人桩订单，所以传过去的都是桩类型评价
		 */
		_intent.putExtra(KEY.INTENT.EVALUATE_TYPE, MultipleEvaluateActivity.EVALUATE_TYPE_PILE);
//		_intent.putExtra("pileName", order.getPileName());
//		_intent.putExtra("ContactName", order.getContactName());
//		_intent.putExtra("ContactPhone", order.getContactPhone());
//		_intent.putExtra("shareStartTime", order.getStartTime());
//		_intent.putExtra("shareEndTime", order.getEndTime());

		self.startActivityForResult(_intent, COMMANDINTENT);
		self.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
	}

	/*
	 * 去付款
	 */

	private void goPay() {

		List<ChargeRecord> chargeRecords = order.getChargeList();

		if (chargeRecords != null && chargeRecords.size() > 0) {
			Intent pay_intent = new Intent(this, ChoicePayWayActivity.class);
			caculateIds(chargeRecords);
			pay_intent.putExtra(KEY.INTENT.KEY_ID, ids.toString());
			pay_intent.putExtra(KEY.INTENT.KEY_TRADE_ORDER, order.getOrderId());
			pay_intent.putExtra(KEY.INTENT.KEY_TOTAL_FEE, order.getChargePrice());
			this.startActivityForResult(pay_intent, MyOrderFragmentActivity.REQUEST_REFRESH);
			this.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
		}
	}

	// 拼接充电记录id
	private void caculateIds(List<ChargeRecord> chargeRecords) {
		ids = new StringBuffer();
		for (int i = 0; i < chargeRecords.size(); i++) {
			if (i == chargeRecords.size() - 1) {
				ids.append(chargeRecords.get(i).getId().toString());
				break;
			}
			ids.append(chargeRecords.get(i).getId().toString() + ",");
		}
	}

	/*
	 * 拨打电话功能
	 */
	private void callphone(String phoneNumber) {
		if (!TextUtils.isEmpty(phoneNumber)) {
			IntentUtil.openTelephone(MyOrderDetailActivity.this, phoneNumber);
		} else {
			Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
			MyOrderDetailActivity.this.startActivity(intent);
		}
	}

	/*
	 * 取消订单
	 */
	private void cancelOrder() {
		WebAPIManager.getInstance().cancelOrder(order.getOrderId(), new WebResponseHandler<ChargeOrder>() {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(MyOrderDetailActivity.this, e);
			}

			@Override
			public void onFailure(WebResponse<ChargeOrder> response) {
				super.onFailure(response);
				TipsUtil.showTips(MyOrderDetailActivity.this, response);
				if (response.getCode().equals(WebResponse.CODE_ORDER_CIRFORM_CANNOT_COMFIRM)) { //桩主已确认订单
					display_status_tvs.setText(R.string.status_confrimed);
				} else if (response.getCode().equals(WebResponse.CODE_ORDER_REFUSE_CANNOT_COMFIRM)) {//桩主已拒绝订单
					display_status_tvs.setText(R.string.owner_reject);
				}
				btn_sub.setVisibility(View.GONE);
			}

			@Override
			public void onSuccess(WebResponse<ChargeOrder> response) {
				super.onSuccess(response);
				response.getResultObj().setPileName(order.getPileName());
				response.getResultObj().setLocation(order.getLocation());
				TipsUtil.showTips(MyOrderDetailActivity.this, response);
				display_status_tvs.setText("已取消");
				btn_sub.setVisibility(View.GONE);
			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		ll_order_info.removeAllViews();
		final String user_id = new SPFile(self, "config").getString("user_id", "");
		//TODO 这里只能网络获取order，因为订单状态随时会改变
		if (orderId != null && !orderId.equals("")) {
			WebAPIManager.getInstance().getOrderByOrderId(user_id, orderId, new WebResponseHandler<ChargeOrder>() {

				@Override
				public void onStart() {
					super.onStart();
					loadingDialog = new LoadingDialog(self, R.string.loading);
					loadingDialog.showDialog();
				}

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
					order = (ChargeOrder) response.getResultObj();
					if (order == null) return;
					mList = order.getChargeList();
					final String onwer_id = order.getOwnerId();
					rl_order_details.setVisibility(View.VISIBLE);
					init(user_id, onwer_id);
					Log.i("tag ==getOrder", "from server");
				}

				@Override
				public void onFinish() {
					super.onFinish();
					loadingDialog.dismiss();
				}
			});
		} else {
			ToastUtil.show(this, "订单详情失败，请稍后再试");
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {

	}

}
