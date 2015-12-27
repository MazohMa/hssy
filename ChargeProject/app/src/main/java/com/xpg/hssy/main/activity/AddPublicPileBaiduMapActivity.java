package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.model.LatLng;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.util.MyOrientationListener;
import com.xpg.hssy.util.UIUtil;
import com.xpg.hssy.util.MyOrientationListener.OnOrientationListener;
import com.xpg.hssychargingpole.R;

/**
 * AddPublicPileBaiduMapActivity
 * @description
 * @author Mazoh
 * @email 471977848@qq.com
 * @create 2015年7月21日
 * @version 2.0.3
 */
public class AddPublicPileBaiduMapActivity extends BaseActivity {
	public static final int zIndex = 15 ; 
	public static final float ZOOM_LEVE_MID = 15;

	private LayoutInflater mInflater;
	private ImageButton btLocation;
	private RelativeLayout rl_map;
	private MapView mapView;
	private BaiduMap baiduMap;
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	private float mXDirection; // 方向传感器X方向的值
	private BDLocation mLocation;
	private static final double BAIDU_LAT_LON_DEFAULT_VALUE = 4.9E-324;
	private MyOrientationListener myOrientationListener;
	private boolean isFirstLoc = true;// 是否首次定位
	private Marker mMarker;
	private BitmapDescriptor mBitmapDescriptor;

	
	public BitmapDescriptor getIconBD() {
		View markView = LayoutInflater.from(this).inflate(
				R.layout.map_marker_view_for_guide, null);
		// 设置数字
		return BitmapDescriptorFactory.fromView(markView);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (baiduMap == null || location == null || mapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc && baiduMap.getLocationData() != null) {
				isFirstLoc = false;
				Log.i("tag", "拖拽结束，新位置：=====");
				OverlayOptions overlayOptions = new MarkerOptions()
						.position(
								new LatLng(location.getLatitude(), location
										.getLongitude()))
						.icon(mBitmapDescriptor).zIndex(zIndex).draggable(true);
				mMarker = (Marker) (baiduMap.addOverlay(overlayOptions));
				moveMapTo(baiduMap.getLocationData().latitude,
						baiduMap.getLocationData().longitude, true,
						ZOOM_LEVE_MID);

			}
			mLocation = location;

		}
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(this);
		myOrientationListener
				.setOnOrientationListener((new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = x;
						if (baiduMap.getLocationData() != null) {
							// 构造定位数据
							MyLocationData locData = new MyLocationData.Builder()
									// 此处设置开发者获取到的方向信息，顺时针0-360
									.direction(mXDirection)
									.accuracy(
											baiduMap.getLocationData().accuracy)
									.latitude(
											baiduMap.getLocationData().latitude)
									.longitude(
											baiduMap.getLocationData().longitude)
									.build();
							// 设置定位数据
							baiduMap.setMyLocationData(locData);
						}
					}
				}));
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param isAnimate
	 * @param zoomLevel
	 *            调整地图的缩放比例
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

	private void moveMapTo(double latitude, double longitude, boolean isAnimate) {
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(
				latitude, longitude));
		if (mMarker != null) {
			mMarker.remove();
		}
		if (baiduMap == null) {
			return;
		}
		if (isAnimate) {
			baiduMap.animateMapStatus(msu);
		} else {
			baiduMap.setMapStatus(msu);
		}
		OverlayOptions ooA = new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.icon(mBitmapDescriptor).zIndex(15).draggable(true);
		mMarker = (Marker) (baiduMap.addOverlay(ooA));

	}

	@Override
	protected void initData() {
		super.initData();
		mInflater = LayoutInflater.from(this);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btLocation.setOnClickListener(this);
		baiduMap.setMyLocationEnabled(true);
		// 开启定位
		mLocClient.start();
		// 开启方向传感器
		myOrientationListener.start();
		baiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
			}

			public void onMarkerDragEnd(Marker marker) {
				Toast.makeText(
						AddPublicPileBaiduMapActivity.this,
						"拖拽结束，新位置：" + marker.getPosition().latitude + ", "
								+ marker.getPosition().longitude,
						Toast.LENGTH_SHORT).show();
			}

			public void onMarkerDragStart(Marker marker) {
			}
		});
		mBitmapDescriptor = getIconBD();
	}

	@Override
	protected void initUI() {
		super.initUI();
		View v = mInflater.inflate(R.layout.add_public_pile_baidumap_layout,
				null);
		setContentView(v);
		UIUtil.hideSoftInput(this, v);
		setTitle("设置位置");
		btLocation = (ImageButton) v.findViewById(R.id.btn_Location);
		rl_map = (RelativeLayout) v.findViewById(R.id.rl_map);
		// 创建百度地图
		BaiduMapOptions mapOptions = new BaiduMapOptions()
				.zoomControlsEnabled(false).scaleControlEnabled(false)
				.compassEnabled(false).overlookingGesturesEnabled(false);
		mapView = new MapView(this, mapOptions);
		rl_map.addView(mapView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		baiduMap = mapView.getMap();

		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);

		initOritationListener();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mapView.onResume();
		super.onResume();

	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onPause()
		mapView.onResume();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		getIconBD().recycle();
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onLeftBtn(View v) {
		super.onLeftBtn(v);
		finish();
	}

	@Override
	protected void onRightBtn(View v) {
		super.onRightBtn(v);
		Intent intent = new Intent(this, AddPublicPileForInfoActivity.class);
		startActivityForResult(intent, 12);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_Location: {
			if (baiduMap == null
					|| baiduMap.getLocationData() == null
					|| baiduMap.getLocationData().latitude == BAIDU_LAT_LON_DEFAULT_VALUE
					|| baiduMap.getLocationData().longitude == BAIDU_LAT_LON_DEFAULT_VALUE) {
				ToastUtil.show(this, "无法定位，请打开无线网络或GPS");
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

}
