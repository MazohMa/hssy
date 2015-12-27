package com.xpg.hssy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.easy.adapter.EasyAdapter;
import com.easy.util.IntentUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.main.activity.ChoicePayWayActivity;
import com.xpg.hssy.main.activity.MultipleEvaluateActivity;
import com.xpg.hssy.main.activity.MyOrderFragmentActivity;
import com.xpg.hssy.main.activity.OrderNavigationActivity;
import com.xpg.hssy.main.activity.PileInfoNewActivity;
import com.xpg.hssy.main.fragment.callbackinterface.IAppointOperater;
import com.xpg.hssy.main.fragment.callbackinterface.ItemForNoneOrderLayoutOperater;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.OrderInfoView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh 订单适配器
 */
public class MyOrdersAdapter extends EasyAdapter<ChargeOrder> {
	private final static String START_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	private final static String END_TIME_FORMAT = "HH:mm";
	private LayoutInflater mInflater;
	private Context context;
	private StringBuffer ids;
	private IAppointOperater listen;
	private ItemForNoneOrderLayoutOperater itemForNoneOrderLayoutOperater;

	private boolean isShowAll = false;

	public MyOrdersAdapter(ItemForNoneOrderLayoutOperater itemForNoneOrderLayoutOperater, IAppointOperater listen, Context context, List<ChargeOrder>
			pileOrderList) {
		super(context, pileOrderList);
		this.context = context;
		this.listen = listen;
		this.itemForNoneOrderLayoutOperater = itemForNoneOrderLayoutOperater;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	protected ViewHolder newHolder() {
		return new mViewHolder();
	}

	class mViewHolder extends ViewHolder {


		public mViewHolder() {
			super();
			setEvent();
		}

		//		private ChargeOrder pileOrder;
		private String phoneNumber = "";
		private OrderInfoView orderInfoView;
		private OnClickListener listener;
//		private LinearLayout check_box_layout;
//		private CheckBox choice_edit;

		@Override
		protected void update() {
			// position由getView()提供；
			ChargeOrder pileOrder = get(position);
			// 初始化订单item
			orderInfoView.setOrderNumber(pileOrder.getOrderId());
			orderInfoView.setBigTitle(pileOrder.getPileName());
			orderInfoView.setDateVisibity(View.VISIBLE);
			orderInfoView.setDate(TimeUtil.format(pileOrder.getOrderTime(), START_TIME_FORMAT));
			orderInfoView.clearOrderInfo();
			View view = OrderInfoView.createTextItem(mInflater, "电桩名称\t", pileOrder.getPileName());
			orderInfoView.addOrderInfo(view);
			view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.pile_location), pileOrder.getLocation());
			orderInfoView.addOrderInfo(view);
			if (getMode() == EasyAdapter.MODE_CHECK_BOX) {
				if (itemSelected != null) {
					itemSelected.cancelCollect((ArrayList<ChargeOrder>) getSelectedItems());
				}
				if (isSelected) {
					orderInfoView.setChecked(true);
				} else {
					orderInfoView.setChecked(false);
				}
				orderInfoView.showChoiceEdit(true);
			} else if (getMode() == EasyAdapter.MODE_NON) {
				orderInfoView.showChoiceEdit(false);
			}

			orderInfoView.showLeftButton(false);
			orderInfoView.showRightButton(false);

			// 根据状态配置
			switch (pileOrder.getAction()) {
				case ChargeOrder.ACTION_WATTING: {
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						StringBuilder strb = new StringBuilder();
						strb.append(TimeUtil.format(pileOrder.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(pileOrder.getEndTime(),
								END_TIME_FORMAT));
						view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.appointment_time), strb.toString());
						orderInfoView.addOrderInfo(view);
					}
					view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
					orderInfoView.addOrderInfo(view);
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_wait_for_confirm));

					orderInfoView.setRightButton(R.drawable.shape_btn_red, "取消", listener);
					orderInfoView.showLeftButton(false);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_CONFIRM: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();

					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
							ChargeRecord chargeRecord = chargeRcordList.get(0);
							StringBuilder strb = new StringBuilder();
							strb.append(TimeUtil.format(chargeRecord.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(chargeRecord
									.getEndTime(), END_TIME_FORMAT));
							view = OrderInfoView.createTextItem(mInflater, "充电时段", strb.toString());
							orderInfoView.addOrderInfo(view);
							String quantity = CalculateUtil.formatQuantity(chargeRecord.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(pileOrder.getChargePrice());
							String single_price = CalculateUtil.formatPirce(pileOrder.getServicePay());
							view = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", single_price);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(view);
						} else {
							float bill_sum = 0f;
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								StringBuilder strb = new StringBuilder();
								if (record.getStartTime() != null && record.getEndTime() != null) {
									strb.append(TimeUtil.format(record.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(record
											.getEndTime(), END_TIME_FORMAT));
								}
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								float bill = ((int) (record.getChargePrice() * 100)) / 100f;
								bill_sum += bill;
								View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, strb.toString(), quantity, "￥ " + String.format
										("%" + ".2f", bill));
								orderInfoView.addOrderInfo(item);
							}
							// 总费用
							orderInfoView.addOrderInfo(OrderInfoView.createLine(mInflater));
							View view_bill = OrderInfoView.createTextItemBill(mInflater, context.getString(R.string.total_price), "￥ " + String.format("%.2f",
									bill_sum));
							orderInfoView.addOrderInfo(view_bill);
						}
					} else {
						if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
							StringBuilder strb = new StringBuilder();
							strb.append(TimeUtil.format(pileOrder.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(pileOrder.getEndTime
									(), END_TIME_FORMAT));

							view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.appointment_time), strb.toString());
							orderInfoView.addOrderInfo(view);
						}
						view = OrderInfoView.createTextItem(mInflater, "充电价格", "￥ " + String.format("%.2f", pileOrder.getServicePay()) + "/kWh");
						orderInfoView.addOrderInfo(view);
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_confrimed));
					orderInfoView.setRightButton(R.drawable.shape_blue, context.getResources().getString(R.string.navigation), listener);
					orderInfoView.showLeftButton(false);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_REJECT: {

					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						StringBuilder strb = new StringBuilder();
						strb.append(TimeUtil.format(pileOrder.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(pileOrder.getEndTime(),
								END_TIME_FORMAT));
						view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.appointment_time), strb.toString());
						orderInfoView.addOrderInfo(view);
					}
					view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
					orderInfoView.addOrderInfo(view);

					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getString(R.string.owner_reject));
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_red, "重新预约", listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_CANCEL: {
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						StringBuilder strb = new StringBuilder();
						strb.append(TimeUtil.format(pileOrder.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(pileOrder.getEndTime(),
								END_TIME_FORMAT));
						view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.appointment_time), strb.toString());
						orderInfoView.addOrderInfo(view);
					}
					view = OrderInfoView.createTextItem(mInflater, "充电价格", String.format("￥ %.2f", pileOrder.getServicePay()) + "/kWh");
					orderInfoView.addOrderInfo(view);
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_canceled));
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_red, context.getResources().getString(R.string.contact_re_appointment), listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_COMPLETE: {
					orderInfoView.clearOrderInfo();
					orderInfoView.setOrderNumber(pileOrder.getOrderId());
					orderInfoView.setBigTitle(pileOrder.getPileName());
					orderInfoView.setDateVisibity(View.VISIBLE);
					orderInfoView.setDate(TimeUtil.format(pileOrder.getOrderTime(), START_TIME_FORMAT));
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_wait_to_play));
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					float bill_sum = 0f;
					String btn_text = "去付款";
					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
							ChargeRecord chargeRecord = chargeRcordList.get(0);
							View view1 = OrderInfoView.createTextItem(mInflater, "电桩名称", pileOrder.getPileName());
							orderInfoView.addOrderInfo(view1);
							View view2 = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.pile_location), pileOrder
									.getLocation());
							orderInfoView.addOrderInfo(view2);
							StringBuilder strb = new StringBuilder();
							strb.append(TimeUtil.format(chargeRecord.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(chargeRecord
									.getEndTime(), END_TIME_FORMAT));
							view = OrderInfoView.createTextItem(mInflater, "充电时段", strb.toString());
							orderInfoView.addOrderInfo(view);
							String quantity = CalculateUtil.formatQuantity(chargeRecord.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(pileOrder.getChargePrice());
							String single_price = CalculateUtil.formatPirce(pileOrder.getServicePay());
							view = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", single_price);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(view);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "电桩名称", pileOrder.getPileName());
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.pile_location), pileOrder.getLocation());
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);

							for (int i = 0; i < chargeRcordList.size(); i++) {

								ChargeRecord record = chargeRcordList.get(i);
								StringBuilder strb = new StringBuilder();
								if (record.getStartTime() != null && record.getEndTime() != null) {
									strb.append(TimeUtil.format(record.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(record
											.getEndTime(), END_TIME_FORMAT));
								}
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								float bill = ((int) (record.getChargePrice() * 100)) / 100f;
								bill_sum += bill;
								View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, strb.toString(), quantity, "￥ " + String.format
										("%" + ".2f", bill));
								orderInfoView.addOrderInfo(item);
							}
							// 总费用
							orderInfoView.addOrderInfo(OrderInfoView.createLine(mInflater));
							View view_bill = OrderInfoView.createTextItemBill(mInflater, context.getString(R.string.total_price), "￥ " + String.format("%.2f",
									bill_sum));
							orderInfoView.addOrderInfo(view_bill);
							btn_text = context.getResources().getString(R.string.user_all_pay);
						}
					}
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, btn_text, listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_PAIED: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
							ChargeRecord chargeRecord = chargeRcordList.get(0);
							StringBuilder strb = new StringBuilder();
							strb.append(TimeUtil.format(chargeRecord.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(chargeRecord
									.getEndTime(), END_TIME_FORMAT));
							view = OrderInfoView.createTextItem(mInflater, "充电时段", strb.toString());
							orderInfoView.addOrderInfo(view);
							view.setVisibility(View.VISIBLE);
							String quantity = CalculateUtil.formatQuantity(chargeRecord.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(pileOrder.getChargePrice());
							String single_price = CalculateUtil.formatPirce(pileOrder.getServicePay());
							view = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(view);

							view = OrderInfoView.createTextItem(mInflater, "充电价格", single_price);
							orderInfoView.addOrderInfo(view);

							view = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(view);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								StringBuilder strb = new StringBuilder();
								strb.append(TimeUtil.format(record.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(record.getEndTime(),
										END_TIME_FORMAT));
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(pileOrder.getChargePrice());

								View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, strb.toString(), quantity, bill);
								orderInfoView.addOrderInfo(item);
							}
							// 总费用
							orderInfoView.addOrderInfo(OrderInfoView.createLine(mInflater));
							View view_bill = OrderInfoView.createTextItemBill(mInflater, context.getString(R.string.total_price), "￥ " + String.format("%.2f",
									pileOrder.getChargePrice()));
							orderInfoView.addOrderInfo(view_bill);
						}
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_paid));
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.go_to_evaluate), listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_TIMEOUT: {
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						StringBuilder strb = new StringBuilder();
						strb.append(TimeUtil.format(pileOrder.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(pileOrder.getEndTime(),
								END_TIME_FORMAT));
						view = OrderInfoView.createTextItem(mInflater, context.getResources().getString(R.string.appointment_time), strb.toString());
						orderInfoView.addOrderInfo(view);
					}
					view = OrderInfoView.createTextItem(mInflater, "充电价格", String.format("￥ %.2f", pileOrder.getServicePay()) + "/kWh");
					orderInfoView.addOrderInfo(view);
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_overdue));

					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_red, context.getResources().getString(R.string.contact_re_appointment), listener);
					orderInfoView.showRightButton(true);
					break;
				}

				case ChargeOrder.ACTION_COMMANDED: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					if (chargeRcordList != null) {
						if (chargeRcordList.size() == 1) {
							ChargeRecord chargeRecord = chargeRcordList.get(0);
							StringBuilder strb = new StringBuilder();
							strb.append(TimeUtil.format(chargeRecord.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(chargeRecord
									.getEndTime(), END_TIME_FORMAT));
							view = OrderInfoView.createTextItem(mInflater, "充电时段", strb.toString());
							orderInfoView.addOrderInfo(view);
							view.setVisibility(View.VISIBLE);
							String quantity = CalculateUtil.formatQuantity(chargeRecord.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(pileOrder.getChargePrice());
							String single_price = CalculateUtil.formatBill(pileOrder.getServicePay());
							view = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(view);

							view = OrderInfoView.createTextItem(mInflater, "充电价格", single_price);
							orderInfoView.addOrderInfo(view);

							view = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(view);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								StringBuilder strb = new StringBuilder();
								strb.append(TimeUtil.format(record.getStartTime(), START_TIME_FORMAT)).append("-").append(TimeUtil.format(record.getEndTime(),
										END_TIME_FORMAT));
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(record.getChargePrice());
								View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, strb.toString(), quantity, bill);
								orderInfoView.addOrderInfo(item);
							}
							// 总费用
							orderInfoView.addOrderInfo(OrderInfoView.createLine(mInflater));
							View view_bill = OrderInfoView.createTextItemBill(mInflater, context.getString(R.string.total_price), CalculateUtil.formatBill
									(pileOrder.getChargePrice()));
							orderInfoView.addOrderInfo(view_bill);
						}
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_finish_order));
					orderInfoView.setEvaluate(pileOrder.getPileLevel());
					orderInfoView.showEvaluateStar(true);
					break;
				}
			}


		}

		@Override
		protected View init(LayoutInflater arg0) {
			orderInfoView = new OrderInfoView(context);
			orderInfoView.setChoiceEdit(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						select(position);
					} else {
						unselect(position);
					}
				}
			});
			return orderInfoView;
		}

		private void setEvent() {
			listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.btn_left: {
							leftButtonClick(get(position));
							break;
						}
						case R.id.btn_right: {
							rightButtonClick(get(position));
							break;
						}
						default:
							break;
					}
				}

				private void leftButtonClick(ChargeOrder pileOrder) {
					switch (pileOrder.getAction()) {
						case ChargeOrder.ACTION_WATTING: {
							cancelOrder(pileOrder);
							break;
						}
						case ChargeOrder.ACTION_CONFIRM: {
							toNavigation(pileOrder);
							((Activity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
							break;
						}
					}
				}

				private void rightButtonClick(ChargeOrder pileOrder) {

					switch (pileOrder.getAction()) {

						case ChargeOrder.ACTION_COMPLETE: {
							goPay(pileOrder);
							break;
						}
						case ChargeOrder.ACTION_PAIED: {
							goCommand(pileOrder);
							break;
						}
						case ChargeOrder.ACTION_WATTING:
							cancelOrder(pileOrder);
							break;
						case ChargeOrder.ACTION_CONFIRM:
							toNavigation(pileOrder);
							break;
						case ChargeOrder.ACTION_REJECT:
						case ChargeOrder.ACTION_CANCEL:
						case ChargeOrder.ACTION_TIMEOUT: {

							Intent intentToPileInfo1 = new Intent(context, PileInfoNewActivity.class);
							intentToPileInfo1.putExtra(KEY.INTENT.PILE_ID, pileOrder.getPileId());
							context.startActivity(intentToPileInfo1);
							break;
						}
					}
				}

				private void toNavigation(ChargeOrder pileOrder) {
					Intent intent = new Intent(context, OrderNavigationActivity.class);
					intent.putExtra("pileId", pileOrder.getPileId());
					intent.putExtra("latitude", pileOrder.getLatitude());
					intent.putExtra("longitude", pileOrder.getLongitude());
					context.startActivity(intent);
				}

				private void callPhone() {
					// 打开拨号功能;
					if (!TextUtils.isEmpty(phoneNumber)) {
						IntentUtil.openTelephone(context, phoneNumber);
					} else {
						Intent _intent = new Intent(Intent.ACTION_CALL_BUTTON);
						context.startActivity(_intent);
					}
				}

				/*
				 * 去评价
				 */
				private void goCommand(ChargeOrder pileOrder) {
					Intent _intent = new Intent(context, MultipleEvaluateActivity.class);
					_intent.putExtra(KEY.INTENT.ORDER_ID, pileOrder.getOrderId());
					_intent.putExtra(KEY.INTENT.PILE_ID, pileOrder.getPileId());
					_intent.putExtra(KEY.INTENT.PILE_NAME, pileOrder.getPileName());
					/**
					 * temp 目前只有私人桩订单，所以传过去的都是桩类型评价
					 */
					_intent.putExtra(KEY.INTENT.EVALUATE_TYPE, MultipleEvaluateActivity.EVALUATE_TYPE_PILE);
					((FragmentActivity) context).startActivityForResult(_intent, MyOrderFragmentActivity.REQUEST_REFRESH);
					((Activity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
				}

				/*
				 * 去付款
				 */

				private void goPay(ChargeOrder pileOrder) {

					List<ChargeRecord> chargeRecords = pileOrder.getChargeList();

					if (chargeRecords != null && chargeRecords.size() > 0) {
						Intent pay_intent = new Intent(context, ChoicePayWayActivity.class);
						ids = new StringBuffer();
						for (int i = 0; i < chargeRecords.size(); i++) {
							if (i == chargeRecords.size() - 1) {
								ids.append(chargeRecords.get(i).getId().toString());
								break;
							}
							ids.append(chargeRecords.get(i).getId().toString() + ",");
						}

						Log.i("ids", ids.toString());

						pay_intent.putExtra(KEY.INTENT.KEY_ID, ids.toString());
						pay_intent.putExtra(KEY.INTENT.KEY_TRADE_ORDER, pileOrder.getOrderId());
						pay_intent.putExtra(KEY.INTENT.KEY_TOTAL_FEE, pileOrder.getChargePrice());
						((FragmentActivity) context).startActivityForResult(pay_intent, MyOrderFragmentActivity.REQUEST_REFRESH);
						((Activity) context).overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
					}
				}

				/*
				 * 取消订单
				 */
				private void cancelOrder(final ChargeOrder pileOrder) {
					WebAPIManager.getInstance().cancelOrder(pileOrder.getOrderId(), new WebResponseHandler<ChargeOrder>() {
						@Override
						public void onError(Throwable e) {
							super.onError(e);
							TipsUtil.showTips(context, e);
						}

						@Override
						public void onFailure(WebResponse<ChargeOrder> response) {
							super.onFailure(response);
							if (response.getCode().equals(WebResponse.CODE_ORDER_REFUSE_CANNOT_COMFIRM) || response.getCode().equals(WebResponse
									.CODE_ORDER_CIRFORM_CANNOT_COMFIRM)|| response.getCode().equals(WebResponse
									.CODE_ORDER_TIME_OUT_CANCEL)) {
								ToastUtil.show(context, response.getMessage() + "");
								if (isShowAll) {
									if(response.getCode().equals(WebResponse.CODE_ORDER_REFUSE_CANNOT_COMFIRM)){
										pileOrder.setAction(ChargeOrder.ACTION_REJECT);
									}else if(response.getCode().equals(WebResponse.CODE_ORDER_CIRFORM_CANNOT_COMFIRM)){
										pileOrder.setAction(ChargeOrder.ACTION_CONFIRM);
									}
								} else {
									items.remove(pileOrder);
								}
								notifyDataSetChanged();
								if (items.size() == 0) {
									if (itemForNoneOrderLayoutOperater != null) {
										itemForNoneOrderLayoutOperater.itemForNoneLayout();
									}
								}
							} else {
								TipsUtil.showTips(context, response);

							}
						}

						@Override
						public void onSuccess(WebResponse<ChargeOrder> response) {
							super.onSuccess(response);
							remove(pileOrder);
							notifyDataSetChanged();

							if (items.size() == 0) {
								if (itemForNoneOrderLayoutOperater != null) {
									itemForNoneOrderLayoutOperater.itemForNoneLayout();
								}
							}
							TipsUtil.showTips(context, response);
						}

					});
				}
			};
		}
	}

	private void setShowAllOrder(boolean isShowAll) {

	}

	private ItemSelected itemSelected;

	public ItemSelected getItemSelected() {
		return itemSelected;
	}

	public void setItemSelected(ItemSelected itemSelected) {
		this.itemSelected = itemSelected;
	}

	public interface ItemSelected {
		public void cancelCollect(ArrayList<ChargeOrder> chargeOrders);
	}
}
