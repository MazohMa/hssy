package com.xpg.hssy.main.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bean.searchconditon.BaseSearchCondition;
import com.xpg.hssy.bean.searchconditon.CenterPointSearchCondition;
import com.xpg.hssy.bean.searchconditon.RectangleSearchCondition;
import com.xpg.hssy.bean.searchconditon.SearchCondition;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.engine.LocationInfos.LocationInfo;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssy.main.activity.SearchPileAndAddressActivity;
import com.xpg.hssy.main.activity.SelectCityActivity;
import com.xpg.hssy.main.fragment.PileFindMapCongruentPointFragment.ChangeLeftBtnListener;
import com.xpg.hssy.main.fragment.callbackinterface.ILocationOperater;
import com.xpg.hssy.popwindow.PileInfoMapPop;
import com.xpg.hssychargingpole.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年3月26日
 */

public class PileFindFragment extends BaseFragment implements OnClickListener, ILocationOperater, OnGetGeoCoderResultListener {

	public static final int REQUEST_CITY = 0x21;
	public static final int REQUEST_SEARCH = 0x22;
	public static final int REQUEST_TIME = 0x23;
	public static final int REQUEST_REFRESH = 0x24;

	public static final double LAT_LON_DEFAULT_VALUE = -1;
	public static final long DEFAULT_DISTANCE = (long) (6374000l * 3.14 / 2);// 地球表面两点间最长的球面距离
	// 三个searchCondition对象,基本,方形,中心点搜索
	private BaseSearchCondition searchCondition;
	private CenterPointSearchCondition centerPointSearchCondition;
	private RectangleSearchCondition rectanglePointSearchCondition;

	private ImageButton btn_right;
	private ImageButton btn_left;
	private TextView tv_left;
	private TextView tv_search;

	private PileFindListFragment listFragment;
	private PileFindMapCongruentPointFragment mapFragment;
	private Fragment currentFragment;

	private SharedPreferences sp;
	private int status = 0;
	private PileInfoMapPop pileInfoMapPop;
	private LocationInfo city;
	private Timer networkTimer;
	private LoadingDialog loadingDialog = null;

	public PileFindFragment() {
		super();
		init();
	}

