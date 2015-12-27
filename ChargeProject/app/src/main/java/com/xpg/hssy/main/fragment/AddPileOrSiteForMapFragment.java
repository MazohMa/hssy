package com.xpg.hssy.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easy.util.ToastUtil;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.main.fragment.callbackinterface.GeoListenerOperater;
import com.xpg.hssy.util.MyOrientationListener;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 * @version 2.4.0
 * @description
 */

public class AddPileOrSiteForMapFragment extends Fragment implements View.OnClickListener, OnGetGeoCoderResultListener, GeoListenerOperater {
	private ImageButton btLocation;
	private RelativeLayout rl_map;
	private MapView mapView;
	private BaiduMap baiduMap;
	private LocationClient mLocClient;
	private MyLocationListenner myListener;
	private MyOrientationListener myOrientationListener;
	private static final double BAIDU_LAT_LON_DEFAULT_VALUE = 4.9E-324;
	private Marker mMarker;
	private BitmapDescriptor mBitmapDescriptor;
	private boolean isFirstLoc = true;// 是否首次定位
	private TextView tv_location_message;
	private Double longitude = null;
	private Double latitude = null;
	private float mXDirection; // 方向传感器X方向的值
	private SharedPreferences sp;
	private GeoListenerOperater geoListenerOperater;
	private boolean isFromMapInfo = false;

	public GeoListenerOperater getGeoListenerOperater() {
		return geoListenerOperater;
	}

	public void setGeoListenerOperater(GeoListenerOperater geoListenerOperater) {
		this.geoListenerOperater = geoListenerOperater;
	}

	public AddPileOrSiteForMapFragment() {
		super();
	}


	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			LbsManager.getInstance().getAddressByLocation(new LatLng(result.getLocation()
					.latitude, result.getLocation().longitude), this);
		}
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
			if (result.getAddressDetail() != null) {
				tv_location_message.setText(result.getAddress());
				if (geoListenerOperater != null) {
					geoListenerOperater.onListener(longitude, latitude, result.getAddress());
				}
			}
		}
	}

	/**
	 * @param latitude
	 * @param longitude
	 * @param isAnimate
	 * @param zoomLevel 调整地图的缩放比例
	 */
	private void moveMapTo(double latitude, double longitude,
	                       boolean isAnimate, float zoomLevel) {
		MapStatus mMapStatus = new MapStatus.Builder()
				.target(new LatLng(latitude, longitude)).zoom(zoomLevel)
				.build();
		MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		if (isAnimate) {
			// 设置中心点,移动到中心点
			baiduMap.animateMapStatus(msu);
		} else {
			baiduMap.setMapStatus(msu);
		}
	}

	public void moveMapTo(double latitude, double longitude, boolean isAnimate) {
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(
				latitude, longitude));
		if (mMarker != null) {
			mMarker.remove();
		}
		if (baiduMap == null) {
			Log.i("tag", "baiduMap == null");
			return;
		}
		if (isAnimate) {
			baiduMap.animateMapStatus(msu);
		} else {
			baiduMap.setMapStatus(msu);
		}
