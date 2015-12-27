package com.xpg.hssy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssy.util.LogUtils;
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
 * 桩主查看自己电桩的预约情况
 *
 * @author black
 */
public class PileOrderAppointmentAdapter extends EasyAdapter<ChargeOrder> {

	private final static String START_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	private final static String END_TIME_FORMAT = "HH:mm";
	private LayoutInflater mInflater;
	private int indexc;
	private LoadingDialog loadingDialog = null;

	public PileOrderAppointmentAdapter(Context context) {
		super(context);
	}

	public PileOrderAppointmentAdapter(Context context, List<ChargeOrder> pileOrderList, int indexCurrent) {
		super(context, pileOrderList);
		this.context = context;
		this.indexc = indexCurrent;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	protected ViewHolder newHolder() {
		return new mViewHoler();
	}

	public class mViewHoler extends ViewHolder {
		private ChargeOrder pileOrder;
		private OrderInfoView orderInfoView;
		private OnClickListener listener;

		public mViewHoler() {
			super();
			setEvent();
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

		@Override
		protected void update() {

			pileOrder = get(position);

			// 初始化订单item
			orderInfoView.clearOrderInfo();
			orderInfoView.setOrderNumber(pileOrder.getOrderId());
			orderInfoView.setDateVisibity(View.VISIBLE);

			orderInfoView.setDate(TimeUtil.format(pileOrder.getOrderTime(), START_TIME_FORMAT));
			View view = OrderInfoView.createTextItem(mInflater, "预  约  人", pileOrder.getTenantName());
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
						String timeString = createTimeString(pileOrder.getStartTime(), pileOrder.getEndTime());
						view = OrderInfoView.createTextItem(mInflater, "预约时段", timeString);
						orderInfoView.addOrderInfo(view);
					}
					if (pileOrder.getServicePay() != null) {
						view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
						orderInfoView.addOrderInfo(view);
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_wait_for_confirm));

					orderInfoView.setLeftButton(R.drawable.shape_btn_red, context.getResources().getString(R.string.reject_order), listener);

					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.agree_order), listener);
					orderInfoView.showLeftButton(true);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_CONFIRM: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();

					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
//							if (view != null) {
//								view.setVisibility(View.VISIBLE);
//							}
							ChargeRecord record = chargeRcordList.get(0);
							if (record.getStartTime() != null && record.getEndTime() != null) {
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								view = OrderInfoView.createTextItem(mInflater, "充电时段", timeString);
								orderInfoView.addOrderInfo(view);
							}
							String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(record.getChargePrice());
							View item = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(item);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							View item2 = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(item2);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								String timeString = "";
								if (record.getStartTime() != null && record.getEndTime() != null) {
									timeString = createTimeString(record.getStartTime(), record.getEndTime());
								}
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(record.getChargePrice());

