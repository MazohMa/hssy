package com.xpg.hssy.main.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.easy.util.SPFile;
import com.easy.util.ToastUtil;
import com.hb.views.PinnedSectionListView;
import com.xpg.hssy.adapter.CityListAdapter;
import com.xpg.hssy.adapter.CityListAdapter.CityItem;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.DistrictData;
import com.xpg.hssy.engine.LbsManager;
import com.xpg.hssy.engine.LocationInfos;
import com.xpg.hssy.engine.LocationInfos.LocationInfo;
import com.xpg.hssy.main.activity.callbackinterface.ISelectCityOperator;
import com.xpg.hssy.util.TextViewDrawableClickUtil;
import com.xpg.hssy.view.SideBarView;
import com.xpg.hssy.view.SideBarView.barItemClickListener;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Black Jack
 * @createDate 2015年7月28日
 * @version 1.0.0
 */

public class SelectCityActivity extends BaseActivity implements
		barItemClickListener, ISelectCityOperator {

	private EditText et_search;
	private PinnedSectionListView pslv_citys;
	private SideBarView ll_sections_side_bar;

	private CityListAdapter<SelectCityActivity> cityListAdapter;

	private CityItem locationItem;
	private SPFile sp;

	@Override
	protected void initData() {
		sp = new SPFile(this, "config");
		boolean cityLoaded = sp.getBoolean(KEY.CONFIG.CITY_LOADED, false);
		if (!cityLoaded) {
			ToastUtil.show(this,
					getResources().getString(R.string.city_no_load_finish));
			finish();
		}
		super.initData();
		// array装进String数组适配器
		String[] cityNameList = getResources().getStringArray(
				R.array.hot_cities);
		List<LocationInfo> locationInfoList = new ArrayList<LocationInfo>();
		// 遍历数组，取出字段，添加进locationInfoList集合
		for (String cityName : cityNameList) {
			locationInfoList.add(LocationInfos.getInstance().getCityByName(
					cityName));
		}
		locationItem = new CityItem(CityItem.ITEM_TYPE_LOCATION);
		DistrictData cityData = new DistrictData();
		cityData.setDistrictName(getResources().getString(R.string.lbsing));
		locationItem.setCitys(cityData);
		cityListAdapter = new CityListAdapter<SelectCityActivity>(this);
		initCityListAdapter();
		startLbs();
	}

	private void initCityListAdapter() {
		List<CityItem> cityItems = new ArrayList<>();
		CityItem locatinSection = new CityItem(CityItem.ITEM_TYPE_SECTION);
		locatinSection.setSection(getResources().getString(R.string.lbs_city));
		cityItems.add(locatinSection);
		cityItems.add(locationItem);
		cityItems.addAll(getHotCityItems());
		cityItems.addAll(getCityItemsWithSection());
		cityListAdapter.add(cityItems);
	}

	private List<CityItem> getCityItemsBysearchArg(String arg) {
		List<DistrictData> citys = DbHelper.getInstance(this)
				.getDistrictDataListByArg(arg);
		List<CityItem> items = new ArrayList<>(citys.size());
		for (DistrictData districtData : citys) {
			Log.e("city", districtData.getDistrictPinyin());
			CityItem item = new CityItem(CityItem.ITEM_TYPE_CITY);
			item.setCitys(districtData);
			String section = districtData.getDistrictFirstPinyin().substring(0,
					1);
			item.setSection(section);
			items.add(item);
		}
		return items;
	}

	private List<CityItem> getCityItemsWithSection() {
		List<DistrictData> citys = DbHelper.getInstance(self)
				.getAllDistrictDataList();
		Map<String, List<CityItem>> itemsMapBySection = new TreeMap<>();
		for (DistrictData districtData : citys) {
			CityItem item = new CityItem(CityItem.ITEM_TYPE_CITY);
			item.setCitys(districtData);
			String section = districtData.getDistrictFirstPinyin().substring(0,
					1);
			item.setSection(section);
			List<CityItem> items = itemsMapBySection.get(section);
			if (items == null) {
				items = new ArrayList<>();
				CityItem sectionItem = new CityItem(CityItem.ITEM_TYPE_SECTION);
				sectionItem.setSection(section);
				items.add(sectionItem);
				itemsMapBySection.put(section, items);
			}
			items.add(item);
		}
		Iterator<List<CityItem>> ItemListItr = itemsMapBySection.values()
				.iterator();
		List<CityItem> cityItems = new ArrayList<>(citys.size());
		while (ItemListItr.hasNext()) {
			cityItems.addAll(ItemListItr.next());
		}
		return cityItems;
	}

	private List<CityItem> getHotCityItems() {
		// array装进String数组适配器
		String[] cityNameList = getResources().getStringArray(
				R.array.hot_cities);
		List<DistrictData> hotCitysData = new ArrayList<DistrictData>();
		// 遍历数组，取出字段，添加进locationInfoList集合
		List<CityItem> cityItems = new ArrayList<>();
		CityItem section = new CityItem(CityItem.ITEM_TYPE_SECTION);
		section.setSection("热门城市");
		cityItems.add(section);
		CityItem hotCitys = new CityItem(CityItem.ITEM_TYPE_HOT_CITY);
		for (String cityName : cityNameList) {
			DistrictData cityData = new DistrictData();
			cityData.setByLocationInfo(LocationInfos.getInstance()
					.getCityByName(cityName));
			hotCitysData.add(cityData);
		}
		hotCitys.setCitys(hotCitysData.toArray(new DistrictData[0]));
		cityItems.add(hotCitys);
		return cityItems;
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_select_citys);
		setTitle(R.string.select_city);

		et_search = (EditText) findViewById(R.id.et_search);
		pslv_citys = (PinnedSectionListView) findViewById(R.id.pslv_citys);
		ll_sections_side_bar = (SideBarView) findViewById(R.id.ll_sections_side_bar);
		pslv_citys.setShadowVisible(false);
		if (cityListAdapter != null) {
			pslv_citys.setAdapter(cityListAdapter);
		}
		ll_sections_side_bar.setOnBarItemClickListener(this);
		refreshSideBar(cityListAdapter.getSections());
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		TextViewDrawableClickUtil.setRightDrawableDelete(et_search);
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					cityListAdapter.clear();
					List<CityItem> cityItems = new ArrayList<>();
					CityItem locatinSection = new CityItem(
							CityItem.ITEM_TYPE_SECTION);
					initCityListAdapter();
				} else {
					cityListAdapter.clear();
					cityListAdapter.add(getCityItemsBysearchArg(s.toString()
							.trim()));
				}
				refreshSideBar(cityListAdapter.getSections());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onCitySelected(DistrictData city) {
		if (!city.getDistrictName().equals(
				getResources().getString(R.string.lbsing))) {
			Intent intent = new Intent();
			intent.putExtra(KEY.INTENT.CITY_ID, city.getDistrictCode());
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onCitySelected(String cityStr) {
		LocationInfo city = LocationInfos.getInstance().getCityByName(cityStr);
		if (city != null) {
			Intent intent = new Intent();
			intent.putExtra(KEY.INTENT.CITY_ID, city.getCode());
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	private void refreshSideBar(Object[] section) {
		LayoutInflater mInflater = LayoutInflater.from(this);
		ll_sections_side_bar.removeAllViews();
		for (int i = 0; i < section.length; i++) {
			CityItem sectionItem = (CityItem) section[i];
			TextView textView = (TextView) mInflater.inflate(
					R.layout.layout_select_city_side_bar_item, null);
			textView.setText(sectionItem.getSection().substring(0, 1));
			textView.setTag(sectionItem);
			ll_sections_side_bar.addView(textView);
		}
		ll_sections_side_bar.invalidate();
	}

	private void onSideItemSelected(int sectionPosition) {
		int position = cityListAdapter.getPositionForSection(sectionPosition);
		pslv_citys.setSelection(position);
	}

	/**
	 * 定位
	 */
	private void startLbs() {

		LbsManager.getInstance().getLocation(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				String cityStr = location.getCity();
				if (cityStr != null) {
					synchronized (locationItem) {
						LocationInfo locationInfo = LocationInfos.getInstance()
								.getCityByName(cityStr);
						DistrictData cityData = new DistrictData();
						cityData.setByLocationInfo(locationInfo);
						locationItem.setCitys(cityData);
						cityListAdapter.notifyDataSetChanged();
						locationItem.notify();
					}

				}
			}

		});
	}

	@Override
	public void onBarItemClick(int index) {
		onSideItemSelected(index);
	}

}