	private void init() {
		// 初始化数据
		searchCondition = new BaseSearchCondition();
		centerPointSearchCondition = new CenterPointSearchCondition(searchCondition);
		rectanglePointSearchCondition = new RectangleSearchCondition(searchCondition);

		//TODO 默认只显示公共站,显示包括不在线的
		searchCondition.addPileTypes(SearchCondition.PUBLIC);
		searchCondition.setIsIdle(false);
		centerPointSearchCondition.setDistance(DEFAULT_DISTANCE);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		sp = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	/**
	 * 创建UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// 初始化UI
		View v = inflater.inflate(R.layout.fragment_pile_find, null);
		btn_right = (ImageButton) v.findViewById(R.id.btn_right);
		btn_left = (ImageButton) v.findViewById(R.id.btn_left);
		btn_left.setVisibility(View.GONE);
		tv_left = (TextView) v.findViewById(R.id.tv_left);
		tv_search = (TextView) v.findViewById(R.id.tv_search);
		List<Fragment> fragments = getChildFragmentManager().getFragments();
		Log.d("fragment", "check the fragment");
		if (fragments != null) {
			for (Fragment fragment : fragments) {
				if (fragment instanceof PileFindMapCongruentPointFragment) {
					Log.d("fragment", "get the map fragment");
					Log.d("fragment", "new list fragment");

					mapFragment = (PileFindMapCongruentPointFragment) fragments;
					listFragment = new PileFindListFragment();
				} else {
					Log.d("fragment", "get the list fragment");
					Log.d("fragment", "new map fragment");

					listFragment = (PileFindListFragment) fragments;
					mapFragment = new PileFindMapCongruentPointFragment();
				}
			}
		} else {
			Log.d("fragment", "new all fragment");
			listFragment = new PileFindListFragment();
			mapFragment = new PileFindMapCongruentPointFragment();
		}

		listFragment.setTargetFragment(this, 0);
		mapFragment.setTargetFragment(this, 0);
		listFragment.setLocationOperater(this);
		mapFragment.setLocationOperater(this);
		listFragment.setSearchCondition(centerPointSearchCondition);
		mapFragment.setCenterPointSearchCondition(centerPointSearchCondition);
		mapFragment.setRectangleSearchCondition(rectanglePointSearchCondition);
		// 初始化交互事件
		btn_right.setOnClickListener(this);
		btn_left.setOnClickListener(this);
		tv_left.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		// TODO fix bug by Mazoh 2015 07 01 show or hide left button and tv_left
		// when PileMapPop
		// in pileFindCongruentPointFragment showed
		mapFragment.setChangeLeftBtnListener(new ChangeLeftBtnListener() {

			@Override
			public void changeLeftBtn(PileInfoMapPop pileInfoMapPop, int status) {
				PileFindFragment.this.pileInfoMapPop = pileInfoMapPop;
				PileFindFragment.this.status = status;
				if (status == 0) {
					showBack();
				} else {
					hideBack();
				}
			}
		});
		// TODO end
		// 默认地图模式
//		change2MapMode();
		//列表模式
		if (currentFragment == null) {
			change2ListMode();
			startLbs();
		} else if (currentFragment instanceof PileFindMapCongruentPointFragment) {
			if (searchCondition.getCityId() == null) {
				tv_left.setText("城市名");
				startLbs();
			} else {
				LocationInfo locationInfo = LocationInfos.getInstance().getCityById(searchCondition.getCityId());
				if (locationInfo != null) {
					tv_left.setText(locationInfo.getName());
				} else {
					tv_left.setText("城市名");
				}
			}
			change2MapMode();
		} else {
			if (searchCondition.getCityId() == null) {
				tv_left.setText("城市名");
				startLbs();
			} else {
				LocationInfo locationInfo = LocationInfos.getInstance().getCityById(searchCondition.getCityId());
				if (locationInfo != null) {
					tv_left.setText(locationInfo.getName());
				} else {
					tv_left.setText("城市名");
				}
			}
			change2ListMode();
		}
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

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left: {
				if (pileInfoMapPop != null) {
					disMapPop(pileInfoMapPop);
				}
				flodFilterWindow();
				break;
			}
			case R.id.btn_right: {// 切换列表和地图模式
				if (pileInfoMapPop != null) {
					disMapPop(pileInfoMapPop);
				}
				currentFragment.setUserVisibleHint(false);
				flodFilterWindow();
				if (currentFragment == listFragment) {
					change2MapMode();
				} else if (currentFragment == mapFragment) {
					change2ListMode();
				}
				new android.os.Handler().post(new Runnable() {
					@Override
					public void run() {
						currentFragment.setUserVisibleHint(true);
						if (currentFragment == mapFragment) {
							mapFragment.onChanged();
						}
					}
				});

				btn_right.setEnabled(false);
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						if (getActivity() != null) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									btn_right.setEnabled(true);
								}
							});
						}
					}
				}, 1500);
				break;
			}
			case R.id.tv_left: {
				flodFilterWindow();
				selectCity();
				break;
			}
			case R.id.tv_search: {
				flodFilterWindow();
				goToSearch();
				break;
			}
			default:

				break;
		}
	}

	private void disMapPop(PileInfoMapPop pileInfoMapPop) {
		if (pileInfoMapPop.isShowing()) {
			pileInfoMapPop.dismiss();
		}
	}

	/**
	 * 切换至列表模式
	 */
	private void change2ListMode() {
		btn_left.setVisibility(View.GONE);
		tv_left.setVisibility(View.VISIBLE);
		tv_search.setText(getResources().getString(R.string.search_input));
		btn_right.setImageResource(R.drawable.icon_tab_map);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.ll_content, listFragment);
		transaction.commit();
		currentFragment = listFragment;
	}

