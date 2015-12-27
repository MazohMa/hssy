package com.xpg.hssy.main.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.easy.util.BitmapUtil;
import com.easy.util.MeasureUtil;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.xpg.hssy.account.LoginActivity;
import com.xpg.hssy.animator.ExtendAnimate;
import com.xpg.hssy.animator.ExtendAnimation;
import com.xpg.hssy.animator.ExtendAnimator;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bean.CongruentPoint;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.bean.searchconditon.RectangleSearchCondition;
import com.xpg.hssy.bean.searchconditon.SearchCondition;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileStation;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.engine.LocationInfos.LocationInfo;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.main.fragment.callbackinterface.ILocationOperater;
import com.xpg.hssy.main.fragment.callbackinterface.IPileDataOperater;
import com.xpg.hssy.popwindow.MapPilesShowListPop;
import com.xpg.hssy.popwindow.PileInfoMapPop;
import com.xpg.hssy.util.MyOrientationListener;
import com.xpg.hssy.util.MyOrientationListener.OnOrientationListener;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.util.UIUtil;
import com.xpg.hssy.view.PilesShowInfoViews;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年3月26日
 */
public class PileFindMapCongruentPointFragment extends BaseFragment implements OnClickListener, IPileDataOperater, OnMarkerClickListener, OnMapClickListener,
		OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {
	/**
	 * 百度地图最大缩放级别
	 */
	private static final double MAX_LEVEL = 19.0;
	private CenterPointSearchCondition centerPointSearchCondition;// 中心点搜索
	private RectangleSearchCondition rectangleSearchCondition;// 矩形搜索

	private ImageButton ibtn_filter;//筛选按钮
	private RelativeLayout rl_pile_filter;// 筛选窗口

	private RelativeLayout rl_pile_type_all;
	private RelativeLayout rl_pile_type_personal;
	private RelativeLayout rl_pile_type_studio;
	private Button btn_show_free_pile;
	private ExtendAnimation filterWindowAnimator;// 筛选窗口动画

	private ILocationOperater operater;
	private PileInfoMapPop pileInfoMapPop;

	private static final int REQUEST_CITY = 1;
	private static final int REQUEST_INFO = 2;
	private static final int REQUEST_FILTER = 3;
	private static final int REQUEST_LOGIN = 4;
	private static final int REQUEST_NAVIGATION = 9;
	private static final int REQUEST_MAP = 10;
	private static final int CLICKBLE_FALSE_MARKER = 999;
	private static final boolean LOCATION_OPEN = true;
	private static final boolean LOCATION_CLOSE = false;
	public static final float ZOOM_LEVE_CITY = 12;
	private static final float ZOOM_LEVE_POINT = 17;
	private static final float ZOOM_LEVE_MID = 15;
	private static final double BAIDU_LAT_LON_DEFAULT_VALUE = 4.9E-324;
	private boolean isOpenBottomPop = false;
	private RelativeLayout rl_map;
	private RelativeLayout rl_map_p;
	private RelativeLayout.LayoutParams lp;
	private ImageView iv_map_center_mark;
	private ImageButton btLocation;
	private TextView tv_map_pile_position;
	private TextView tv_map_pile_distance;
	private TextView tv_map_pile_share_time_first;
	private TextView tv_map_pile_share_time_second;
	private TextView tv_map_pile_share_time_third;
	private TextView map_pile_share_time;
	private Dialog dialog;

	private MapView mapView;
	private BaiduMap baiduMap;
	private Marker marker;
	private Marker bigMarker;
	private RoutePlanSearch mSearch;
	private BDLocation mLocation;
	private DrivingRouteOverlay routeOverlay;
	private Marker locationMarker;
	private LocationClient mLocClient;
	private View v_touch;
	private SPFile sp;
	private Map<Marker, CongruentPoint> pileMarkers = new HashMap<Marker, CongruentPoint>();
	private WebResponseHandler<List<CongruentPoint>> handler;
	private boolean isCollecResult; // 收藏功能登录回调
	private boolean isShowInfowin;
	private boolean isFirstLoc = true;// 是否首次定位
	// private boolean needRefreshAddress;// 是否需要刷新地址信息

	private MyLocationListenner myListener = new MyLocationListenner();
	private MyOrientationListener myOrientationListener;
	private float mXDirection; // 方向传感器X方向的值
	private MapPilesShowListPop mapPilesShowListPop;
	private LayoutInflater mInflater;
	private InfoWindow mInfoWindow;
	private int[] drawIcons = new int[]{R.drawable.map_personal_wifi, R.drawable.map_personal, R.drawable.map_public_wifi, R.drawable.map_public};

	public static Bitmap[] bitMapStrings = new Bitmap[4];
	private Pile pile;
	// private int currentIndex = 999;
	private boolean isFirstClickPopItem = true;

	private PilesShowInfoViews pilesShowInfoViews;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// 初始化城市定位，注册事件监听
		// mCitySearch = GeoCoder.newInstance();
		// mCitySearch.setOnGetGeoCodeResultListener(this);
		// getLocation();// 桩信息页面距离要的当前的位置初始获得;
		initData();
		View v = initView(inflater);
		initListener();
		// 线程请求
		mapViewPostDelayed();
		// needRefreshAddress = true;
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mapView != null) rl_map.addView(mapView);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mapView != null) mapView.onResume();
		if (baiduMap != null) {
			baiduMap.setMyLocationEnabled(true);
			if (mInfoWindow != null) {
				baiduMap.hideInfoWindow();
				mInfoWindow = null;
			}
		}
		if (pileInfoMapPop.isShowing()) {
			pileInfoMapPop.dismiss();
		}
		updateFilter(centerPointSearchCondition);
		initMapFocus();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (baiduMap != null) baiduMap.setMyLocationEnabled(false);
		if (mapView != null) mapView.onPause();
	}

	@Override
	public void onStop() {
		if (mapView != null) rl_map.removeView(mapView);
		super.onStop();
	}

	private void initData() {
		mInflater = LayoutInflater.from(getActivity());
		sp = new SPFile(getActivity(), "config");
		if (bitMapStrings[0] == null) {
			for (int i = 0; i < bitMapStrings.length; i++) {
				bitMapStrings[i] = BitmapUtil.get(getActivity(), drawIcons[i]);
			}
		}
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_order, null);
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		// initCondition();
	}

	private View initView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.pile_find_map_2_fragment, null);
		v_touch = v.findViewById(R.id.v_touch);
		UIUtil.hideSoftInput(getActivity(), v);
		iv_map_center_mark = (ImageView) v.findViewById(R.id.iv_map_center_mark);
		iv_map_center_mark.setEnabled(false);
		btLocation = (ImageButton) v.findViewById(R.id.btn_Location);
		rl_map = (RelativeLayout) v.findViewById(R.id.rl_map);
		rl_map_p = (RelativeLayout) v.findViewById(R.id.map_p);
		tv_map_pile_position = (TextView) v.findViewById(R.id.tv_map_pole_position);
		tv_map_pile_distance = (TextView) v.findViewById(R.id.tv_map_pole_distance);
		tv_map_pile_share_time_first = (TextView) v.findViewById(R.id.tv_map_pile_share_time_first);
		tv_map_pile_share_time_second = (TextView) v.findViewById(R.id.tv_map_pile_share_time_second);
		tv_map_pile_share_time_third = (TextView) v.findViewById(R.id.tv_map_pile_share_time_third);
		map_pile_share_time = (TextView) v.findViewById(R.id.map_pile_share_time);

		hideMapPoleInfo();
		//筛选按钮
		ibtn_filter = (ImageButton) v.findViewById(R.id.ibtn_filter);
		// 筛选窗口
		rl_pile_filter = (RelativeLayout) v.findViewById(R.id.rl_pile_filter);
		//筛选类型
		rl_pile_type_all = (RelativeLayout) v.findViewById(R.id.rl_pile_type_all);
		rl_pile_type_personal = (RelativeLayout) v.findViewById(R.id.rl_pile_type_personal);
		rl_pile_type_studio = (RelativeLayout) v.findViewById(R.id.rl_pile_type_studio);
		btn_show_free_pile = (Button) v.findViewById(R.id.btn_show_free_pile);
		rl_pile_filter.setVisibility(View.GONE);
		filterWindowAnimator = new ExtendAnimation(rl_pile_filter, ExtendAnimator.ORIENTATION_TOP | ExtendAnimator.ORIENTATION_RIGHT);
		filterWindowAnimator.setOnAnimateListener(new ExtendAnimate.OnAnimateListener() {
			@Override
			public void onStart() {
				ibtn_filter.setEnabled(false);
			}

			@Override
			public void onEnd() {
				ibtn_filter.setEnabled(true);
			}
		});

		// 创建百度地图
		BaiduMapOptions mapOptions = new BaiduMapOptions().zoomControlsEnabled(false).rotateGesturesEnabled(false).scaleControlEnabled(false).compassEnabled
				(false).overlookingGesturesEnabled(false);
		mapView = new MapView(getActivity(), mapOptions);

		//隐藏百度地图的logo
		View baiduLogo = mapView.getChildAt(1);
		if (baiduLogo != null && (baiduLogo instanceof ImageView || baiduLogo instanceof ZoomControls)) {
			baiduLogo.setVisibility(View.GONE);
		}

		baiduMap = mapView.getMap();

		// 开启定位图层
		baiduMap.setMyLocationEnabled(true);
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));

		// 定位初始化
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
		// 开启定位
		mLocClient.start();
		baiduMap.setMyLocationEnabled(true);

		initOritationListener();
		myOrientationListener.start();

		MeasureUtil.getScreenWidth(getActivity());

		pileInfoMapPop = new PileInfoMapPop(getActivity(), null, null, null);
		return v;
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

	private void initListener() {
		btLocation.setOnClickListener(this);
		v_touch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					unextendFilterWindow();
				}
				return true;
			}
		});

		ibtn_filter.setOnClickListener(this);
		rl_pile_type_all.setOnClickListener(this);
		rl_pile_type_personal.setOnClickListener(this);
		rl_pile_type_studio.setOnClickListener(this);
		btn_show_free_pile.setOnClickListener(this);
		baiduMap.setOnMapClickListener(this);
		baiduMap.setOnMarkerClickListener(this);
		baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			LatLng topLeftLatLng;
			LatLng centerLatLng;
			float zoom;
			double miniMapMoveOffset;// 引起电桩重新加载的最小变化值,与量纲无关,只取数值作为判断标准
			boolean isFirstTimeChange = true;


			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				if (baiduMap.getProjection() == null) {
					return;
				}
				/*
				 * if (pileInfoMapPop != null) { if (pileInfoMapPop.isShowing())
				 * { pileInfoMapPop.dismiss(); } }
				 */
				// if (baiduMap != null && mInfoWindow != null) {
				// baiduMap.hideInfoWindow();
				// mInfoWindow = null;
				// }
				if (baiduMap != null && isFirstTimeChange) {
					isFirstTimeChange = false;
					topLeftLatLng = baiduMap.getProjection().fromScreenLocation(new Point(0, 0));
					centerLatLng = arg0.target;
					// 取地图边缘距离中心点的经度数值的1/5作为标准量,约为屏幕的1/10宽度
					miniMapMoveOffset = Math.abs(centerLatLng.longitude - topLeftLatLng.longitude) / 5;
					zoom = arg0.zoom;
				}

				Log.d("startXY", arg0.targetScreen.x + " " + arg0.targetScreen.y);
				Log.d("mapStatusChange", "start " + topLeftLatLng.longitude + " " + centerLatLng.longitude + " " + miniMapMoveOffset);
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				if (baiduMap.getProjection() == null) {
					return;
				}

				// TODO 缩放不比例没变化的时候，防止向服务器提交数据，目的在点击覆盖物的时候弹出的pile list 位置一致 2015
				// 06 26 by Mazoh
				if (mInfoWindow != null && arg0.zoom == zoom) {
					return;
				} else {
					if (baiduMap != null && mInfoWindow != null) {
						baiduMap.hideInfoWindow();
						if (pileInfoMapPop != null && pileInfoMapPop.isShowing()) {
							pileInfoMapPop.dismiss();
						}
						mInfoWindow = null;
					}
				}
				// TODO END
				// Log.d("mapStatusChange", "end " + topLeftLatLng.longitude +
				// " "
				// + centerLatLng.longitude + " "
				// + (centerLatLng.longitude - topLeftLatLng.longitude));
				// 当地图不为空,中心经纬度不为空,经度或纬度变化量大于最小变化量,或进行了缩放,则刷新地图上的电桩
				if (baiduMap != null && centerLatLng != null && (Math.abs(arg0.target.longitude - centerLatLng.longitude) >= miniMapMoveOffset || Math.abs
						(arg0.target.latitude - centerLatLng.latitude) >= miniMapMoveOffset || arg0.zoom != zoom)) {
					topLeftLatLng = baiduMap.getProjection().fromScreenLocation(new Point(0, 0));
					centerLatLng = arg0.target;
					miniMapMoveOffset = Math.abs(centerLatLng.longitude - topLeftLatLng.longitude) / 5;
					zoom = arg0.zoom;

					doOnMapStatusChange(arg0);
					// 实时改变搜索条件中的中心点和距离，以便列表模式加载时使用
					centerPointSearchCondition.setCenterLatitude(centerLatLng.latitude);
					centerPointSearchCondition.setCenterLongitude(centerLatLng.longitude);
					centerPointSearchCondition.setZoom(arg0.zoom);
				} else {
					isFirstClickPopItem = true;
				}
				// needRefreshAddress = true;
				LbsManager.getInstance().getAddressByLocation(arg0.target, PileFindMapCongruentPointFragment.this);
				Log.d("endXY", arg0.targetScreen.x + " " + arg0.targetScreen.y);
			}

			@Override
			public void onMapStatusChange(MapStatus arg0) {
			}
		});
	}

	// 实例化地址聚合，调用geocode，设置到地图中心
	/*
	 * 步骤： 1 实例化地图数据 2 把经纬度设到地图中心点（moveMapTo（））
	 * 3通过编译（反编译）经纬度/信息，在接收回调函数里面实时监控通过result对象获取address，设置textview值
	 */
	public void initMapFocus() {
		// TODO null bug fix
		if (centerPointSearchCondition != null) {

			if (centerPointSearchCondition.getCenterLatitude() != PileFindFragment.LAT_LON_DEFAULT_VALUE && centerPointSearchCondition.getCenterLongitude() !=
					PileFindFragment.LAT_LON_DEFAULT_VALUE) {
				float zoom = centerPointSearchCondition.getZoom();
				if (zoom == -1) {
					zoom = ZOOM_LEVE_MID;
					centerPointSearchCondition.setZoom(zoom);
				}
				moveMapTo(centerPointSearchCondition.getCenterLatitude(), centerPointSearchCondition.getCenterLongitude(), true, zoom);
			} else if (!TextUtils.isEmpty(centerPointSearchCondition.getCityId())) {// cityId非空
				// 地图中心获取城市位置
				LocationInfo city = LocationInfos.getInstance().getCities().get(centerPointSearchCondition.getCityId());
				// 编译经纬度》地址
				if (city != null) {// 城市真实存在
					String address = centerPointSearchCondition.getAddress();
					// 如果地址为空,用城市名作为地址
					if (address == null) {
						address = city.getName();
					}
					LbsManager.getInstance().getLocationByAddress(city.getName(), address, this);
				}
				return;
				// 这个语句执行完之后返回,不再执行下面语句
			}
		}
		// 根据地图中心点返回地址信息
		if (baiduMap != null && baiduMap.getMapStatus() != null) {
			LbsManager.getInstance().getAddressByLocation(baiduMap.getMapStatus().target, this);
		}
		// TODO END
	}

	private void showLocationBtBottom() {
		lp = (RelativeLayout.LayoutParams) btLocation.getLayoutParams();

		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		// lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btLocation.setLayoutParams(lp);
		// rl_map_p.removeView(btLocation);
		// rl_map_p.addView(btLocation, lp);
	}

	private void showLcationBtAbove() {
		lp = (RelativeLayout.LayoutParams) btLocation.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
		lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		btLocation.setLayoutParams(lp);
	}

	private void loadPile(LatLng startLatLng, LatLng endLatlng) {
		rectangleSearchCondition.setStartLat(startLatLng.latitude);
		rectangleSearchCondition.setStartLong(startLatLng.longitude);
		rectangleSearchCondition.setEndLatitude(endLatlng.latitude);
		rectangleSearchCondition.setEndLongitude(endLatlng.longitude);
		WebAPIManager.getInstance().getCongruentPoint(MAX_LEVEL, rectangleSearchCondition, handler);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case PileFindFragment.REQUEST_CITY: {
					// do not thing
					break;
				}
				case PileFindFragment.REQUEST_SEARCH: {
					// do not thing
					break;
				}
				case PileFindFragment.REQUEST_TIME: {
					long startTime = data.getLongExtra(KEY.INTENT.START_TIME, -1);
					long endTime = data.getLongExtra(KEY.INTENT.END_TIME, -1);
					double duration = data.getDoubleExtra(KEY.INTENT.DURATION, -1);
					centerPointSearchCondition.setStartTime(startTime);
					centerPointSearchCondition.setEndTime(endTime);
					centerPointSearchCondition.setDuration(duration);
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public synchronized void stop() {
		if (handler != null) {
			handler.abandon();
			handler = null;
		}
	}

	/**
	 * 打开聚合点到合适的距离，令聚合点散开
	 *
	 * @param cp
	 */
	private void moveMapTo(CongruentPoint cp) {
		if (cp.getTopRightLat() - cp.getBottomLeftLat() < 0.0005 && cp.getTopRightLong() - cp.getBottomLeftLong() < 0.0005) {
			moveMapTo(cp.getAvgLat(), cp.getAvgLong(), true, 19);
			return;
		}

		double topRightLatMirror = 2 * cp.getAvgLat() - cp.getTopRightLat();
		double topRightLongMirror = 2 * cp.getAvgLong() - cp.getTopRightLong();
		double bottomLeftLatMirror = 2 * cp.getAvgLat() - cp.getBottomLeftLat();
		double bottomLeftLongMirror = 2 * cp.getAvgLong() - cp.getBottomLeftLong();
		LatLng llTopRight = new LatLng(cp.getTopRightLat(), cp.getTopRightLong());
		LatLng llBottomLeft = new LatLng(cp.getBottomLeftLat(), cp.getBottomLeftLong());
		LatLng llTopRightMirror = new LatLng(topRightLatMirror, topRightLongMirror);
		LatLng llBottomLeftMirror = new LatLng(bottomLeftLatMirror, bottomLeftLongMirror);
		LatLngBounds.Builder llbb = new LatLngBounds.Builder();
		llbb.include(llTopRight);
		llbb.include(llBottomLeft);
		llbb.include(llTopRightMirror);
		llbb.include(llBottomLeftMirror);

		baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(llbb.build()));
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @param isAnimate
	 * @param zoomLevel 调整地图的缩放比例
	 */
	private void moveMapTo(double latitude, double longitude, boolean isAnimate, float zoomLevel) {
		// needRefreshAddress = true;
		MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(latitude, longitude)).zoom(zoomLevel).build();
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		if (null != msu) {
			if (isAnimate) {
				// 设置中心点,移动到中心点
				baiduMap.animateMapStatus(msu);
			} else {
				baiduMap.setMapStatus(msu);
			}
		}
	}

	private void moveMapTo(double latitude, double longitude, boolean isAnimate) {
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(latitude, longitude));
		if (baiduMap == null) {
			return;
		}
		if (isAnimate) {
			baiduMap.animateMapStatus(msu);
		} else {
			baiduMap.setMapStatus(msu);
		}
	}

	private void doOnMapStatusChange(final MapStatus status) {// status.target//获取地图中当前的中心点
		if (getActivity() == null || baiduMap == null) {
			return;
		}
		stop();

		rectangleSearchCondition.setZoom(status.zoom);
		// TODO getProjection()容易导致null exception, unfix

		LatLng screenStartLatLng = baiduMap.getProjection().fromScreenLocation(new Point(mapView.getLeft(), mapView.getBottom()));
		LatLng screenEndLatLng = baiduMap.getProjection().fromScreenLocation(new Point(mapView.getRight(), mapView.getTop()));

		handler = new WebResponseHandler<List<CongruentPoint>>(this.getClass().getSimpleName()) {

			@Override
			public void onSuccess(WebResponse<List<CongruentPoint>> response) {
				super.onSuccess(response);
				if (!isVisible()) {
					return;
				}
				if (baiduMap == null || !isVisible()) {
					return;
				}
				// 获得CongruentPoint里面相应的经纬度
				// baiduMap.clear();
				for (Marker marker : pileMarkers.keySet()) {
					marker.remove();
				}
				pileMarkers.clear();
				List<CongruentPoint> congruentPoints = response.getResultObj();
				if (congruentPoints != null) {
					for (CongruentPoint congruentPoint : congruentPoints) {
						double lat = congruentPoint.getAvgLat();
						double lng = congruentPoint.getAvgLong();
						LatLng latlng = new LatLng(lat, lng);
						// 相应的经纬度添加对应的覆盖物
						Marker marker = (Marker) baiduMap.addOverlay(new MarkerOptions().position(latlng).icon(getIconBD(congruentPoint)).anchor(0.5f, 0.5f));
						pileMarkers.put(marker, congruentPoint);
					}
				}
			}
		};

		// 请求之前，做一次本地过滤;
		// filterLocalPile();
		loadPile(screenStartLatLng, screenEndLatLng);
	}

	private void getCollectedTag(Pile pile) {

		WebAPIManager.getInstance().getFavorById(sp.getString("user_id", ""), pile.getPileId(), new WebResponseHandler<Boolean>(getActivity()) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				TipsUtil.showTips(getActivity(), e, R.string.get_collectedtag_fail);
			}

			@Override
			public void onFailure(WebResponse<Boolean> response) {
				super.onFailure(response);
				TipsUtil.showTips(getActivity(), response, R.string.get_collectedtag_fail);
			}

			@Override
			public void onSuccess(WebResponse<Boolean> response) {
				super.onSuccess(response);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void showMapPileInfo(Pile pile) {
//		showLcationBtAbove();
		if (isLogin()) {
			getCollectedTag(pile);
		}
		iv_map_center_mark.setVisibility(View.GONE);
		if (baiduMap != null && pile != null) {
//			if (mLocation != null) {
			pileInfoMapPop.setPile(pile);
			pileInfoMapPop.setmLocation(mLocation);
			pileInfoMapPop.setSearchCondition(centerPointSearchCondition);
			pileInfoMapPop.init();
			pileInfoMapPop.setAnimationStyle(R.style.mypopwindow_anim_style);
			if (pileInfoMapPop.isShowing()) {
				pileInfoMapPop.dismiss();
			} else {
				pileInfoMapPop.showAtLocation(ibtn_filter, Gravity.BOTTOM, 0, 0);
			}
			if (changeLeftBtnListener != null) {
				changeLeftBtnListener.changeLeftBtn(pileInfoMapPop, 0);
			}

			pileInfoMapPop.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					isFirstClickPopItem = true;
					if (bigMarker != null) {
						bigMarker.remove();
					}
					hideMapPoleInfo();
					if (baiduMap != null) {
						baiduMap.hideInfoWindow();
					}
					if (changeLeftBtnListener != null) {
						changeLeftBtnListener.changeLeftBtn(pileInfoMapPop, 1);
					}
				}
			});
//			} else {
//				ToastUtil.show(activity, "尚未定位您的位置,请稍后");
//			}
		}
	}

	private ChangeLeftBtnListener changeLeftBtnListener;

	public ChangeLeftBtnListener getChangeLeftBtnListener() {
		return changeLeftBtnListener;
	}

	public void setChangeLeftBtnListener(ChangeLeftBtnListener changeLeftBtnListener) {
		this.changeLeftBtnListener = changeLeftBtnListener;
	}

	public interface ChangeLeftBtnListener {
		public void changeLeftBtn(PileInfoMapPop pileInfoMapPop, int status);
	}

	private void hideMapPoleInfo() {
		iv_map_center_mark.setVisibility(View.VISIBLE);
		showLocationBtBottom();
	}

	private boolean isLogin() {
		return sp.getBoolean("isLogin", false);

	}

	private void mapViewPostDelayed() {
		mapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (getActivity() == null || baiduMap == null || baiduMap.getProjection() == null) {
					mapView.postDelayed(this, 500);
					return;
				}
				// 缩放地图
				doOnMapStatusChange(baiduMap.getMapStatus());
			}
		}, 500);
		if (getActivity() != null && getActivity() instanceof MainActivity) {
			((MainActivity) getActivity()).setScrollable(false);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recycleRes();
		if (baiduMap != null) {
			baiduMap.setMyLocationEnabled(false);
		}
		if (mapView != null) mapView.onDestroy();
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (getActivity() == null) {
			return;
		}
		if (result != null && result.error == ERRORNO.NO_ERROR) {
			float zoom = centerPointSearchCondition.getZoom();
			if (zoom == -1) {
				zoom = ZOOM_LEVE_CITY;
				centerPointSearchCondition.setZoom(zoom);
			}
			moveMapTo(result.getLocation().latitude, result.getLocation().longitude, true, zoom);
			LbsManager.getInstance().getAddressByLocation(new LatLng(result.getLocation().latitude, result.getLocation().longitude), this);
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (getActivity() == null) {
			return;
		}
		if (result != null && result.error == ERRORNO.NO_ERROR) {
			if (/* needRefreshAddress && */operater != null && result.getAddressDetail() != null) {
				//				tv_location_message.setText(result.getAddress());
				operater.setCity(result.getAddressDetail().city);
				String proStr = result.getAddressDetail().province;
				String cityStr = result.getAddressDetail().city;
				LocationInfo city = LocationInfos.getInstance().getCityByName(proStr, cityStr);
				if (city == null) {
					centerPointSearchCondition.setCityId(null);
				} else {
					centerPointSearchCondition.setCityId(city.getCode());
				}
				centerPointSearchCondition.setAddress(result.getAddress());
				operater.setAddress(result.getAddress());
			}
		}
		// needRefreshAddress = false;
	}

	@Override
	public void onMapClick(LatLng paramLatLng) {
		if (baiduMap != null && mInfoWindow != null) {
			baiduMap.hideInfoWindow();
			mInfoWindow = null;
		}
		if (pileInfoMapPop.isShowing()) {
			Log.i("isShowing", "isShowing");
			pileInfoMapPop.dismiss();
		}
		if (isShowInfowin) {
			hideMapPoleInfo();
			baiduMap.hideInfoWindow();
			isShowInfowin = false;
			Set<Marker> markers = pileMarkers.keySet();
			for (Marker mMarker : markers) {
				mMarker.setVisible(true);
			}
		}
		if (routeOverlay != null) {
			routeOverlay.removeFromMap();
			routeOverlay = null;
		}

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	// 点击覆盖物事件，Marker参数
	@Override
	public boolean onMarkerClick(Marker paramMarker) {
		if (paramMarker.getZIndex() != CLICKBLE_FALSE_MARKER) {
			if (pileInfoMapPop != null && pileInfoMapPop.isShowing()) {
				pileInfoMapPop.dismiss();
			}
			final CongruentPoint congruentPoint = pileMarkers.get(paramMarker);
			// 避免点击路线规划起终点图标crash
			if (congruentPoint == null || !paramMarker.isVisible()) {
				return false;
			}
			if (baiduMap != null && mInfoWindow != null) {
				baiduMap.hideInfoWindow();
				mInfoWindow = null;

			}
			if (congruentPoint.getNum() == 1) {

				this.marker = paramMarker;

				Set<Marker> markers = pileMarkers.keySet();
				// 放大该图标
				Bitmap bmp = getSingleIcon(congruentPoint.getPile());
				bmp = BitmapUtil.scale(bmp, 1.5f);
				bigMarker = (Marker) baiduMap.addOverlay(new MarkerOptions().position(paramMarker.getPosition()).icon(BitmapDescriptorFactory.fromBitmap(bmp))
						.anchor(0.5f, 0.5f).zIndex(99));
				// isFirstClickPopItem = false;
				// 点击覆盖物时候，覆盖物位置自动设为中心点
				moveMapTo(marker.getPosition().latitude, marker.getPosition().longitude, true);
				// 放到最大19.0时候没有聚合，展现单独的桩，点击屏幕下方show出桩信息
				pile = congruentPoint.getPile();
				showMapPileInfo(pile);
			} else {
				// TODO 最新接口congruentPoint.add(Piles),判断piles
				// 是否为null，若为null，则地图缩放不是最大，否则为最大

				if (congruentPoint.getPiles() != null && congruentPoint.getPiles().size() > 0) {
					moveMapTo(congruentPoint.getAvgLat(), congruentPoint.getAvgLong(), true, baiduMap.getMapStatus().zoom);
					final LatLng ll = paramMarker.getPosition();
					if (pilesShowInfoViews != null) {
						pilesShowInfoViews = null;
					}
					if (congruentPoint.getPiles().size() == 2) {
						pilesShowInfoViews = (PilesShowInfoViews) mInflater.inflate(R.layout.map_piles_list_pops_for_two, null);
					} else {
						pilesShowInfoViews = (PilesShowInfoViews) mInflater.inflate(R.layout.map_piles_list_pops, null);
					}
					pilesShowInfoViews.setPiles(congruentPoint.getPiles());
					pilesShowInfoViews.setBitMapStrings(bitMapStrings);
					pilesShowInfoViews.setPileInfoMapPop(pileInfoMapPop);
					pilesShowInfoViews.init();
					mInfoWindow = new InfoWindow(pilesShowInfoViews, ll, -47);
					baiduMap.showInfoWindow(mInfoWindow);
					if (isFirstClickPopItem) {
						Log.i("isFirstClickPopItem", "====isFirstClickPopItem");

						// showMapPileInfo(pile);
						showMapPileInfo(congruentPoint.getPiles().get(0));
						isFirstClickPopItem = false;
					}
					final LinearLayout layout = (LinearLayout) pilesShowInfoViews.findViewById(R.id.listlinear);

					pilesShowInfoViews.setMyOnItemClickListener(new PilesShowInfoViews.MyOnItemClickListener() {

						@Override
						public void myOnItemClick(int index, Pile pile) {
							Log.i("index", index + "");
							if (index != 0) {
								// Log.i("index", index + "");
								// layout.getChildAt(0).setPressed(false);
								// layout.getChildAt(index)
								// .setPressed(true) ;
							}
							pileInfoMapPop.setPile(pile);
							pileInfoMapPop.setmLocation(mLocation);
							pileInfoMapPop.setSearchCondition(centerPointSearchCondition);
							pileInfoMapPop.init();
							// currentIndex = index;
						}

					});

				} else {
					if (congruentPoint.getBottomLeftLat() == 0) {
						moveMapTo(congruentPoint.getAvgLat(), congruentPoint.getAvgLong(), true, baiduMap.getMapStatus().zoom + 1);
					} else {
						moveMapTo(congruentPoint);
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_Location: {
				if (baiduMap == null || baiduMap.getLocationData() == null || baiduMap.getLocationData().latitude == BAIDU_LAT_LON_DEFAULT_VALUE || baiduMap
						.getLocationData().longitude == BAIDU_LAT_LON_DEFAULT_VALUE) {
					ToastUtil.show(getActivity(), "无法定位，请打开无线网络或GPS");
					return;
				}
				if (pileInfoMapPop.isShowing()) {
					pileInfoMapPop.dismiss();
				}
				if (baiduMap != null && mInfoWindow != null) {
					baiduMap.hideInfoWindow();
					mInfoWindow = null;
				}
				moveMapTo(baiduMap.getLocationData().latitude, baiduMap.getLocationData().longitude, true);
				break;
			}
			case R.id.ibtn_filter: {
				if (v_touch.getVisibility() == View.VISIBLE) {
					unextendFilterWindow();
				} else {
					rl_pile_filter.setVisibility(View.VISIBLE);
					extendFilterWindow();
				}
				break;
			}

			case R.id.rl_pile_type_all: {
				selectView(rl_pile_type_all);
				unselectView(rl_pile_type_personal);
				unselectView(rl_pile_type_studio);
				centerPointSearchCondition.getPileTypes().clear();
				centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_DC);
				centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_AC);
				centerPointSearchCondition.addPileTypes(SearchCondition.PUBLIC);
				if (getUserVisibleHint()) {
					pileDataUpdate();
				}
//				unextendFilterWindow();
				break;
			}
			case R.id.rl_pile_type_personal: {
				selectView(rl_pile_type_personal);
				unselectView(rl_pile_type_all);
				unselectView(rl_pile_type_studio);
				centerPointSearchCondition.getPileTypes().clear();
				centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_DC);
				centerPointSearchCondition.addPileTypes(SearchCondition.CURRENT_AC);
				if (getUserVisibleHint()) {
					pileDataUpdate();
				}
//				unextendFilterWindow();
				break;
			}
			case R.id.rl_pile_type_studio: {
				selectView(rl_pile_type_studio);
				unselectView(rl_pile_type_all);
				unselectView(rl_pile_type_personal);
				centerPointSearchCondition.getPileTypes().clear();
				centerPointSearchCondition.addPileTypes(SearchCondition.PUBLIC);
				if (getUserVisibleHint()) {
					pileDataUpdate();
				}
//				unextendFilterWindow();
				break;
			}
			case R.id.btn_show_free_pile: {
				btn_show_free_pile.setSelected(!btn_show_free_pile.isSelected());
				centerPointSearchCondition.setIsIdle(btn_show_free_pile.isSelected());
				if (getUserVisibleHint()) {
					pileDataUpdate();
				}
//				unextendFilterWindow();
				break;
			}
		}
	}

	private void naviByOtherMap() {
		WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(getActivity());
		waterBlueDialogComfirmNav.show(baiduMap.getLocationData().latitude, baiduMap.getLocationData().longitude, marker.getPosition().latitude, marker
				.getPosition().longitude);
	}

	private void startNavigation() {
		// 路线规划
		if (baiduMap.getLocationData() == null) {
			ToastUtil.show(getActivity(), "无法获取你的位置信息，请开启定位");
			return;
		}
		PlanNode stNode = PlanNode.withLocation(new LatLng(baiduMap.getLocationData().latitude, baiduMap.getLocationData().longitude));
		PlanNode enNode = PlanNode.withLocation(marker.getPosition());
		mSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
	}

	private void collect() {
		if (marker == null) return;

		Pile pile = pileMarkers.get(marker).getPile();
		if (pile == null) return;

		String userid = sp.getString("user_id", null);
		if (!isLogin() || userid == null) {
			startActivity(LoginActivity.class);
			return;
		}

		WebAPIManager.getInstance().addFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(getActivity()) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(getActivity(), R.string.collect_fail);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				ToastUtil.show(getActivity(), R.string.collect_fail);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(getActivity(), R.string.collect_success);
			}

		});
	}

	private void cancelCollect() {
		if (marker == null) return;

		Pile pile = pileMarkers.get(marker).getPile();
		if (pile == null) return;

		String userid = sp.getString("user_id", null);
		if (!isLogin() || userid == null) {
			startActivity(LoginActivity.class);
			return;
		}

		WebAPIManager.getInstance().removeFavoritesPile(userid, pile.getPileId(), new WebResponseHandler<Object>(getActivity()) {
			@Override
			public void onError(Throwable e) {
				super.onError(e);
				ToastUtil.show(getActivity(), R.string.cancel_collect_fail);
			}

			@Override
			public void onFailure(WebResponse<Object> response) {
				super.onFailure(response);
				ToastUtil.show(getActivity(), R.string.cancel_collect_fail);
			}

			@Override
			public void onSuccess(WebResponse<Object> response) {
				super.onSuccess(response);
				ToastUtil.show(getActivity(), R.string.cancel_collect_success);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

	private void startActivity(Class<? extends Activity> activityClass) {
		getActivity().startActivity(new Intent(getActivity(), activityClass));
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {

	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			DrivingRouteLine route = result.getRouteLines().get(0);
			// 创建路线规划线路覆盖物
			routeOverlay = new DrivingRouteOverlay(baiduMap);
			// 设置路线规划数据
			routeOverlay.setData(route);
			// 将路线规划覆盖物添加到地图中
			routeOverlay.addToMap();
			// routeOverlay.zoomToSpan();
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {

	}

	@Override
	protected void onVisible() {
		super.onVisible();
		// 开启定位
		mLocClient.start();
		// 开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onInvisible() {
		super.onInvisible();
		// 结束定位
		if (mLocClient != null) mLocClient.stop();
		// 结束方向传感器
		if (myOrientationListener != null) myOrientationListener.stop();
	}

	@Override
	public void onChanged() {
		super.onChanged();
		pileMarkers.clear();
	}

	@Override
	public void pileDataUpdate() {
		if (handler == null) {
			return;
		}
		//		for (Marker marker : pileMarkers.keySet()) {
		//			marker.remove();
		//		}
		baiduMap.clear();
		pileMarkers.clear();
		//		rectangleSearchCondition.setDuration(centerPointSearchCondition.getDuration());
		//		rectangleSearchCondition.setIsIdle(centerPointSearchCondition.getIsIdle());
		//		rectangleSearchCondition.setStartTime(centerPointSearchCondition.getStartTime());
		//		rectangleSearchCondition.setEndTime(centerPointSearchCondition.getEndTime());
		//		rectangleSearchCondition.setPileTypes(centerPointSearchCondition.getPileTypes());
		WebAPIManager.getInstance().getCongruentPoint(MAX_LEVEL, rectangleSearchCondition, handler);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null) return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc && (centerPointSearchCondition.getCityId() == null || centerPointSearchCondition.getCityId().equals(""))) {
				moveMapTo(baiduMap.getLocationData().latitude, baiduMap.getLocationData().longitude, true, ZOOM_LEVE_MID);
				isFirstLoc = false;
			}
			mLocation = location;
		}
	}

	public void saveConfigToSp(String key, String value) {
		sp.addString2Set(key, value);
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(getActivity());
		myOrientationListener.setOnOrientationListener((new OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = x;
				if (baiduMap.getLocationData() != null) {
					// 构造定位数据
					MyLocationData locData = new MyLocationData.Builder()
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(mXDirection).accuracy(baiduMap.getLocationData().accuracy).latitude(baiduMap.getLocationData().latitude).longitude
									(baiduMap.getLocationData().longitude).build();
					// 设置定位数据
					baiduMap.setMyLocationData(locData);
				}
			}
		}));
	}

	public Bitmap getSingleIcon(Pile pile) {
		// 单个桩
		if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
//			if (centerPointSearchCondition.getIsIdle()) {
			if (pile.getGprsType() == Pile.TYPE_BLUETOOTH) {
				return bitMapStrings[1];
			} else {
				return bitMapStrings[0];
			}
//			} else {
//				// 私人桩
//				if (pile.getType() == Pile.TYPE_AC) {
//					// 私人交流
//					if (pile.getShareState() == null || pile.getShareState() == Pile.SHARE_STATUS_NO || pile.getIsIdle() == Pile.UNIDLE) {
//						// 私人交流未分享
//						return bitMapStrings[1];
//					} else {
//						// 私人交流已分享
//						return bitMapStrings[0];
//
//					}
//				} else {
//					// 私人直流
//					if (pile.getShareState() == null || pile.getShareState() == Pile.SHARE_STATUS_NO || pile.getIsIdle() == Pile.UNIDLE) {
//						// 私人直流未分享
//						return bitMapStrings[3];
//
//					} else {
//						// 私人直流已分享
//						return bitMapStrings[2];
//
//					}
//				}
//			}
		} else {
			// 公共桩
			if (pile.getGprsType() == PileStation.TYPE_GPRS_NOT) {
				return bitMapStrings[3];
			} else {
				return bitMapStrings[2];
			}
		}
	}

	public RectangleSearchCondition getRectangleSearchCondition() {
		return rectangleSearchCondition;
	}

	public void setRectangleSearchCondition(RectangleSearchCondition rectangleSearchCondition) {
		this.rectangleSearchCondition = rectangleSearchCondition;
	}

	public CenterPointSearchCondition getCenterPointSearchCondition() {
		return centerPointSearchCondition;
	}

	public void setCenterPointSearchCondition(CenterPointSearchCondition centerPointSearchCondition) {
		this.centerPointSearchCondition = centerPointSearchCondition;
	}

	public ILocationOperater getLocationOperater() {
		return operater;
	}

	public void setLocationOperater(ILocationOperater operater) {
		this.operater = operater;
	}

	public BitmapDescriptor getIconBD(CongruentPoint congruentPoint) {
		int num = congruentPoint.getNum();
		if (num == 1) {
			// Bitmap bmp = getSingleIcon(congruentPoint.getPile());
			return BitmapDescriptorFactory.fromBitmap(getSingleIcon(congruentPoint.getPile()));
		} else {
			// 多个桩
			View markView = LayoutInflater.from(getActivity()).inflate(R.layout.map_marker_congruent, null);
			TextView tv = (TextView) markView.findViewById(R.id.tv);
			// 设置数字
			tv.setText(num + "");
			return BitmapDescriptorFactory.fromView(markView);
		}
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

	//收起
	public void unextendFilterWindow() {
		v_touch.setVisibility(View.GONE);
		if (ibtn_filter.isSelected()) {
			filterWindowAnimator.unextend();
			ibtn_filter.setSelected(false);
		}
//		if (getTargetFragment() != null) {
//			PileFindFragment targetFragment = (PileFindFragment) getTargetFragment();
//			targetFragment.hideBack();
//		}

		if (baiduMap != null && mInfoWindow != null) {
			baiduMap.hideInfoWindow();
			mInfoWindow = null;
		}
		if (pileInfoMapPop.isShowing()) {
			Log.i("isShowing", "isShowing");
			pileInfoMapPop.dismiss();
		}

//		if (getUserVisibleHint()) {
//			pileDataUpdate();
//		}
	}

	//展开
	public void extendFilterWindow() {
		v_touch.setVisibility(View.VISIBLE);
		if (!ibtn_filter.isSelected()) {
			ibtn_filter.setSelected(true);
			filterWindowAnimator.extend();
		}
//		if (getTargetFragment() != null) {
//			PileFindFragment targetFragment = (PileFindFragment) getTargetFragment();
//			targetFragment.showBack();
//		}
		if (baiduMap != null && mInfoWindow != null) {
			baiduMap.hideInfoWindow();
			mInfoWindow = null;
		}
		if (pileInfoMapPop.isShowing()) {
			Log.i("isShowing", "isShowing");
			pileInfoMapPop.dismiss();
		}
	}

	private void recycleRes() {
		// 结束定位
		if (mLocClient != null) mLocClient.stop();
		// 结束方向传感器
		if (myOrientationListener != null) myOrientationListener.stop();
		// 回收图片
		if (bitMapStrings != null) {
			for (int i = 0; i < bitMapStrings.length; i++) {
				if (bitMapStrings[i] != null) {
					BitmapUtil.recycle(bitMapStrings[i]);
					bitMapStrings[i] = null;
				}
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (rl_map != null) rl_map.setVisibility(View.VISIBLE);
		} else {
			if (rl_map != null) rl_map.setVisibility(View.GONE);
			if (pileInfoMapPop != null && pileInfoMapPop.isShowing()) {
				pileInfoMapPop.dismiss();
				// pileInfoMapPop = null ;
			}
			if (baiduMap != null && mInfoWindow != null) {
				baiduMap.hideInfoWindow();
				// mInfoWindow = null;
			}
			//收起
//			if (ibtn_filter.isSelected()) {
//				rl_pile_filter.setVisibility(View.GONE);
//				ibtn_filter.setSelected(false);
//			}
			unextendFilterWindow();
		}
	}

}
