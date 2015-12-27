package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easy.adapter.EasyAdapter;
import com.easy.util.ToastUtil;
import com.xpg.hssy.adapter.MyOrdersAdapter;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.main.fragment.MyOrderAll;
import com.xpg.hssy.main.fragment.MyOrderComplete;
import com.xpg.hssy.main.fragment.MyOrderConfirm;
import com.xpg.hssy.main.fragment.MyOrderIng;
import com.xpg.hssy.main.fragment.MyOrderUnused;
import com.xpg.hssy.main.fragment.MyOrderUnused.ItemOnclickForSelections;
import com.xpg.hssy.main.fragment.callbackinterface.IAppointOperatable;
import com.xpg.hssy.main.fragment.callbackinterface.IAppointOperater;
import com.xpg.hssy.main.fragment.callbackinterface.IMyOrderOperater;
import com.xpg.hssy.popwindow.AppointmentListShowPop;
import com.xpg.hssy.popwindow.AppointmentListShowPop.ItemOnClick;
import com.xpg.hssy.view.PageView;
import com.xpg.hssy.view.PageView.ScrollToFragment;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mazoh 我的订单管理
 */
public class MyOrderFragmentActivity extends FragmentActivity implements IAppointOperater, OnClickListener {

	// 使用activityForResult()启动其他Activity时使用此参数请求返回刷新,当从其他activity返回本activity时,将刷新当前的Fragment,
	// 详情请看onActivityResult()方法
	public final static int REQUEST_REFRESH = 0x01;
	public static final int REQUEST_FROM_MY_ORDER_DETAIL = 100;

