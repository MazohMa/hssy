package com.xpg.hssy.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.adapter.MyPileFindAdapterNews;
import com.xpg.hssy.animator.ExtendAnimate;
import com.xpg.hssy.animator.ExtendAnimation;
import com.xpg.hssy.animator.ExtendAnimator;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.bean.searchconditon.SearchCondition;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.dialog.WaterBlueListPop;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.engine.LocationInfos.LocationInfo;
import com.xpg.hssy.main.activity.PileInfoNewActivity;
import com.xpg.hssy.main.activity.PileStationInfoActivity;
import com.xpg.hssy.main.fragment.callbackinterface.ILocationOperater;
import com.xpg.hssy.main.fragment.callbackinterface.IPileDataOperater;
import com.xpg.hssy.util.LogUtils;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.view.DropDownListView.OnDropDownListener;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年3月26日
 */

public class PileFindListFragment extends BaseFragment implements IPileDataOperater, OnCheckedChangeListener, OnItemClickListener, OnDismissListener,
		OnGetGeoCoderResultListener {

	public static final long[] DISTANCE_ARRAY = {PileFindFragment.DEFAULT_DISTANCE, 500, 1000, 2000, 5000};

	public static final int[] DISTANCE_STRING_ARRAY = {R.string.distance, R.string.distance_500m, R.string.distance_1km, R.string.distance_2km, R.string
			.distance_5km};

	public static final String SETTING_LAUNCH_TIMES = "launch_times";

	private SPFile sp;

	private CenterPointSearchCondition searchCondition;

	private ILocationOperater locationOperater;

	private LinearLayout ll_condition;
	private View v_touch;
	private RadioGroup rg_condition2;
	private RadioButton rb_distance;
	private RadioButton rb_sort;
	private RadioButton rb_filter;
	private RefreshListView lv_piles;
	private MyPileFindAdapterNews adapter;

	private Timer conditionHideTimer;
	private int conditionHeight;
	private int conditionMoveInterval;

	private WaterBlueListPop distancePop;
	private WaterBlueListPop sortPop;
	private String userid = "";
	//	private PileScreeningLayout listScreeningLayout;
	//	private FlodAnimator animScreening;

	private RelativeLayout rl_pile_filter;// 筛选窗口
	//筛选界面
	private RelativeLayout rl_pile_type_all;
	private RelativeLayout rl_pile_type_personal;
	private RelativeLayout rl_pile_type_studio;
	private Button btn_show_free_pile;
	private ExtendAnimator filterWindowAnimator;// 筛选窗口动画

	private LoadPileHandler loadPileHandler;

	private double myLatitude = -1;
	private double myLongitude = -1;

	private boolean isRefresh;

	private BroadcastReceiver refreshPileList = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			isRefresh = true;
//			String pileId = intent.getStringExtra(KEY.INTENT.PILE_ID);
//			int favor = intent.getIntExtra(KEY.INTENT.IS_FAVOR, 0);
//			boolean flag = intent.getBooleanExtra(KEY.INTENT.IS_REFRESH, false);
//			if (flag) isRefresh = true;
//			if (EmptyUtil.notEmpty(pileId)) {
//				if (adapter != null && adapter.getCount() > 0) {
//					for (int i = 0; i < adapter.getCount(); i++) {
//						if (((Pile) adapter.getItem(i)).getPileId().equals(pileId)) {
//							((Pile) adapter.getItem(i)).setFavor(favor);
//							adapter.notifyDataSetChanged();
//							break;
//						}
//					}
//				}
//			}
		}
	};


	/**
	 * 初始化
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// 初始化数据
		initData();

		// 初始化UI
		View v = initUI(inflater);

		// 初始化搜索条件弹出窗口
		initPop();

		// 初始化交互事件
		initEvent();

		// 刷新筛选条件
		updateConditionUI();

		// 刷新列表
		refreshPilesAndShowRefreshing();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateFilter(searchCondition);
		LogUtils.e("....", "onResume");
		if (sp != null) {
			userid = sp.getString("user_id", "");
		}
	}


	private void initData() {
		sp = new SPFile(getActivity(), "config");
		userid = sp.getString("user_id", "");
		myLongitude = Double.valueOf(sp.getString(KEY.CONFIG.MY_LONGITUDE, "-1"));
		myLatitude = Double.valueOf(sp.getString(KEY.CONFIG.MY_LATITUDE, "-1"));
		if (searchCondition.getCenterLatitude() > 0 && searchCondition.getCenterLongitude() > 0 &&
				searchCondition.getDistance() <= 0) {
			searchCondition.setDistance(PileFindFragment.DEFAULT_DISTANCE);
		}
		getActivity().registerReceiver(refreshPileList, new IntentFilter(KEY.ACTION.ACTION_REFRESH_PILE_LIST));
	}

	private View initUI(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.fragment_pile_find_lsit_new, null);
		v_touch = v.findViewById(R.id.v_touch);
		ll_condition = (LinearLayout) v.findViewById(R.id.ll_condition);
		rg_condition2 = (RadioGroup) v.findViewById(R.id.rg_condition2);
		rb_distance = (RadioButton) v.findViewById(R.id.rb_distance);
		rb_sort = (RadioButton) v.findViewById(R.id.rb_sort);
		rb_filter = (RadioButton) v.findViewById(R.id.rb_filter);
		lv_piles = (RefreshListView) v.findViewById(R.id.lv_piles);
		adapter = new MyPileFindAdapterNews(getActivity(), myLongitude, myLatitude);
		lv_piles.setAdapter(adapter);
		adapter.setFromActivity(false);

		// 初始化筛选窗口
		//		listScreeningLayout = (PileScreeningLayout) v
		//				.findViewById(R.id.layout_screening);
		//		listScreeningLayout.init(getActivity(), this, searchCondition);
		//		animScreening = new FlodAnimator(listScreeningLayout);
		//		animScreening.flod();

		// 筛选窗口
		rl_pile_filter = (RelativeLayout) v.findViewById(R.id.rl_pile_filter);
		//筛选类型
		rl_pile_type_all = (RelativeLayout) v.findViewById(R.id.rl_pile_type_all);
		rl_pile_type_personal = (RelativeLayout) v.findViewById(R.id.rl_pile_type_personal);
		rl_pile_type_studio = (RelativeLayout) v.findViewById(R.id.rl_pile_type_studio);
		btn_show_free_pile = (Button) v.findViewById(R.id.btn_show_free_pile);
		filterWindowAnimator = new ExtendAnimator(rl_pile_filter, ExtendAnimate.ORIENTATION_TOP);
		rl_pile_filter.setVisibility(View.GONE);
		filterWindowAnimator.unextend();

		return v;
	}

	private void initPop() {
		List<Integer> items = new ArrayList<Integer>();
		items.add(R.string.no_limit);
		items.add(R.string.distance_500m);
		items.add(R.string.distance_1km);
		items.add(R.string.distance_2km);
		items.add(R.string.distance_5km);
		distancePop = new WaterBlueListPop(getActivity(), items);
		items = new ArrayList<Integer>();
		items.add(R.string.no_limit);
		items.add(R.string.sort_hot);
		items.add(R.string.sort_distance);
		items.add(R.string.sort_grade);
		items.add(R.string.sort_price);
		sortPop = new WaterBlueListPop(getActivity(), items);
	}

	private void initEvent() {
		rg_condition2.setOnCheckedChangeListener(this);
		distancePop.setOnDismissListener(this);
		sortPop.setOnDismissListener(this);
		v_touch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				flodFilterAndClearCheck();
				return true;
			}
		});
		// 选择距离
		distancePop.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				position = position - 1;
				Log.d("distance_itme", "distance index: " + position);
				if (position > -1 && position <= DISTANCE_ARRAY.length) {
					searchCondition.setDistance(DISTANCE_ARRAY[position]);
					rb_distance.setText(DISTANCE_STRING_ARRAY[position]);
				} else {
					searchCondition.setDistance(PileFindFragment.DEFAULT_DISTANCE);
					rb_distance.setText(R.string.distance);
				}
				refreshPilesAndShowRefreshing();
			}
		});
		// 选择排序
		sortPop.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				int selectedId = (int) id;
				rb_sort.setText(selectedId == R.string.no_limit ? R.string.sort : selectedId);
				switch (selectedId) {
					case R.string.sort_hot:
						searchCondition.setSortType(CenterPointSearchCondition.SORT_HOT);
						break;
					case R.string.sort_distance:
						searchCondition.setSortType(CenterPointSearchCondition.SORT_DISTANCE);
						break;
					case R.string.sort_grade:
						searchCondition.setSortType(CenterPointSearchCondition.SORT_GRADE);
						break;
					case R.string.sort_price:
						searchCondition.setSortType(CenterPointSearchCondition.SORT_PRICE);
						break;
					default:
						searchCondition.setSortType(-1);
						break;
				}
				refreshPilesAndShowRefreshing();
			}
		});
		rb_filter.setOnClickListener(this);
		rl_pile_type_all.setOnClickListener(this);
		rl_pile_type_personal.setOnClickListener(this);
		rl_pile_type_studio.setOnClickListener(this);
		btn_show_free_pile.setOnClickListener(this);
		// 桩列表相关事件
		lv_piles.setOnRefreshListener(new OnDropDownListener() {
			@Override
			public void onDropDown() {
				refreshPiles();
			}
		});
		lv_piles.setOnLoadListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadPiles();
			}
		});
		lv_piles.setOnTouchListener(onTouchListListener);
		lv_piles.setOnItemClickListener(this);
		ll_condition.measure(0, 0);
		conditionHeight = ll_condition.getMeasuredHeight();
		conditionMoveInterval = conditionHeight / 10;
		lv_piles.setDropDownStyle(true, true, conditionHeight);
	}

	/**
	 * 点击筛选条件按钮事件
	 */
	@Override
	public void onClick(View view) {
		super.onClick(view);
		cancelHideConditionDelay();
		if (getActivity() == null) {
			return;
		}
		switch (view.getId()) {
			case R.id.rb_filter: {
				if (filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDED) {
					flodFilterAndClearCheck();
				}
				break;
			}
			case R.id.rl_pile_type_all: {
				selectView(rl_pile_type_all);
				unselectView(rl_pile_type_personal);
				unselectView(rl_pile_type_studio);
				searchCondition.getPileTypes().clear();
				searchCondition.addPileTypes(SearchCondition.CURRENT_DC);
				searchCondition.addPileTypes(SearchCondition.CURRENT_AC);
				searchCondition.addPileTypes(SearchCondition.PUBLIC);
				refreshPilesAndShowRefreshing();
//				flodFilterAndClearCheck();
				break;
			}
			case R.id.rl_pile_type_personal: {
				selectView(rl_pile_type_personal);
				unselectView(rl_pile_type_all);
				unselectView(rl_pile_type_studio);
				searchCondition.getPileTypes().clear();
				searchCondition.addPileTypes(SearchCondition.CURRENT_DC);
				searchCondition.addPileTypes(SearchCondition.CURRENT_AC);
				refreshPilesAndShowRefreshing();
//				flodFilterAndClearCheck();
				break;
			}
			case R.id.rl_pile_type_studio: {
				selectView(rl_pile_type_studio);
				unselectView(rl_pile_type_all);
				unselectView(rl_pile_type_personal);
				searchCondition.getPileTypes().clear();
				searchCondition.addPileTypes(SearchCondition.PUBLIC);
				refreshPilesAndShowRefreshing();
//				flodFilterAndClearCheck();
				break;
			}
			case R.id.btn_show_free_pile: {
				btn_show_free_pile.setSelected(!btn_show_free_pile.isSelected());
				searchCondition.setIsIdle(btn_show_free_pile.isSelected());
				if (getUserVisibleHint()) {
					pileDataUpdate();
				}
//				flodFilterAndClearCheck();
				break;
			}
		}
	}

	/**
	 * 点击筛选条件下拉按钮事件
	 */
	@Override
	public void onCheckedChanged(RadioGroup rg, int checkedId) {
		cancelHideConditionDelay();
		switch (checkedId) {
			case R.id.rb_distance:
				if (filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDED) {
					flodFilterAndClearCheck();
				} else {
					if (filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDING) {
						flodFilter();
					}
					if (searchCondition.getCenterLongitude() > 0 && searchCondition.getCenterLatitude() > 0) {
						distancePop.showAsDropDown(rg_condition2);
						v_touch.setVisibility(View.VISIBLE);
					} else {
						ToastUtil.show(getActivity(), "请选择具体地址");
						clearConditionCheck();
					}
				}
				break;
			case R.id.rb_sort:
				if (filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDED) {
					flodFilterAndClearCheck();
				} else {
					if (filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDING) {
						flodFilter();
					}
					sortPop.showAsDropDown(rg_condition2);
					v_touch.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.rb_filter:
				unflodFilter();
				break;
			default:
				break;
		}
	}

	/**
	 * 点击桩列表项事件
	 */
	@Override
	public void onItemClick(AdapterView<?> av, View v, int positionInListView, long id) {
		//这里的position是由listView提供的,会因为出现headerView的数量不同而跟adapter中的position不一致
		int positionInAdapter = positionInListView - lv_piles.getHeaderViewsCount();
		Pile pile = adapter.get(positionInAdapter);
		//		Log.d("pile_desp", "desp:" + pile.getDesp());
		if (pile == null) return;
		Intent intent = null;
		if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
			intent = new Intent(getActivity(), PileInfoNewActivity.class);
			intent.putExtra(KEY.INTENT.GPRS_TYPE, pile.getGprsType());
			LogUtils.e("MyPileFindFragment", "gprsType:" + pile.getGprsType());
		} else {
			intent = new Intent(getActivity(), PileStationInfoActivity.class);
		}
		intent.putExtra("isCollectedPile", false);
		intent.putExtra(KEY.INTENT.PILE_ID, pile.getPileId());
//		intent.putExtra(KEY.INTENT.START_TIME, searchCondition.getStartTime());
//		intent.putExtra(KEY.INTENT.END_TIME, searchCondition.getEndTime());
//		intent.putExtra(KEY.INTENT.DURATION, searchCondition.getDuration());
		intent.putExtra(KEY.INTENT.POSITION, positionInAdapter);
		intent.putExtra(KEY.INTENT.OPERATOR, pile.getOperator());
		if (myLatitude > 0 && myLongitude > 0 && pile.getLatitude() != null && pile.getLongitude() != null) {
			LatLng sLatLng = new LatLng(myLatitude, myLongitude);
			LatLng eLatLng = new LatLng(pile.getLatitude(), pile.getLongitude());
			double distance = DistanceUtil.getDistance(sLatLng, eLatLng) / 1000;
			intent.putExtra(KEY.INTENT.DISTANCE, distance);
		}
		getActivity().startActivityForResult(intent, PileFindFragment.REQUEST_REFRESH);
		getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

	}

	/**
	 * 筛选条件列表消失时，取消checkbox选中状态
	 */
	@Override
	public void onDismiss() {
		clearConditionCheck();
		v_touch.setVisibility(View.GONE);
	}

	private void clearConditionCheck() {
		rg_condition2.setOnCheckedChangeListener(null);
		rg_condition2.clearCheck();
		rg_condition2.setOnCheckedChangeListener(this);
	}

	/**
	 * 随着用户滑动列表，隐藏或显示搜索条件区域
	 */
	private OnTouchListener onTouchListListener = new OnTouchListener() {
		private float preY;// 上一个事件相对于window的Y坐标
		//		private int[] location = new int[2];// list在window中的坐标
		private boolean isMoveCondition;
		@Override
		public boolean onTouch(View view, MotionEvent event) {
//			lv_piles.getLocationInWindow(location);
			if (adapter.getCount() < 2) return false;
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isMoveCondition = false;
//					cancelHideConditionDelay();
//					preY = (int) (event.getY() + location[1]);
					preY = event.getY();
					// LogUtil.v("debug", "preY: " + preY);
					// LogUtil.v("debug", "event.getY(): " + event.getY());
					return false;

				case MotionEvent.ACTION_MOVE:
//					int curY = (int) (event.getY() + location[1]);
					float curY = event.getY();
					if (preY == curY) {
						lv_piles.setCanLayoutChildren(false);
						return true;
					}
					float moveUpDistance = preY - curY;
//					preY = curY;
					// LogUtil.e("debug", "preY: " + preY);
					// LogUtil.e("debug", "event.getY(): " + event.getY());
					// LogUtil.e("debug", "location[1]: " + location[1]);
					// LogUtil.e("debug", "moveUpDistance: " + moveUpDistance);
					// Log.e("debug",
					// "ll_condition.getPaddingTop(): "
					// + ll_condition.getPaddingTop());

					// 条件栏满足移动条件时，touch事件用于控制条件栏的padding
					// 同时listview也会移动位置，所以不需要传递事件来滚动列表
					// 可以向上移动
					if (moveUpDistance > 0 && -ll_condition.getPaddingTop() < conditionHeight) {
						lv_piles.setCanLayoutChildren(false);
						if (lv_piles.getFirstVisiblePosition() == 0) {
							moveCondition(moveUpDistance);
							isMoveCondition = true;
							preY = curY;
						} else {
							if(moveUpDistance > 5) {
								hideCondition();
								isMoveCondition = true;
								preY = curY;
							}
						}
//						isMoveCondition = true;
						return false;
					}

					// 可以向下移动
					if (moveUpDistance < -5 && ll_condition.getPaddingTop() < 0) {
						lv_piles.setCanLayoutChildren(false);
//						moveCondition(moveUpDistance);
						showCondition();
						isMoveCondition = true;
						preY = curY;
						return false;
					}

					if(moveUpDistance > 5 || moveUpDistance<-5){
						preY = curY;
					}
					// 在条件栏到顶或到底后，touch事件传递给listview去滚动
					// 但在第一次传递时，由于listview曾经改变过位置
					// 所以会重新调用adapter的getview，但实际上是不需要的，所以将最耗时的图片加载暂时禁止掉
					if (lv_piles.isCanLayoutChildren()) {
						adapter.setImageLoadable(true);
					} else {
						lv_piles.setCanLayoutChildren(true);
						// adapter.setImageLoadable(false);
					}
//					preY = curY;
					// lv_piles.setCanLayoutChildren(true);
					return false;
				case MotionEvent.ACTION_UP:
					preY = 0;
					lv_piles.setCanLayoutChildren(true);
					// adapter.setImageLoadable(false);
					// 滑动结束，如果condition超过一半显示在外，则显示完整，否则隐藏。
					if (-ll_condition.getPaddingTop() < conditionHeight / 2) {
						showCondition();
						hideConditionDelay();
					} else {
						hideCondition();
					}
					// LogUtil.v("debug", "event.getY(): " + event.getY());
					// 如果此次事件是移动condition后的弹起事件，有可能弹起的相对坐标和按下的相对坐标相同，
					// 会被误认为按list item
					if (isMoveCondition) {
						event.setLocation(0, event.getY());
					}
					return false;

				default:
					return false;
			}
		}
	};

	private void moveCondition(float moveUpDistance) {
		if (moveUpDistance - ll_condition.getPaddingTop() > conditionHeight + 10) {
			// 此次移动距离超过顶部，隐藏整个condition
			hideCondition();
		} else if (moveUpDistance - ll_condition.getPaddingTop() < 0) {
			// 此次移动距离超过底部，显示完整condition
			showCondition();
		} else {
			// 正常移动
			ll_condition.setPadding(ll_condition.getPaddingLeft(), (int) (ll_condition.getPaddingTop() - moveUpDistance), ll_condition.getPaddingRight(),
					ll_condition.getPaddingBottom());
		}
	}

	private void hideCondition() {
		ll_condition.setPadding(ll_condition.getPaddingLeft(), -conditionHeight, ll_condition.getPaddingRight(), ll_condition.getPaddingBottom());
		if (lv_piles.getFirstVisiblePosition() == 0) {
			lv_piles.scrollTo(0, conditionHeight);
		}
	}

	private void showCondition() {
		if (ll_condition != null) {
			ll_condition.setPadding(ll_condition.getPaddingLeft(), 0, ll_condition.getPaddingRight(), ll_condition.getPaddingBottom());
		}
	}

	private void hideConditionDelay() {
		// cancelHideConditionDelay();
		// conditionHideTimer = new Timer();
		// conditionHideTimer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// if (isVisible()
		// && -ll_condition.getPaddingTop() < conditionHeight) {
		// activity.runOnUiThread(moveConditionRunnable);
		// } else {
		// cancelHideConditionDelay();
		// hideCondition();
		// }
		// }
		// }, 3000, 10);
	}

	private Runnable moveConditionRunnable = new Runnable() {
		@Override
		public void run() {
			moveCondition(conditionMoveInterval);
		}
	};

	private LocationInfo city;

	private void cancelHideConditionDelay() {
		if (conditionHideTimer != null) {
			conditionHideTimer.cancel();
			conditionHideTimer = null;
		}
	}

	/**
	 * 加载列表
	 */
	private void refreshPilesAndShowRefreshing() {
		isRefresh = false;
		lv_piles.showRefreshing(true);
		lv_piles.setLoadable(false);
		lv_piles.completeLoad();
		refreshPiles();
	}

	private void refreshPiles() {
		if (adapter != null) {
			adapter.clear();
		}
		lv_piles.setLoadable(false);
		lv_piles.completeLoad();
		newHandler();
		WebAPIManager.getInstance().findPiles(userid, searchCondition, 0, MyConstant.PAGE_SIZE, loadPileHandler);
	}

	public void loadPiles() {
		newHandler();
		WebAPIManager.getInstance().findPiles(userid, searchCondition, adapter.getCount(), MyConstant.PAGE_SIZE, loadPileHandler);
	}

	private void newHandler() {
		if (loadPileHandler != null) {
			loadPileHandler.abandon();
		}
		loadPileHandler = new LoadPileHandler();
	}

	private class LoadPileHandler extends WebResponseHandler<List<Pile>> {
		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onError(Throwable e) {
			super.onError(e);
			if (getUserVisibleHint() == false) return;
			if (lv_piles.isRefreshing()) {
				lv_piles.showRefreshFail();
			} else {
				lv_piles.showLoadFail();
			}
			TipsUtil.showTips(getActivity(), e);
		}

		@Override
		public void onFailure(WebResponse<List<Pile>> response) {
			super.onFailure(response);
			if (getUserVisibleHint() == false) return;
			if (lv_piles.isRefreshing()) {
				lv_piles.showRefreshFail();
			} else {
				lv_piles.showLoadFail();
			}
			TipsUtil.showTips(getActivity(), response);
		}

		@Override
		public void onSuccess(WebResponse<List<Pile>> response) {
			super.onSuccess(response);
			if (getUserVisibleHint() == false) {
				return;
			}
			List<Pile> piles = response.getResultObj();
			if (lv_piles.isRefreshing()) {
				lv_piles.completeRefresh();
				adapter.clear();
			} else {
				lv_piles.completeLoad();
			}
			if (piles != null && piles.size() > 0) {
				adapter.add(piles);
			}
			if (piles != null && piles.size() >= MyConstant.PAGE_SIZE) {
				lv_piles.prepareLoad();
			} else {
				lv_piles.showNoMore();
			}
		}
	}

	/**
	 * 接收返回值
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.e("PileFindListFragment", "requestCode:" + requestCode);
		showCondition();
		switch (requestCode) {
			case PileFindFragment.REQUEST_CITY: {
				break;
			}
			case PileFindFragment.REQUEST_SEARCH: {
				if (data != null && locationOperater != null) {
					String cityName = data.getStringExtra(KEY.INTENT.CITY);
					String address = data.getStringExtra(KEY.INTENT.ADDRESS);
					locationOperater.setCity(cityName);
					locationOperater.setAddress(address);
				}
				updateConditionUI();
				refreshPilesAndShowRefreshing();
				break;
			}
			case PileFindFragment.REQUEST_TIME: {
				if (data != null) {
					long startTime = data.getLongExtra(KEY.INTENT.START_TIME, -1);
					long endTime = data.getLongExtra(KEY.INTENT.END_TIME, -1);
					double duration = data.getDoubleExtra(KEY.INTENT.DURATION, -1);
					searchCondition.setStartTime(startTime);
					searchCondition.setEndTime(endTime);
					searchCondition.setDuration(duration);
					//			listScreeningLayout.update();
				}
				break;
			}
			case PileFindFragment.REQUEST_REFRESH:
				if (data != null) {
					int position = data.getIntExtra(KEY.INTENT.POSITION, -1);
					String pileId = data.getStringExtra(KEY.INTENT.PILE_ID);
					boolean favor = data.getBooleanExtra(KEY.INTENT.IS_FAVOR, false);
					float avgLevel = data.getFloatExtra(KEY.INTENT.AVERAGE_LEVEL, 0f);
					LogUtils.e("PileFindListFragment", "favor:" + favor + "  | avgLevel:" + avgLevel);
					int runStatus = data.getIntExtra(KEY.INTENT.RUN_STATUS, 0);
					int operator = data.getIntExtra(KEY.INTENT.OPERATOR, 0);
					if (adapter != null) {
						int count = adapter.getCount();
						if (count > position && position != -1) {
							Pile pile = adapter.get(position);
							if (null != pile && pile.getPileId().equals(pileId)) {
								pile.setFavor(favor ? PileStation.FAVOR_YES : PileStation.FAVOR_NOT);
								pile.setAvgLevel(avgLevel);
								if (operator == Pile.OPERATOR_PERSONAL) pile.setRunStatus(runStatus);
								else pile.setIsIdle(runStatus);

							} else {
								for (int i = 0; i < count; i++) {
									if (((Pile) adapter.getItem(i)).getPileId().equals(pileId)) {
										((Pile) adapter.getItem(i)).setFavor(favor ? PileStation.FAVOR_YES : PileStation.FAVOR_NOT);
										((Pile) adapter.getItem(i)).setAvgLevel(avgLevel);
										if (operator == Pile.OPERATOR_PERSONAL) ((Pile) adapter.getItem(i)).setRunStatus(runStatus);
										else ((Pile) adapter.getItem(i)).setIsIdle(runStatus);
										break;
									}
								}
							}
						} else {
							for (int i = 0; i < count; i++) {
								if (((Pile) adapter.getItem(i)).getPileId().equals(pileId)) {
									((Pile) adapter.getItem(i)).setFavor(favor ? PileStation.FAVOR_YES : PileStation.FAVOR_NOT);
									((Pile) adapter.getItem(i)).setAvgLevel(avgLevel);
									if (operator == Pile.OPERATOR_PERSONAL) ((Pile) adapter.getItem(i)).setRunStatus(runStatus);
									else ((Pile) adapter.getItem(i)).setIsIdle(runStatus);
									break;
								}
							}
						}
						adapter.notifyDataSetChanged();
					}
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 显示搜索条件
	 */
	private void updateConditionUI() {
		if (searchCondition != null) {
			if (searchCondition.getDistance() == 500) {
				distancePop.select(R.string.distance_500m);
				rb_distance.setText(R.string.distance_500m);
			} else if (searchCondition.getDistance() == 1000) {
				distancePop.select(R.string.distance_1km);
				rb_distance.setText(R.string.distance_1km);
			} else if (searchCondition.getDistance() == 2000) {
				distancePop.select(R.string.distance_2km);
				rb_distance.setText(R.string.distance_2km);
			} else if (searchCondition.getDistance() == 5000) {
				distancePop.select(R.string.distance_5km);
				rb_distance.setText(R.string.distance_5km);
			} else {
				distancePop.select(R.string.no_limit);
				rb_distance.setText(R.string.distance);
			}

			if (searchCondition.getSortType() == CenterPointSearchCondition.SORT_HOT) {
				sortPop.select(R.string.sort_hot);
				rb_sort.setText(R.string.sort_hot);
			} else if (searchCondition.getSortType() == CenterPointSearchCondition.SORT_DISTANCE) {
				sortPop.select(R.string.sort_distance);
				rb_sort.setText(R.string.sort_distance);
			} else if (searchCondition.getSortType() == CenterPointSearchCondition.SORT_GRADE) {
				sortPop.select(R.string.sort_grade);
				rb_sort.setText(R.string.sort_grade);
			} else if (searchCondition.getSortType() == CenterPointSearchCondition.SORT_PRICE) {
				sortPop.select(R.string.sort_price);
				rb_sort.setText(R.string.sort_price);
			} else {
				sortPop.select(R.string.no_limit);
				rb_sort.setText(R.string.sort);
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void onVisible() {
		super.onVisible();
		showCondition();
		hideConditionDelay();
		if (isRefresh) refreshPilesAndShowRefreshing();
	}

	@Override
	protected void onInvisible() {
		super.onInvisible();
	}

	public ILocationOperater getLocationOperater() {
		return locationOperater;
	}

	public void setLocationOperater(ILocationOperater iLocationOperater) {
		this.locationOperater = iLocationOperater;
	}


	/**
	 * setter & getter
	 */
	public CenterPointSearchCondition getSearchCondition() {
		return searchCondition;
	}

	public void setSearchCondition(CenterPointSearchCondition searchCondition) {
		this.searchCondition = searchCondition;
	}

	@Override
	public void pileDataUpdate() {
		LogUtils.e("PileFindListFragment", "pileDataUpdate");
		refreshPilesAndShowRefreshing();
	}

	public void flodFilter() {
		v_touch.setVisibility(View.GONE);
		filterWindowAnimator.unextend();
//		if (getTargetFragment() != null) {
//			PileFindFragment targetFragment = (PileFindFragment) getTargetFragment();
//			targetFragment.hideBack();
//		}
//		refreshPilesAndShowRefreshing();
		//		if (getUserVisibleHint()) {
		//			listScreeningLayout.submit();
		//		}
	}

	public void flodFilterAndClearCheck() {
		if (null != filterWindowAnimator && filterWindowAnimator.getStatus() == ExtendAnimation.STATUS_EXTENDED) {
			flodFilter();
		}
		clearConditionCheck();
	}

	public void unflodFilter() {
		v_touch.setVisibility(View.VISIBLE);
		filterWindowAnimator.extend();
//		if (getTargetFragment() != null) {
//			PileFindFragment targetFragment = (PileFindFragment) getTargetFragment();
//			targetFragment.showBack();
//		}
	}

	private void selectView(ViewGroup viewGroup) {
		viewGroup.setSelected(true);
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View item = viewGroup.getChildAt(i);
			item.setSelected(true);
		}
	}

	private void unselectView(ViewGroup viewGroup) {
		viewGroup.setSelected(false);
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View item = viewGroup.getChildAt(i);
			item.setSelected(false);
		}
	}

	private void updateFilter(CenterPointSearchCondition centerPointSearchCondition) {
		btn_show_free_pile.setSelected(centerPointSearchCondition.getIsIdle());
		Set<Integer> pileType = centerPointSearchCondition.getPileTypes();
		//如果包含全部的桩类型的话
		if (pileType.contains(SearchCondition.PUBLIC) && pileType.contains(SearchCondition.CURRENT_DC) && pileType.contains(SearchCondition.CURRENT_AC)) {
			selectView(rl_pile_type_all);
			unselectView(rl_pile_type_personal);
			unselectView(rl_pile_type_studio);
		} else if (pileType.contains(SearchCondition.CURRENT_DC) && pileType.contains(SearchCondition.CURRENT_AC)) {//如果只包含私人类型
			selectView(rl_pile_type_personal);
			unselectView(rl_pile_type_all);
			unselectView(rl_pile_type_studio);
		} else if (pileType.contains(SearchCondition.PUBLIC)) {//如果只包含公共类型
			selectView(rl_pile_type_studio);
			unselectView(rl_pile_type_all);
			unselectView(rl_pile_type_personal);
		} else if (pileType.size() == 0) {//如果没有包含任何类型
			selectView(rl_pile_type_all);
			unselectView(rl_pile_type_personal);
			unselectView(rl_pile_type_studio);
			centerPointSearchCondition.addPileTypes(SearchCondition.PUBLIC);
			centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_DC);
			centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_AC);
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult geocoderesult) {
		if (geocoderesult != null && geocoderesult.error == ERRORNO.NO_ERROR) {
			city = LocationInfos.getInstance().getCities().get(searchCondition.getCityId());
			if (city == null || searchCondition.getCenterLatitude() < 0 || searchCondition.getCenterLongitude() < 0) {
				ToastUtil.show(getActivity(), getResources().getString(R.string.please_select_city));
			} else {
				adapter.clear();
				lv_piles.showRefreshing(true);
				lv_piles.setLoadable(false);
				LatLng ll = geocoderesult.getLocation();
				searchCondition.setCenterLongitude(ll.longitude);
				searchCondition.setCenterLatitude(ll.latitude);
				if (searchCondition.getDistance() <= 0) {
					searchCondition.setDistance(PileFindFragment.DEFAULT_DISTANCE);
				}
				refreshPiles();
			}

		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// do not thing
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		try {
			getActivity().unregisterReceiver(refreshPileList);
		} catch (Exception e) {
		}
	}
}
