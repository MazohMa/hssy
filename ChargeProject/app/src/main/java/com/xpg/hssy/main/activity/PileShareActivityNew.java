package com.xpg.hssy.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.easy.adapter.EasyAdapter;
import com.easy.util.ToastUtil;
import com.xpg.hssy.adapter.PileOrderAppointmentAdapter;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.ChargeOrder;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.main.fragment.ReservationOrderFragment;
import com.xpg.hssy.main.fragment.ReservationOrderFragment.ItemOnclickForSelections;
import com.xpg.hssy.popwindow.AppointmentListShowPop;
import com.xpg.hssy.popwindow.AppointmentListShowPop.ItemOnClick;
import com.xpg.hssy.view.PageView;
import com.xpg.hssy.view.PageView.ScrollToFragment;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PileShareActivityNew
 *
 * @author Mazoh 电桩预约订单
 */
public class PileShareActivityNew extends FragmentActivity implements OnClickListener {

	public static final String TAG = "PileShareActivityNew";
	public static final int CONFIRM = 12; // 确认
	public static final int REJECT = 13; // 拒绝

	private static final int PAGE_PAY_INDEX = 2;
	private static final int PAGE_TIMEOUT_INDEX = 4;
	private RelativeLayout rl_top_content;
	private ImageView imageview_tiao;
	private Activity mActivity;
	public static String position;
	private Pile pile;
	private String pile_id;
	private RadioGroup mRadioGroupNew;
	private PageView mViewPager;
	private List<Fragment> mFragments = new ArrayList<Fragment>();
	private LayoutInflater inflater;
	private View view;
	private ImageView iv_back, iv_delete, iv_time;
	private AppointmentListShowPop pop;
	public PileOrderAppointmentAdapter pileSettingAppointmentAdapter;
	private ArrayList<ChargeOrder> chargeOrders;

	public static final int ALL = 0;
	//	public static final int TODAY = 1;
	public static final int RECENT3 = 1;
	public static final int RECENT14 = 2;
	public static final int RECENT30 = 3;
	//	public static final int HALFYEAR = 5;
	public static final int RECENT90 = 4;