	private static final String TAG = "MyOrderFragmentActivity";
	private LinearLayout ll_head;
	private RadioGroup mRadioGroupNew;
	private PageView mPageView;
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	private ImageView iv_back, iv_delete, iv_time;
	private AppointmentListShowPop pop;
	private TextView tv_title;
	private RelativeLayout rl_top_content;
	private Button btn_cancel;
	private RelativeLayout delete_rl;
	private Button delete;
	private ImageView imageview_tiao;
	private Fragment currentFragment;
	private MyOrdersAdapter myOrdersAdapter;
	private ArrayList<ChargeOrder> chargeOrders;
	private String user_id;
	private SharedPreferences sp;
	private LoadingDialog loadingDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorder_layout);
		View view = LayoutInflater.from(this).inflate(R.layout.myorder_layout, null);
		setContentView(view);
		initDate();
		init(view);
		initEvents();
		boolean isShowOrdering = getIntent().getBooleanExtra(KEY.INTENT.KEY_SHOW_ORDERING, false);
		if(isShowOrdering){
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					mPageView.setCurrentItem(1);
				}
			});
		}
	}

	private void initDate() {
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", null);
	}

	public void showUiForRl_top_content() {
		rl_top_content.setVisibility(View.VISIBLE);
		mRadioGroupNew.setVisibility(View.GONE);
		delete_rl.setVisibility(View.VISIBLE);
		imageview_tiao.setVisibility(View.GONE);
	}

	public void showUiForRadioGroup() {
		rl_top_content.setVisibility(View.GONE);
		mRadioGroupNew.setVisibility(View.VISIBLE);
		delete_rl.setVisibility(View.GONE);
		imageview_tiao.setVisibility(View.VISIBLE);
	}

	private void init(View view) {
		ll_head = (LinearLayout) view.findViewById(R.id.ll_head);
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_time = (ImageView) view.findViewById(R.id.iv_time);
		iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
		rl_top_content = (RelativeLayout) view.findViewById(R.id.rl_top_content);
		imageview_tiao = (ImageView) view.findViewById(R.id.imageview_tiao);


		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		delete_rl = (RelativeLayout) view.findViewById(R.id.delete_rl);
		delete = (Button) view.findViewById(R.id.delete);

		iv_delete.setVisibility(View.GONE);
		btn_cancel.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_time.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
		delete.setOnClickListener(this);
		tv_title.setText("我的订单");
		mPageView = (PageView) view.findViewById(R.id.id_viewpager);
		mRadioGroupNew = (RadioGroup) view.findViewById(R.id.rg_tab_new);
		showUiForRadioGroup();

		//取出遗留的fragment的初始时预约操作
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null) {
			int index = fragments.size();
			for (int i = 0; i < index; i++) {
				Fragment fragment = fragments.get(i);
				if (fragment instanceof IAppointOperatable) {
					((IAppointOperatable) fragment).setListen(this);
				}
			}
		}

		MyOrderAll myOrderAll = new MyOrderAll();
		MyOrderConfirm myorderConfirm = new MyOrderConfirm();
		MyOrderIng myOrderIng = new MyOrderIng();
		MyOrderComplete myOrderComlpete = new MyOrderComplete();
		MyOrderUnused myOrderUnused = new MyOrderUnused();


		myOrderAll.setListen(this);
		myorderConfirm.setListen(this);
		myOrderIng.setListen(this);
		myOrderComlpete.setListen(this);
		myOrderComlpete.setListen(this);
		myOrderUnused.setListen(this);
		mFragments.clear();
		mFragments.add(myorderConfirm);
		mFragments.add(myOrderIng);
		mFragments.add(myOrderComlpete);
		mFragments.add(myOrderUnused);
		mFragments.add(myOrderAll);

		mPageView.setIndicator(this, view, mFragments.size());

		pop = new AppointmentListShowPop(this);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.MATCH_PARENT);
	}

	private void initEvents() {
		mPageView.setPages(mFragments);
		mPageView.setRadioGroup(mRadioGroupNew);
		mPageView.initAdapter(getSupportFragmentManager());
		mPageView.setmScrollToFragment(new ScrollToFragment() {
			@Override
			public void onClickCurrentFragment(Fragment currentFragment) {
				pop.select(0);
				if (currentFragment instanceof MyOrderUnused) {
					MyOrderFragmentActivity.this.currentFragment = currentFragment;
					iv_delete.setVisibility(View.VISIBLE);
				} else {
					iv_delete.setVisibility(View.GONE);
					MyOrderFragmentActivity.this.currentFragment = null;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case REQUEST_REFRESH: {
					mPageView.getCurrentPage().onActivityResult(requestCode, resultCode, data);
					break;
				}
				case REQUEST_FROM_MY_ORDER_DETAIL: {
					if (data == null) {
						mPageView.getCurrentPage().onActivityResult(requestCode, resultCode, data);
						return;
					}
					setResult(Activity.RESULT_OK, data);
					finish();
					break;
				}
			}
		}

	}

	@Override
	public FragmentManager getSupportFragmentManager() {

		return super.getSupportFragmentManager();

	}

	@Override
	public void onAppointPlieId(String plie) {
		setResult(RESULT_OK, new Intent().putExtra(KEY.INTENT.PILE_ID, plie));
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_back:
				finish();
				break;
			case R.id.iv_delete:
				showUiForRl_top_content();
				iv_delete.setVisibility(View.GONE);
				iv_time.setVisibility(View.GONE);
				if (currentFragment != null) {
					myOrdersAdapter = ((MyOrderUnused) currentFragment).getMyOrdersAdapter();
					myOrdersAdapter.setMode(EasyAdapter.MODE_CHECK_BOX);
					//没有订单的时候置灰删除按钮
					if (myOrdersAdapter.getCount() <= 0) {
						delete.setText(R.string.btn_delete);
						delete.setEnabled(false);
					} else {
						delete.setText("删除(0)");
						delete.setEnabled(true);
					}
					((MyOrderUnused) currentFragment).setmItemOnclickForSelections(new MyItemOnclickForSelections());
					((MyOrderUnused) currentFragment).hideFindPileBtn();
				} else {

				}
				break;
			case R.id.btn_cancel:
				showUiForRadioGroup();
				iv_delete.setVisibility(View.VISIBLE);
				iv_time.setVisibility(View.VISIBLE);
				if (currentFragment != null) {
					myOrdersAdapter = ((MyOrderUnused) currentFragment).getMyOrdersAdapter();
					myOrdersAdapter.setMode(EasyAdapter.MODE_NON);
					((MyOrderUnused) currentFragment).showFindPileBtn();
				}
				break;
			case R.id.delete:
				if (chargeOrders.size() == 0) {
					ToastUtil.show(this, "请选择删除的订单");
					return;
				}
				StringBuilder strs = new StringBuilder();
				for (int i = 0; i < chargeOrders.size(); i++) {
					if (i == chargeOrders.size() - 1) {
						strs.append(chargeOrders.get(i).getOrderId());
					} else {
						strs.append(chargeOrders.get(i).getOrderId() + ",");
					}
				}
				Log.i("tag", strs.toString());
				WebAPIManager.getInstance().deleteOrder(user_id, strs.toString(), new WebResponseHandler<Object>(this) {

					@Override
					public void onStart() {
						super.onStart();
						loadingDialog = new LoadingDialog(MyOrderFragmentActivity.this, R.string.loading);
						loadingDialog.showDialog();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
						ToastUtil.show(MyOrderFragmentActivity.this, "删除失败");

					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						loadingDialog.dismiss();

						ToastUtil.show(MyOrderFragmentActivity.this, response.getMessage() + "");
					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);

						loadingDialog.dismiss();

						ToastUtil.show(MyOrderFragmentActivity.this, response.getMessage() + "");
						if (myOrdersAdapter != null) {
							myOrdersAdapter.remove(myOrdersAdapter.getSelectedItems());
							myOrdersAdapter.unselectAll();
							delete.setText("删除(0)");

						}
						showUiForRadioGroup();
						iv_delete.setVisibility(View.VISIBLE);
						iv_time.setVisibility(View.VISIBLE);
						if (currentFragment != null) {
							myOrdersAdapter = ((MyOrderUnused) currentFragment).getMyOrdersAdapter();
							myOrdersAdapter.setMode(EasyAdapter.MODE_NON);
							((MyOrderUnused) currentFragment).showFindPileBtn();
						}
					}

				});
				break;
			case R.id.iv_time:
				iv_time.getWidth();
				pop.showAsDropDown(iv_time, -2 * (iv_time.getWidth()), 20);
				pop.setItemOnClick(new ItemOnClick() {

					@Override
					public void click(int index) {
						switch (index) {
							case PileShareActivityNew.ALL:
								((IMyOrderOperater) mPageView.getCurrentPage()).refresh(-1, -1);
								break;
							//					case PileShareActivityNew.TODAY:
							//						((IMyOrderOperater) mPageView.getCurrentPage())
							//								.refresh(0, 0);
							//						break;
							case PileShareActivityNew.RECENT3:
								((IMyOrderOperater) mPageView.getCurrentPage()).refresh(2, 0);
								break;
							case PileShareActivityNew.RECENT14:
								((IMyOrderOperater) mPageView.getCurrentPage()).refresh(13, 0);
								break;
							case PileShareActivityNew.RECENT30:
								((IMyOrderOperater) mPageView.getCurrentPage()).refresh(29, 0);
								break;
							case PileShareActivityNew.RECENT90:
								((IMyOrderOperater) mPageView.getCurrentPage()).refresh(89, 0);
								break;

							default:
								break;
						}
					}
				});
				break;

			default:
				break;
		}
	}

	class MyItemOnclickForSelections implements ItemOnclickForSelections {

		@Override
		public void myItemForSelected(ArrayList<ChargeOrder> chargeOrders) {
			delete.setText("删除(" + chargeOrders.size() + ")");
			MyOrderFragmentActivity.this.chargeOrders = chargeOrders;
		}

	}

}