								if (pileOrder.getPayTime() != null) {
									View item = OrderInfoView.createChargeDetailItem(mInflater, i + 1, timeString, quantity, bill, TimeUtil.format(pileOrder
											.getPayTime(), START_TIME_FORMAT));
									orderInfoView.addOrderInfo(item);
								} else {
									View item = OrderInfoView.createChargeDetailItem(mInflater, i + 1, timeString, quantity, bill);
									orderInfoView.addOrderInfo(item);
								}
							}
							view = OrderInfoView.createTextLine(mInflater);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, context.getString(R.string.total_price), CalculateUtil.formatBill(pileOrder
									.getChargePrice()));
							orderInfoView.addOrderInfo(view);
						}
					} else {
						if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
							String timeString = createTimeString(pileOrder.getStartTime(), pileOrder.getEndTime());
							view = OrderInfoView.createTextItem(mInflater, "预约时段", timeString);
							orderInfoView.addOrderInfo(view);
						}
						if (pileOrder.getServicePay() != null) {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
						}

					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), "已确认");
					Log.i("tag", "已确认");
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.contact_car_owner), listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_REJECT: {
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						String timeString = createTimeString(pileOrder.getStartTime(), pileOrder.getEndTime());
						view = OrderInfoView.createTextItem(mInflater, "预约时段", timeString);
						orderInfoView.addOrderInfo(view);
					}
					if (pileOrder.getServicePay() != null) {
						view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
						orderInfoView.addOrderInfo(view);
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_rejected));

					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.contact_car_owner), listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_CANCEL: {
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						String timeString = createTimeString(pileOrder.getStartTime(), pileOrder.getEndTime());
						view = OrderInfoView.createTextItem(mInflater, "预约时段", timeString);
						orderInfoView.addOrderInfo(view);
					}
					if (pileOrder.getServicePay() != null) {
						view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
						orderInfoView.addOrderInfo(view);
					}
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), "对方取消");

					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.contact_car_owner), listener);
					orderInfoView.showRightButton(true);
					break;
				}
				case ChargeOrder.ACTION_COMPLETE: {

					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), "待付款");
					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.contact_car_owner), listener);
					orderInfoView.showRightButton(true);
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
//							if (view != null) {
//								view.setVisibility(View.VISIBLE);
//							}
							ChargeRecord record = chargeRcordList.get(0);
							if (record.getStartTime() != null && record.getEndTime() != null) {
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								view = OrderInfoView.createTextItem(mInflater, "充电时段", timeString);
								orderInfoView.addOrderInfo(view);
							}
							String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(record.getChargePrice());
							View item = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(item);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							View item2 = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(item2);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(record.getChargePrice());

								if (pileOrder.getPayTime() != null) {
									View item = OrderInfoView.createChargeDetailItem(mInflater, i + 1, timeString, quantity, bill, TimeUtil.format(pileOrder
											.getPayTime(), START_TIME_FORMAT));
									orderInfoView.addOrderInfo(item);
								} else {
									View item = OrderInfoView.createChargeDetailItem(mInflater, i + 1, timeString, quantity, bill);
									orderInfoView.addOrderInfo(item);
								}
							}
							view = OrderInfoView.createTextLine(mInflater);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, context.getString(R.string.total_price), CalculateUtil.formatBill(pileOrder
									.getChargePrice()));
							orderInfoView.addOrderInfo(view);
						}
					}
					break;
				}
				case ChargeOrder.ACTION_PAIED: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
							if (view != null) {
								view.setVisibility(View.VISIBLE);
							}
							ChargeRecord record = chargeRcordList.get(0);
							if (record.getStartTime() != null && record.getEndTime() != null) {
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								view = OrderInfoView.createTextItem(mInflater, "充电时段", timeString);
								orderInfoView.addOrderInfo(view);
							}
							String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(record.getChargePrice());
							View item = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(item);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							View item2 = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(item2);

						} else {
							View view3 = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view3);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(record.getChargePrice());

								if (pileOrder.getPayTime() != null) {
									View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, timeString, quantity, bill, TimeUtil.format
											(pileOrder.getPayTime(), START_TIME_FORMAT));
									orderInfoView.addOrderInfo(item);
								} else {
									View item = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, timeString, quantity, bill);
									orderInfoView.addOrderInfo(item);
								}
							}
							view = OrderInfoView.createTextLine(mInflater);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, context.getString(R.string.total_price), CalculateUtil.formatBill(pileOrder
									.getChargePrice()));
							orderInfoView.addOrderInfo(view);
						}

						View space = OrderInfoView.createTextItem(mInflater, "", "");
						orderInfoView.addOrderInfo(space);

						orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
								.status_paid));
						orderInfoView.showLeftButton(false);
						orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, "对方未评价", listener);
						orderInfoView.showRightButton(false);
						orderInfoView.setNoComment(View.VISIBLE);
					}
					break;
				}
				case ChargeOrder.ACTION_TIMEOUT:
					if (pileOrder.getStartTime() != null && pileOrder.getEndTime() != null) {
						String timeString = createTimeString(pileOrder.getStartTime(), pileOrder.getEndTime());
						view = OrderInfoView.createTextItem(mInflater, "预约时段", timeString);
						orderInfoView.addOrderInfo(view);
					}
					view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
					orderInfoView.addOrderInfo(view);
					orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getResources().getString(R.string
							.status_overdue));

					orderInfoView.showLeftButton(false);
					orderInfoView.setRightButton(R.drawable.shape_btn_water_blue, context.getResources().getString(R.string.contact_car_owner), listener);
					orderInfoView.showRightButton(true);
					break;


				case ChargeOrder.ACTION_COMMANDED: {
					List<ChargeRecord> chargeRcordList = pileOrder.getChargeList();
					if (chargeRcordList != null && chargeRcordList.size() > 0) {
						if (chargeRcordList.size() == 1) {
							ChargeRecord record = chargeRcordList.get(0);
							if (record.getStartTime() != null && record.getEndTime() != null) {
								String timeString = createTimeString(record.getStartTime(), record.getEndTime());
								view = OrderInfoView.createTextItem(mInflater, "充电时段", timeString);
								orderInfoView.addOrderInfo(view);
							}
							String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
							String bill = CalculateUtil.formatBill(record.getChargePrice());
							view = OrderInfoView.createTextItem(mInflater, "充电电量", quantity);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, "充电费用", bill);
							orderInfoView.addOrderInfo(view);
						} else {
							view = OrderInfoView.createTextItem(mInflater, "充电价格", CalculateUtil.formatPirce(pileOrder.getServicePay()));
							orderInfoView.addOrderInfo(view);
							for (int i = 0; i < chargeRcordList.size(); i++) {
								ChargeRecord record = chargeRcordList.get(i);
								String timeString = "";
								if (record.getStartTime() != null && record.getEndTime() != null) {
									timeString = createTimeString(record.getStartTime(), record.getEndTime());
								}
								String quantity = CalculateUtil.formatQuantity(record.getQuantity() / 100f);
								String bill = CalculateUtil.formatBill(record.getChargePrice());

								if (pileOrder.getPayTime() != null) {
									view = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, timeString, quantity, bill, TimeUtil.format
											(pileOrder.getPayTime(), START_TIME_FORMAT));
									orderInfoView.addOrderInfo(view);
								} else {
									view = OrderInfoView.createChargeDetailItemForTanent(mInflater, i + 1, timeString, quantity, bill);
									orderInfoView.addOrderInfo(view);
								}
							}
							view = OrderInfoView.createTextLine(mInflater);
							orderInfoView.addOrderInfo(view);
							view = OrderInfoView.createTextItem(mInflater, context.getString(R.string.total_price), CalculateUtil.formatBill(pileOrder
									.getChargePrice()));
							orderInfoView.addOrderInfo(view);
						}

						View spaces = OrderInfoView.createTextItem(mInflater, "", "");
						orderInfoView.addOrderInfo(spaces);
						orderInfoView.setOrderStatusMessage(context.getResources().getColor(R.color.text_orange), context.getString(R.string
								.status_finish_order));
						orderInfoView.setEvaluate(pileOrder.getPileLevel());
						orderInfoView.showEvaluateStar(true);
					}
					break;
				}
				default:
					break;
			}
		}

		@NonNull
		private String createTimeString(long startTime, long endTime) {
			String start = TimeUtil.format(startTime, START_TIME_FORMAT);
			String end = TimeUtil.format(endTime, END_TIME_FORMAT);
			end = end.equals("00:00") ? "24:00" : end;
			return start + "-" + end;
		}

		private void setEvent() {

			listener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					switch (v.getId()) {
						case R.id.btn_left: {
							leftButtonClick();
							break;
						}
						case R.id.btn_right: {
							rightButtonClick();
							break;
						}
						default:
							break;
					}
				}

				private void leftButtonClick() {
					loadingDialog = new LoadingDialog((Activity) context, R.string.loading);
					loadingDialog.showDialog();
					WebAPIManager.getInstance().rejectOrder(pileOrder.getOrderId(), new WebResponseHandler<ChargeOrder>(context) {
						@Override
						public void onError(Throwable e) {
							super.onError(e);
							loadingDialog.dismiss();
							TipsUtil.showTips(context, e);
						}

						@Override
						public void onFailure(WebResponse<ChargeOrder> response) {
							super.onFailure(response);
							loadingDialog.dismiss();

							if (response.getCode() == WebResponse.CODE_ORDER_CANCEL_CANNOT_REJECT || response.getCode() == WebResponse
									.CODE_ORDER_TIME_OUT_CANNOT_REJECT) {
								ToastUtil.show(context, response.getMessage() + "");
								LogUtils.e("PileOrderAppointmentAdapter", "response failed:" + response.getMessage());
								items.remove(position);
								notifyDataSetChanged();
								if (items.size() == 0) {
									if (itemToNoneData != null) {
										itemToNoneData.jumpToNoneLayout();
									}
								}
							} else {
								TipsUtil.showTips(context, response);

							}

						}

						@Override
						public void onSuccess(WebResponse<ChargeOrder> response) {
							super.onSuccess(response);
							loadingDialog.dismiss();

							items.remove(position);
							notifyDataSetChanged();
							if (items.size() == 0) {
								if (itemToNoneData != null) {
									itemToNoneData.jumpToNoneLayout();
								}
							}
							TipsUtil.showTips(context, response);
							LogUtils.e("PileOrderAppointmentAdapter", "response succeed:" + response.getMessage());
//                                    }
						}


					});
				}

				private void rightButtonClick() {
					switch (pileOrder.getAction()) {
						case ChargeOrder.ACTION_WATTING: {
							loadingDialog = new LoadingDialog((Activity) context, R.string.loading);
							loadingDialog.showDialog();
							WebAPIManager.getInstance().agreeOrder(pileOrder.getOrderId(), new WebResponseHandler<ChargeOrder>(context) {
								@Override
								public void onError(Throwable e) {
									super.onError(e);
									loadingDialog.dismiss();
									TipsUtil.showTips(context, e);
								}

								@Override
								public void onFailure(WebResponse<ChargeOrder> response) {
									super.onFailure(response);
									loadingDialog.dismiss();

									if (response.getCode() == WebResponse.CODE_ORDER_CANCEL_CANNOT_COMFIRM || response.getCode() == WebResponse
											.CODE_ORDER_TIME_OUT_CANNOT_AGREE) {
										ToastUtil.show(context, response.getMessage() + "");
										items.remove(position);
										notifyDataSetChanged();
										if (items.size() == 0) {
											if (itemToNoneData != null) {
												itemToNoneData.jumpToNoneLayout();
											}
										}
									} else {
										TipsUtil.showTips(context, response);

									}
								}

								@Override
								public void onSuccess(WebResponse<ChargeOrder> response) {
									super.onSuccess(response);
									loadingDialog.dismiss();

									items.remove(position);
									notifyDataSetChanged();
									if (items.size() == 0) {
										if (itemToNoneData != null) {
											itemToNoneData.jumpToNoneLayout();
										}
									}
									TipsUtil.showTips(context, response);
//                                            }
								}
							});
							break;
						}

						// 这些状态下都是联系车主
						case ChargeOrder.ACTION_CONFIRM:
						case ChargeOrder.ACTION_REJECT:
						case ChargeOrder.ACTION_CANCEL:
						case ChargeOrder.ACTION_COMPLETE:
						case ChargeOrder.ACTION_PAIED:
						case ChargeOrder.ACTION_TIMEOUT: {
							String phoneNumber = pileOrder.getTenantPhone();
							// 打开拨号功能;
							if (!TextUtils.isEmpty(phoneNumber)) {
								IntentUtil.openTelephone(context, phoneNumber);
							} else {
								Intent intent = new Intent(Intent.ACTION_CALL_BUTTON);
								context.startActivity(intent);
							}
							break;
						}
					}
				}
			};
		}
	}

	private ItemToNoneData itemToNoneData;

	public ItemToNoneData getItemToNoneData() {
		return itemToNoneData;
	}

	public void setItemToNoneData(ItemToNoneData itemToNoneData) {
		this.itemToNoneData = itemToNoneData;
	}

	public interface ItemToNoneData {
		public void jumpToNoneLayout();
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