//		OverlayOptions ooA = new MarkerOptions()
//				.position(new LatLng(latitude, longitude))
//				.icon(mBitmapDescriptor).zIndex(15);
//		mMarker = (Marker) (baiduMap.addOverlay(ooA));
		LbsManager.getInstance().getAddressByLocation(baiduMap.getMapStatus().target,
				AddPileOrSiteForMapFragment.this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public BitmapDescriptor getIconBD() {
		View markView = LayoutInflater.from(getActivity()).inflate(
				R.layout.map_marker_view_for_guide, null);
		// 设置数字
		return BitmapDescriptorFactory.fromView(markView);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_Location: {
				if (baiduMap == null
						|| baiduMap.getLocationData() == null
						|| baiduMap.getLocationData().latitude == BAIDU_LAT_LON_DEFAULT_VALUE
						|| baiduMap.getLocationData().longitude == BAIDU_LAT_LON_DEFAULT_VALUE) {
					ToastUtil.show(getActivity(), "无法定位，请打开无线网络或GPS");
					return;
				}
				moveMapTo(baiduMap.getLocationData().latitude,
						baiduMap.getLocationData().longitude, true);
				break;
			}
			default:
				break;
		}
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(getActivity());
		myOrientationListener.setOnOrientationListener((new MyOrientationListener.OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mXDirection = x;
				if (baiduMap.getLocationData() != null) {
					// 构造定位数据
					MyLocationData locData = new MyLocationData.Builder()
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(mXDirection).accuracy(baiduMap.getLocationData().accuracy)
							.latitude(baiduMap.getLocationData().latitude).longitude(baiduMap
									.getLocationData().longitude).build();
					// 设置定位数据
					baiduMap.setMyLocationData(locData);
				}
			}
		}));
	}

	@Override
	public void onListener(double longitude, double latitude, String address) {
		Log.i("longitude onListener", longitude + "");
		Log.i("latitude onListener", latitude + "");
		isFromMapInfo = true;
		this.longitude = longitude;
		this.latitude = latitude;
		if (this.longitude != null && this.latitude != null && isFromMapInfo == true) {
			moveMapTo(latitude, longitude, true);
		}
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
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location
							.getLongitude()).build();
			if (locData != null) {
				baiduMap.setMyLocationData(locData);
				if (isFirstLoc) {
					moveMapTo(baiduMap.getLocationData().latitude, baiduMap.getLocationData()
							.longitude, true);
					isFirstLoc = false;
					longitude = location.getLongitude();
					latitude = location.getLatitude();
					Log.i("longitude:", "longitude:" + longitude + "");
					Log.i("latitude:", "latitude:" + latitude + "");
					LbsManager.getInstance().getAddressByLocation(baiduMap.getMapStatus().target,
							AddPileOrSiteForMapFragment.this);

				}
			}
		}
	}

	/**
	 * 创建UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 初始化UI
		Log.i("onCreateView", "onCreateView");
		View v = inflater.inflate(R.layout.add_public_pile_baidumap_layout, null);
		mBitmapDescriptor = getIconBD();
		btLocation = (ImageButton) v.findViewById(R.id.btn_Location);
		rl_map = (RelativeLayout) v.findViewById(R.id.rl_map);
		tv_location_message = (TextView) v.findViewById(R.id.tv_location_message);
		// 创建百度地图
		BaiduMapOptions mapOptions = new BaiduMapOptions()
				.zoomControlsEnabled(false).scaleControlEnabled(false)
				.compassEnabled(false).overlookingGesturesEnabled(false);
		mapView = new MapView(getActivity(), mapOptions);
		rl_map.addView(mapView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		baiduMap = mapView.getMap();
		myListener = new MyLocationListenner();
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
		btLocation.setOnClickListener(this);
		baiduMap.setMyLocationEnabled(true);
		initOritationListener();
		myOrientationListener.start();
		baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
				if (baiduMap.getProjection() == null) {
					return;
				}
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				if (baiduMap.getProjection() == null) {
					return;
				}
				longitude = arg0.target.longitude;
				latitude = arg0.target.latitude;
				Log.i("longitude:", "longitude:" + longitude + "");
				Log.i("latitude:", "latitude:" + latitude + "");
				LbsManager.getInstance().getAddressByLocation(arg0.target,
						AddPileOrSiteForMapFragment.this);
				Log.d("endXY", arg0.targetScreen.x + " " + arg0.targetScreen.y);
			}

			@Override
			public void onMapStatusChange(MapStatus arg0) {

			}
		});
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("onResume", "onResume");
		if (latitude != null && longitude != null && isFromMapInfo == true) {
			moveMapTo(latitude, longitude, true);
		}

	}

	/**
	 * 从其他页面返回结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void recycleRes() {
		if (baiduMap != null) {
			baiduMap.setMyLocationEnabled(false);
		}
		// 结束定位
		mLocClient.stop();
		// 结束方向传感器
		myOrientationListener.stop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		recycleRes();
		System.gc();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recycleRes();
		System.gc();
	}
}