	/**
	 * 切换至地图模式
	 */
	private void change2MapMode() {
		tv_search.setText(getResources().getString(R.string.search_input));
		btn_right.setImageResource(R.drawable.list_bt);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.ll_content, mapFragment);
		transaction.commit();
		currentFragment = mapFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (currentFragment == null && btn_right != null) {
			try {
				change2MapMode();
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * 从其他页面返回结果
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 处理从桩的详细页面跳转到地图
		switch (requestCode) {
			case MainActivity.REQUEST_MAP: {
				change2MapMode();
				break;
			}
			case REQUEST_CITY: {
				if (data != null) {
					String cityId = data.getStringExtra(KEY.INTENT.CITY_ID);
					if (cityId != null && !cityId.equals("")) {
						if (networkTimer != null) {
							networkTimer.cancel();
							networkTimer = null;
						}
						networkTimer = new Timer();
						city = LocationInfos.getInstance().getCityById(cityId);
						LbsManager.getInstance().getLocationByAddress(city.getName(), city.getName(), this);
						currentFragment.onActivityResult(requestCode, resultCode, data);
						loadingDialog = new LoadingDialog(getActivity(), R.string.loading);
						loadingDialog.showDialog();
						networkTimer.schedule(new TimerTask() {
							@Override
							public void run() {
								getActivity().runOnUiThread(new Runnable() {

									@Override
									public void run() {
										loadingDialog.dismiss();
										ToastUtil.show(getActivity(), R.string.network_over_time);
									}
								});
							}

						}, 10000);
					}
					break;
				}
			}
			case REQUEST_SEARCH: {
				if (data != null) {
					String cityName = data.getStringExtra(KEY.INTENT.CITY);
					String address = data.getStringExtra(KEY.INTENT.ADDRESS);
					double latitude = data.getDoubleExtra(KEY.INTENT.LATITUDE, -1);
					double longitude = data.getDoubleExtra(KEY.INTENT.LONGITUDE, -1);
					city = LocationInfos.getInstance().getCityByName(cityName);
					searchCondition.setCityId(city.getCode());
					searchCondition.setAddress(address);
					centerPointSearchCondition.setCenterLatitude(latitude);
					centerPointSearchCondition.setCenterLongitude(longitude);
					currentFragment.onActivityResult(requestCode, resultCode, data);
					saveSearchConditionToSP();
					break;
				}
			}
			case REQUEST_TIME: {
				currentFragment.onActivityResult(requestCode, resultCode, data);
				break;
			}
			default: {
				if (currentFragment != null) {
					currentFragment.onActivityResult(requestCode, resultCode, data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 分发Visible事件
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (currentFragment != null) {
			currentFragment.setUserVisibleHint(isVisibleToUser);
		}
		if (isVisibleToUser == false) {
			if (pileInfoMapPop != null) {
				disMapPop(pileInfoMapPop);
			}
		}
	}

	private void selectCity() {
		Intent intent = new Intent(getActivity(), SelectCityActivity.class);
		getActivity().startActivityForResult(intent, REQUEST_CITY);
	}

	private void goToSearch() {
		if (searchCondition.getCityId() == null) {
			ToastUtil.show(getActivity(), getResources().getString(R.string.please_select_city));
			return;
		}
//         startLbs();
		Intent intent = new Intent(getActivity(), SearchPileAndAddressActivity.class);
		if (centerPointSearchCondition.getCityId() != null) {
			LocationInfo mCity = LocationInfos.getInstance().getCityById(centerPointSearchCondition.getCityId());
			intent.putExtra(KEY.INTENT.CITY, mCity.getName());
			intent.putExtra(KEY.INTENT.LATITUDE, centerPointSearchCondition.getCenterLatitude());
			intent.putExtra(KEY.INTENT.LONGITUDE, centerPointSearchCondition.getCenterLongitude());
			getActivity().startActivityForResult(intent, REQUEST_SEARCH);
		} else {
			ToastUtil.show(getActivity(), R.string.location_fail);
		}
	}

	@Override
	public void setCity(String city) {
		if (this.tv_left != null) {
			this.tv_left.setText(city);
		}
	}

	@Override
	public void setAddress(String address) {
		tv_search.setText(address);
	}

	/**
	 * 定位
	 */
	private void startLbs() {

		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String proStr = location.getProvince();// 省份,目前没有用上
				String cityStr = location.getCity();
				String addressStr = "";
				if (cityStr != null) {
					if (location.getDistrict() != null) {
						addressStr += location.getDistrict();
						if (location.getStreet() != null) {
						}
						addressStr += location.getStreet();
					}
					if (location.getStreetNumber() != null) {
						addressStr += location.getStreetNumber();
					}
					setCity(cityStr);
					city = LocationInfos.getInstance().getCityByName(proStr, cityStr);
					if (city == null) {
						searchCondition.setCityId(null);
					} else {
						searchCondition.setCityId(city.getCode());
					}
					searchCondition.setAddress(addressStr);

					centerPointSearchCondition.setCenterLatitude(location.getLatitude());
					centerPointSearchCondition.setCenterLongitude(location.getLongitude());
					// save in sharepreference
					saveSearchConditionToSP();
					//保存自己的位置信息
					saveConfigToSp(KEY.CONFIG.MY_CITY_ID, city.getCode());
					saveConfigToSp(KEY.CONFIG.MY_LONGITUDE, String.valueOf(location.getLongitude()));
					saveConfigToSp(KEY.CONFIG.MY_LATITUDE, String.valueOf(location.getLatitude()));
					if (getActivity() != null && mapFragment != null && currentFragment == mapFragment && mapFragment.isAdded()) {
						mapFragment.initMapFocus();
					} else if (listFragment != null && currentFragment == listFragment && listFragment.isAdded()) {
						listFragment.loadPiles();
					}
				} else {
					if (getActivity() != null) {
						ToastUtil.show(getActivity(), "定位失败");
					}
				}
			}

		});
	}

	private void saveSearchConditionToSP() {
		Editor editor = sp.edit();
		editor.putString(KEY.CONFIG.CITY_ID, searchCondition.getCityId());
		editor.putString(KEY.CONFIG.ADDRESS, searchCondition.getAddress());
		editor.putString(KEY.CONFIG.LATITUDE, String.valueOf(centerPointSearchCondition.getCenterLatitude()));
		editor.putString(KEY.CONFIG.LONGITUDE, String.valueOf(centerPointSearchCondition.getCenterLongitude()));
		editor.commit();
	}

	public void saveConfigToSp(String key, String value) {
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void showBack() {
		btn_left.setVisibility(View.VISIBLE);
		tv_left.setVisibility(View.GONE);
	}

	public void hideBack() {
		btn_left.setVisibility(View.GONE);
		tv_left.setVisibility(View.VISIBLE);
	}

	public void flodFilterWindow() {
		if (currentFragment == mapFragment) {
			mapFragment.unextendFilterWindow();
		} else if (currentFragment == listFragment) {
			listFragment.flodFilterAndClearCheck();
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (networkTimer != null) {
			networkTimer.cancel();
		}
		if (getActivity() == null) {
			return;
		}
		loadingDialog.dismiss();
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtil.show(getActivity(), "切换失败，请重试");
			return;
		}
		if (result.getLocation().latitude <= 0 && result.getLocation().longitude <= 0) {
			ToastUtil.show(getActivity(), "切换失败，请重试");
			return;
		}
		setCity(city.getName());
		searchCondition.setCityId(city.getCode());
		searchCondition.setAddress(city.getName());
		centerPointSearchCondition.setCenterLatitude(result.getLocation().latitude);
		centerPointSearchCondition.setCenterLongitude(result.getLocation().longitude);
		centerPointSearchCondition.setZoom(PileFindMapCongruentPointFragment.ZOOM_LEVE_CITY);
		((OnGetGeoCoderResultListener) currentFragment).onGetGeoCodeResult(result);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}
}