	private Button delete;
	private RelativeLayout delete_rl;
	private String user_id;
	private SharedPreferences sp;
	private Fragment currentFragment;
	private Button btn_cancel;
	private HorizontalScrollView scrollview;
	private LoadingDialog loadingDialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		inflater = LayoutInflater.from(mActivity);
		pile_id = getIntent().getStringExtra("pile_id");
		if (pile_id != null) {
			pile = DbHelper.getInstance(mActivity).getPileDao().load(pile_id);
		}
		view = inflater.inflate(R.layout.pile_share_activity, null);
		initData();
		init(view);
		initEvents();
	}

	private void initEvents() {

		btn_cancel.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_time.setOnClickListener(this);
		iv_delete.setOnClickListener(this);
		delete.setOnClickListener(this);


		mViewPager.setScrollview(scrollview);
		mViewPager.setIndicator(mActivity, view, mFragments.size());
		mViewPager.setPages(mFragments);
		mViewPager.setRadioGroup(mRadioGroupNew);
		mViewPager.initAdapter(getSupportFragmentManager());
		mViewPager.setmScrollToFragment(new ScrollToFragment() {

			@Override
			public void onClickCurrentFragment(Fragment currentFragment) {
				pop.select(0);
				if (currentFragment instanceof ReservationOrderFragment && ((ReservationOrderFragment) currentFragment).getCurrentIndex() == 1) {
					scrollview.setScrollX(0);
					Log.i("tag scrollview:", scrollview.getScrollX() + "");
				}
				if (currentFragment instanceof ReservationOrderFragment && ((ReservationOrderFragment) currentFragment).getCurrentIndex() == 6) {
					scrollview.setScrollX(999);
					Log.i("tag scrollview:", scrollview.getScrollX() + "");
				}
				if (currentFragment instanceof ReservationOrderFragment && ((ReservationOrderFragment) currentFragment).getCurrentIndex() == 5) {
					PileShareActivityNew.this.currentFragment = currentFragment;
					Log.i("onClickCurrentFragment", ((ReservationOrderFragment) currentFragment).getFragmentName() + "");
					iv_delete.setVisibility(View.VISIBLE);
				} else {
					iv_delete.setVisibility(View.GONE);
					PileShareActivityNew.this.currentFragment = null;
				}
			}
		});
	}

	private void initData() {

		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		user_id = sp.getString("user_id", null);

		ArrayList<Integer> actionTypeList0 = new ArrayList<>();
		ArrayList<Integer> actionTypeList1 = new ArrayList<>();
		ArrayList<Integer> actionTypeList2 = new ArrayList<>();
		ArrayList<Integer> actionTypeList3 = new ArrayList<>();
		ArrayList<Integer> actionTypeList4 = new ArrayList<>();
		ArrayList<Integer> actionTypeList5 = new ArrayList<>();

		Map<Integer, ArrayList<Integer>> temMap = new HashMap<>();


		actionTypeList0.add(ChargeOrder.ACTION_WATTING); // 待处理;
		temMap.put(0, actionTypeList0);// 待处理;

		actionTypeList1.add(ChargeOrder.ACTION_CONFIRM);// 已确认;
		temMap.put(1, actionTypeList1);// 已确认;

		actionTypeList2.add(ChargeOrder.ACTION_COMPLETE);// 已充电;
		temMap.put(2, actionTypeList2);// 待付款;

		actionTypeList3.add(ChargeOrder.ACTION_PAIED);// 已付款;
		actionTypeList3.add(ChargeOrder.ACTION_COMMANDED);// 已评价;
		temMap.put(3, actionTypeList3);// 已完成;

		actionTypeList4.add(ChargeOrder.ACTION_REJECT);// 桩主拒绝;
		actionTypeList4.add(ChargeOrder.ACTION_CANCEL);// 客户取消;
		actionTypeList4.add(ChargeOrder.ACTION_TIMEOUT);// 已过期;
		temMap.put(4, actionTypeList4);// 已过期;


		temMap.put(5, actionTypeList5);// 全部;

		mFragments.clear();
		for (int i = 0; i < 6; i++) {
			Bundle bundle = new Bundle();
			ReservationOrderFragment tab = new ReservationOrderFragment();
			bundle.putInt("fragmentIndex", i + 1);
			bundle.putString("pileId", pile_id);
			bundle.putIntegerArrayList("actionList", temMap.get(i));
			tab.setArguments(bundle);
			tab.setCurrentIndex(i + 1);
			mFragments.add(tab);
		}

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
		setContentView(view);
		iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_time = (ImageView) view.findViewById(R.id.iv_time);
		iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		iv_delete.setVisibility(View.GONE);

		rl_top_content = (RelativeLayout) view.findViewById(R.id.rl_top_content);
		scrollview = (HorizontalScrollView) view.findViewById(R.id.scrollview);
		imageview_tiao = (ImageView) view.findViewById(R.id.imageview_tiao);

		mViewPager = (PageView) view.findViewById(R.id.id_viewpager);
		mRadioGroupNew = (RadioGroup) view.findViewById(R.id.rg_tab_new);

		pop = new AppointmentListShowPop(this);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.MATCH_PARENT);

		delete_rl = (RelativeLayout) view.findViewById(R.id.delete_rl);
		delete = (Button) view.findViewById(R.id.delete);
		showUiForRadioGroup();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK, new Intent());
			finish();
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				setResult(RESULT_OK, new Intent());
				finish();
				break;
			case R.id.btn_right:
				break;
			case R.id.iv_back:
				setResult(RESULT_OK, new Intent());
				finish();
				break;
			case R.id.btn_cancel:
				showUiForRadioGroup();
				iv_delete.setVisibility(View.VISIBLE);
				iv_time.setVisibility(View.VISIBLE);
				if (currentFragment != null) {
					pileSettingAppointmentAdapter = ((ReservationOrderFragment) currentFragment).getPileSettingAppointmentAdapter();
					pileSettingAppointmentAdapter.setMode(EasyAdapter.MODE_NON);
				}
				break;
			case R.id.iv_delete:
				showUiForRl_top_content();
				iv_delete.setVisibility(View.GONE);
				iv_time.setVisibility(View.GONE);

				if (currentFragment != null) {
					pileSettingAppointmentAdapter = ((ReservationOrderFragment) currentFragment).getPileSettingAppointmentAdapter();
					pileSettingAppointmentAdapter.setMode(EasyAdapter.MODE_CHECK_BOX);
					//没有订单的时候置灰删除按钮
					if (pileSettingAppointmentAdapter.getCount() <= 0) {
						delete.setText(R.string.btn_delete);
						delete.setEnabled(false);
					} else {
						delete.setText("删除(0)");
						delete.setEnabled(true);
					}

					((ReservationOrderFragment) currentFragment).setmItemOnclickForSelections(new ItemOnclickForSelections() {

						@Override
						public void myItemForSelected(ArrayList<ChargeOrder> chargeOrders, boolean isRefreshing) {
							if (isRefreshing && chargeOrders == null) {
								delete.setText("删除(0)");
							} else {
								delete.setText("删除(" + chargeOrders.size() + ")");
								PileShareActivityNew.this.chargeOrders = chargeOrders;
							}
						}
					});
				} else {

				}
				break;
			case R.id.iv_time:

				iv_time.getWidth();
				pop.showAsDropDown(iv_time, -2 * (iv_time.getWidth()), 20);
				pop.setItemOnClick(new ItemOnClick() {

					@Override
					public void click(int index) {
						switch (index) {
							case ALL:
								((ReservationOrderFragment) mFragments.get(mViewPager.getCurrentItem())).reflash(-1, -1);
								break;
//					case TODAY:
//						((ReservationOrderFragment) mFragments.get(mViewPager
//								.getCurrentItem())).refresh(0, 0);
//						break;
							case RECENT3:
								((ReservationOrderFragment) mFragments.get(mViewPager.getCurrentItem())).reflash(2, 0);
								break;
							case RECENT14:
								((ReservationOrderFragment) mFragments.get(mViewPager.getCurrentItem())).reflash(13, 0);
								break;
							case RECENT30:
								((ReservationOrderFragment) mFragments.get(mViewPager.getCurrentItem())).reflash(29, 0);
								break;
							case RECENT90:
								((ReservationOrderFragment) mFragments.get(mViewPager.getCurrentItem())).reflash(89, 0);
								break;

							default:
								break;
						}
					}
				});
				break;
			case R.id.delete: {
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

						loadingDialog = new LoadingDialog(PileShareActivityNew.this, R.string.loading);
						loadingDialog.showDialog();
					}

					@Override
					public void onError(Throwable e) {
						super.onError(e);
						loadingDialog.dismiss();
						ToastUtil.show(PileShareActivityNew.this, "删除失败");

					}

					@Override
					public void onFailure(WebResponse<Object> response) {
						super.onFailure(response);
						loadingDialog.dismiss();
						ToastUtil.show(PileShareActivityNew.this, response.getMessage() + "");
					}

					@Override
					public void onSuccess(WebResponse<Object> response) {
						super.onSuccess(response);

						loadingDialog.dismiss();

						ToastUtil.show(PileShareActivityNew.this, response.getMessage() + "");
						if (pileSettingAppointmentAdapter != null) {
							pileSettingAppointmentAdapter.remove(pileSettingAppointmentAdapter.getSelectedItems());
							pileSettingAppointmentAdapter.unselectAll();
							delete.setText("删除(0)");

						}
						showUiForRadioGroup();
						iv_delete.setVisibility(View.VISIBLE);
						iv_time.setVisibility(View.VISIBLE);
						if (currentFragment != null) {
							pileSettingAppointmentAdapter = ((ReservationOrderFragment) currentFragment).getPileSettingAppointmentAdapter();
							pileSettingAppointmentAdapter.setMode(EasyAdapter.MODE_NON);
						}
					}

				});
				break;
			}
			default:
				break;
		}

	}

}
