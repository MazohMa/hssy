package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.easy.adapter.EasyAdapter;
import com.easy.util.EmptyUtil;
import com.xpg.hssy.adapter.PileOrderAppointmentAdapter;
import com.xpg.hssy.adapter.PileOrderAppointmentAdapter.ItemSelected;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.main.activity.MyOrderDetailActivity;
import com.xpg.hssy.main.activity.MyOrderFragmentActivity;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.DropDownListView.OnDropDownListener;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class ReservationOrderFragment extends BaseFragment {
	private RefreshListView pileSetting_yuyue_all_lv;
	private LinearLayout yuyue_noorder_layout;
	public PileOrderAppointmentAdapter pileSettingAppointmentAdapter;
	private List<ChargeOrder> pileSettingAppointmentList;
	private String pileId;
	private List<Integer> action;
	private int fragmentIndx;
	private boolean isRefreshing = false;
	private ChargeOrder order;
	private int currentIndex;

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public PileOrderAppointmentAdapter getPileSettingAppointmentAdapter() {
		return pileSettingAppointmentAdapter;
	}

	public void setPileSettingAppointmentAdapter(PileOrderAppointmentAdapter pileSettingAppointmentAdapter) {
		this.pileSettingAppointmentAdapter = pileSettingAppointmentAdapter;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && pileSetting_yuyue_all_lv != null) {
			refreshOrders(startOffset, endOffset);
		} else {
			return;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void reflash(int startOffset, int endOffset) {
		refreshOrders(startOffset, endOffset);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		action = new ArrayList<Integer>();
		pileId = getArguments().getString("pileId");
		action = getArguments().getIntegerArrayList("actionList");
		fragmentIndx = getArguments().getInt("fragmentIndex");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.yuyue_tab, null);
		pileSetting_yuyue_all_lv = (RefreshListView) v.findViewById(R.id.n_dianzhuangsetting_yuyue_lv);
		yuyue_noorder_layout = (LinearLayout) v.findViewById(R.id.yuyue_noorder_layout);
		pileSettingAppointmentList = new ArrayList<ChargeOrder>();
		pileSettingAppointmentAdapter = new PileOrderAppointmentAdapter(getActivity(), pileSettingAppointmentList, fragmentIndx);
		pileSetting_yuyue_all_lv.setAdapter(pileSettingAppointmentAdapter);

		yuyue_noorder_layout.setVisibility(View.GONE);
		pileSetting_yuyue_all_lv.setOnBottomListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadOrders(startOffset, endOffset);
			}
		});
		pileSetting_yuyue_all_lv.setOnRefreshListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				refreshOrders(startOffset, endOffset);
				pileSettingAppointmentAdapter.getSelectedItems().clear();
				if (mItemOnclickForSelections != null) {
					mItemOnclickForSelections.myItemForSelected(null, true);
				}
			}
		});
		refreshOrders(startOffset, endOffset);

		pileSetting_yuyue_all_lv.setOnItemClickListener(new MyItemClick());
		pileSettingAppointmentAdapter.setItemSelected(new MyItemSelected());
		pileSettingAppointmentAdapter.setItemToNoneData(new PileOrderAppointmentAdapter.ItemToNoneData() {
			@Override
			public void jumpToNoneLayout() {
				pileSetting_yuyue_all_lv.setVisibility(View.GONE);
				yuyue_noorder_layout.setVisibility(View.VISIBLE);
			}
		});
		return v;
	}

	private void refreshOrders(int startOffset, int endOffset) {
		if (isRefreshing) {
			return;
		}
		isRefreshing = true;
		yuyue_noorder_layout.setVisibility(View.GONE);
		pileSettingAppointmentList.clear();
		pileSettingAppointmentAdapter.notifyDataSetChanged();
		// pileSetting_yuyue_all_lv.setDropDownStyle(true);
		// pileSetting_yuyue_all_lv.setOnBottomStyle(false);
		// pileSetting_yuyue_all_lv.onDropDownBegin();
		pileSetting_yuyue_all_lv.setLoadable(false);
		pileSetting_yuyue_all_lv.showRefreshing(false);
		loadOrders(startOffset, endOffset);
	}

	int startOffset = -1;
	int endOffset = -1;

	private void loadOrders(int startOffset, int endOffset) {
		WebAPIManager.getInstance().getPileOrder(pileId, null, action, pileSettingAppointmentAdapter.getCount(), MyConstant.PAGE_SIZE, startOffset, endOffset,
				new WebResponseHandler<List<ChargeOrder>>(getActivity()) {

					@Override
					public void onStart() {
						super.onStart();

					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						if (pileSettingAppointmentAdapter.getCount() == 0) {
							pileSetting_yuyue_all_lv.showRefreshFail();
						} else {
							pileSetting_yuyue_all_lv.showLoadFail();
						}
						TipsUtil.showTips(getActivity(), e);
					}

					@Override
					public void onFailure(WebResponse<List<ChargeOrder>> response) {
						super.onFailure(response);
						if (pileSettingAppointmentAdapter.getCount() == 0) {
							pileSetting_yuyue_all_lv.showRefreshFail();
						} else {
							pileSetting_yuyue_all_lv.showLoadFail();
						}
						TipsUtil.showTips(getActivity(), response);
					}

					@Override
					public void onSuccess(WebResponse<List<ChargeOrder>> response) {
						super.onSuccess(response);
						List<ChargeOrder> orders = response.getResultObj();
						if (!EmptyUtil.isEmpty(orders)) {
							// 有数据
							if (pileSettingAppointmentAdapter.getCount() == 0) {
								// 第一页有数据
								yuyue_noorder_layout.setVisibility(View.GONE);
								pileSetting_yuyue_all_lv.completeRefresh();
							} else {
								// 第二页起有数据
							}
							pileSettingAppointmentList.addAll(orders);
							pileSettingAppointmentAdapter.notifyDataSetChanged();
							if (orders.size() < MyConstant.PAGE_SIZE) {
								// 已经是最后一页
								pileSetting_yuyue_all_lv.showNoMore();
							} else {
								// 还有下一页
								pileSetting_yuyue_all_lv.prepareLoad();
							}
						} else {
							// 没有数据
							if (pileSettingAppointmentAdapter.getCount() == 0) {
								// 第一页没数据
								pileSetting_yuyue_all_lv.completeRefresh();
								yuyue_noorder_layout.setVisibility(View.VISIBLE);
							} else {
								// 第二页起没数据
							}
							pileSetting_yuyue_all_lv.showNoMore();
						}
					}

					@Override
					public void onFinish() {
						super.onFinish();
						isRefreshing = false;
					}
				});
	}

	class MyItemSelected implements ItemSelected {

		@Override
		public void cancelCollect(ArrayList<ChargeOrder> chargeOrders) {
			Log.i("cancelCollect", chargeOrders.size() + "");
			if (mItemOnclickForSelections != null) {
				mItemOnclickForSelections.myItemForSelected(chargeOrders, isRefreshing);
			}
		}

	}

	class MyItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (pileSettingAppointmentAdapter.getMode() == EasyAdapter.MODE_CHECK_BOX) {
				if (pileSettingAppointmentAdapter.isSelected(position - 1)) {
					pileSettingAppointmentAdapter.unselect(position - 1);
				} else {
					pileSettingAppointmentAdapter.select(position - 1);
				}
			} else {
				order = pileSettingAppointmentAdapter.get(position - 1);
				if (order == null) return;
				Intent intent = new Intent(getActivity(), MyOrderDetailActivity.class);
//				intent.putExtra("order", order);
				intent.putExtra("orderId", order.getOrderId());
				getActivity().startActivityForResult(intent, MyOrderFragmentActivity.REQUEST_FROM_MY_ORDER_DETAIL);
				getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
			}
		}

	}

	@Override
	public String getFragmentName() {
		return "ReservationOrderFragment";
	}

	private ItemOnclickForSelections mItemOnclickForSelections;

	public ItemOnclickForSelections getmItemOnclickForSelections() {
		return mItemOnclickForSelections;
	}

	public void setmItemOnclickForSelections(ItemOnclickForSelections mItemOnclickForSelections) {
		this.mItemOnclickForSelections = mItemOnclickForSelections;
	}

	public interface ItemOnclickForSelections {
		public void myItemForSelected(ArrayList<ChargeOrder> chargeOrders, boolean isRefreshing);
	}

}
