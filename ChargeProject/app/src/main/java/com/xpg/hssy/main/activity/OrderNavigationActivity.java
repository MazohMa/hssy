package com.xpg.hssy.main.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.easy.util.BitmapUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.dialog.WaterBlueDialogComfirmNav;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.util.MyOrientationListener;
import com.xpg.hssy.util.MyOrientationListener.OnOrientationListener;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

public class OrderNavigationActivity extends BaseActivity {

	private MapView mapView;
	private BaiduMap baiduMap;
	private RelativeLayout rl_map, rl_map_pole_info;
	private TextView tv_map_pile_name;
	private TextView tv_map_pile_position;
	private TextView tv_map_pile_distance;
	private double mLatitude, mLongitude;
	private String mPileId;
	private Pile mPile;
	private BitmapDescriptor icon;
	private BDLocation mLocation;
	private Button mNavigation;

	private MyOrientationListener myOrientationListener;
	protected float mXDirection;
    private LoadingDialog loadingDialog = null;
	private int[] drawIcons = new int[] { R.drawable.icon_personal_ac,
			R.drawable.icon_personal_unfree, R.drawable.icon_personal_dc,
			R.drawable.icon_personal_unfree, R.drawable.icon_station };

	public static Bitmap[] bitMapStrings = new Bitmap[5];
	@SuppressLint("HandlerLeak")
	private Handler notifyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showMapPileInfo();
		}
	};

	@Override
	protected void onLeftBtn(View v) {
		finish();
		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
	}

	private void getLocation() {
		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String cityStr = location.getCity();
				if (cityStr != null) {
					mLocation = location;
					// map view 销毁后不在处理新接收的位置
					if (location == null || mapView == null)
						return;
					MyLocationData locData = new MyLocationData.Builder()
							.accuracy(location.getRadius())
							.latitude(location.getLatitude())
							.longitude(location.getLongitude()).build();
					baiduMap.setMyLocationData(locData);
					notifyHandler.sendEmptyMessage(0);
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initData() {
		mLatitude = getIntent().getDoubleExtra("latitude", 0);
		mLongitude = getIntent().getDoubleExtra("longitude", 0);
		mPileId = getIntent().getStringExtra("pileId");
		getPileData();

		if (bitMapStrings[0] == null) {
			for (int i = 0; i < bitMapStrings.length; i++) {
				bitMapStrings[i] = BitmapUtil.get(this, drawIcons[i]);
			}
		}

		// 创建百度地图
		BaiduMapOptions mapOptions = new BaiduMapOptions()
				.zoomControlsEnabled(false).scaleControlEnabled(false)
				.compassEnabled(false).overlookingGesturesEnabled(false);
		mapView = new MapView(this, mapOptions);
		baiduMap = mapView.getMap();
	}

	@Override
	public void initUI() {
		setContentView(R.layout.ordernavigation);
		rl_map = (RelativeLayout) findViewById(R.id.order_navigation);
		rl_map_pole_info = (RelativeLayout) findViewById(R.id.rl_map_pole_info);
		tv_map_pile_name = (TextView) findViewById(R.id.tv_map_pole_name);
		tv_map_pile_position = (TextView) findViewById(R.id.tv_map_pole_position);
		tv_map_pile_distance = (TextView) findViewById(R.id.tv_map_pole_distance);
		mNavigation = (Button) findViewById(R.id.btn_navigation);
		rl_map.addView(mapView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		baiduMap.setMyLocationEnabled(true);
		baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
		initOritationListener();
	}

	@Override
	public void initEvent() {
		super.initEvent();
		mNavigation.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_navigation:
			if ((mLocation != null) && (mPile != null)) {
				callNavigation();
			}
			break;
		default:
			break;
		}
	}

	// 调百度地图导航;
	private void callNavigation() {

		WaterBlueDialogComfirmNav waterBlueDialogComfirmNav = new WaterBlueDialogComfirmNav(
				this);
		waterBlueDialogComfirmNav.show(mLocation.getLatitude(),
				mLocation.getLongitude(), mPile.getLatitude(),
				mPile.getLongitude());
	}

	private void moveMapTo(double latitude, double longitude, boolean isAnimate) {
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(
				latitude, longitude));
		if (isAnimate) {
			baiduMap.animateMapStatus(msu);
		} else {
			baiduMap.setMapStatus(msu);
		}
	}

	private void getPileData() {
		loadingDialog = new LoadingDialog(self,R.string.please_wait) ;
		loadingDialog.showDialog();
		WebAPIManager.getInstance().getPileById(mPileId,
				new WebResponseHandler<Pile>(this) {
					@Override
					public void onError(Throwable e) {
						super.onError(e);
						TipsUtil.showTips(self, e);
					}

					@Override
					public void onFailure(WebResponse<Pile> response) {
						super.onFailure(response);
						TipsUtil.showTips(self, response);
					}

					@Override
					public void onSuccess(WebResponse<Pile> response) {
						super.onSuccess(response);
						mPile = response.getResultObj();
						icon = BitmapDescriptorFactory
								.fromBitmap(getSingleIcon(mPile));
						moveMapTo(mLatitude, mLongitude, true);
						baiduMap.addOverlay(new MarkerOptions()
								.position(new LatLng(mLatitude, mLongitude))
								.icon(icon).anchor(0.5f, 0.5f));
						getLocation();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						loadingDialog.dismiss();
					}
				});

	}

	private void showMapPileInfo() {
		if ((mLocation != null) && (mPile != null)) {
			rl_map_pole_info.setVisibility(View.VISIBLE);
			LatLng sLatLng = new LatLng(mLocation.getLatitude(),
					mLocation.getLongitude());
			LatLng eLatLng = new LatLng(mPile.getLatitude(),
					mPile.getLongitude());
			tv_map_pile_name.setText(mPile.getPileName());
			tv_map_pile_position.setText(mPile.getLocation());
			double distance = DistanceUtil.getDistance(sLatLng, eLatLng) / 1000;
			tv_map_pile_distance.setText(String.format("%1$.1f", distance)
					+ "公里");
		} else {
			rl_map_pole_info.setVisibility(View.GONE);
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

	private Bitmap getSingleIcon(Pile pile) {
		// 单个桩
		if (pile.getOperator() == Pile.OPERATOR_PERSONAL) {
			// 私人桩
			if (pile.getType() == Pile.TYPE_AC) {
				// 私人交流
				if (pile.getShareState() == null
						|| pile.getShareState() == Pile.SHARE_STATUS_NO) {
					// 私人交流未分享
					return bitMapStrings[1];
				} else {
					// 私人交流已分享
					return bitMapStrings[0];

				}
			} else {
				// 私人直流
				if (pile.getShareState() == null
						|| pile.getShareState() == Pile.SHARE_STATUS_NO) {
					// 私人直流未分享
					return bitMapStrings[3];

				} else {
					// 私人直流已分享
					return bitMapStrings[2];

				}
			}
		} else {
			// 公共桩
			return bitMapStrings[4];

		}
	}

	private void hideMapPoleInfo() {
		rl_map_pole_info.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		myOrientationListener.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		myOrientationListener.stop();
	}

	private void recycleRes() {
		if (baiduMap != null) {
			baiduMap.setMyLocationEnabled(false);
		}
		// 结束方向传感器
		myOrientationListener.stop();
		// 回收图片
		for (int i = 0; i < bitMapStrings.length; i++) {
			if (bitMapStrings[i] != null) {
				BitmapUtil.recycle(bitMapStrings[i]);
				bitMapStrings[i] = null;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		recycleRes();
	}
}
