package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.easy.util.ToastUtil;
import com.xpg.hssy.adapter.SearchPileAndAddressAdapter;
import com.xpg.hssy.adapter.SearchPileAndAddressAdapter.AdapterItem;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.db.DbHelper;
import com.xpg.hssy.db.pojo.AddressSearchRecode;
import com.xpg.hssy.db.pojo.Pile;
import com.xpg.hssy.db.pojo.PileSearchRecode;
import com.xpg.hssy.popwindow.SearchTypeSelectPopWindow;
import com.xpg.hssy.util.TipsUtil;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchPileAndAddressActivity extends BaseActivity implements OnGetPoiSearchResultListener, OnClickListener, OnItemClickListener {
	private View btn_left;
	private TextView tv_find_type;
	private ImageView iv_pull_down;
	private EditText et_search;
	private ImageView iv_icon_delete;
	private ListView search_recode_list;
	private View search_message_view;
	private TextView tv_message;
	private TextView tv_message_detail;
	private SearchTypeSelectPopWindow searchTypeSelcetWindow;

	private String city;

	private PoiSearch mPoiSearch = null;

	private SearchPileAndAddressAdapter addressAdapter = null;
	private SearchPileAndAddressAdapter pileAdapter = null;

	private LoadPileHandler loadPileHandler;
	private double longitude;
	private double latitude;
	//    private
	private final int SEARCH_TYPE_ADDRESS = 0;
	private final int SEARCH_TYPE_PILE = 1;
	private int searchType = SEARCH_TYPE_ADDRESS;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		// POI搜索API
		mPoiSearch = PoiSearch.newInstance();
		city = getIntent().getStringExtra(KEY.INTENT.CITY);
		List<AddressAdapterItem> addressSearchRecodes = new ArrayList<>();
		List<PileAdapterItem> pileSearchRecodes = new ArrayList<>();
		loadPileHandler = new LoadPileHandler();
		latitude = getIntent().getDoubleExtra(KEY.INTENT.LATITUDE, -1);
		longitude = getIntent().getDoubleExtra(KEY.INTENT.LONGITUDE, -1);
		addressAdapter = new SearchPileAndAddressAdapter(this, addressSearchRecodes);
		pileAdapter = new SearchPileAndAddressAdapter(this, pileSearchRecodes);
	}

	@Override
	protected void initUI() {
		setContentView(R.layout.select_address_activity);
		search_message_view = LayoutInflater.from(this).inflate(R.layout.layout_search_pile_and_adress_head, null);
		tv_message = (TextView) search_message_view.findViewById(R.id.tv_message);
		tv_message_detail = (TextView) search_message_view.findViewById(R.id.tv_message_detail);
		btn_left = findViewById(R.id.btn_left);
		tv_find_type = (TextView) findViewById(R.id.tv_find_type);
		et_search = (EditText) findViewById(R.id.et_search);
		iv_pull_down = (ImageView) findViewById(R.id.iv_pull_down);
		iv_icon_delete = (ImageView) findViewById(R.id.iv_icon_delete);
		search_recode_list = (ListView) findViewById(R.id.address_list);
		searchTypeSelcetWindow = new SearchTypeSelectPopWindow(this);
		search_recode_list.addHeaderView(search_message_view);
		switch (searchType) {
			case SEARCH_TYPE_ADDRESS:{
				search_recode_list.setAdapter(addressAdapter);
				loadAddressSearchRecodes();
				break;
			}
			case SEARCH_TYPE_PILE:{
				search_recode_list.setAdapter(pileAdapter);
				loadPileSearchRecodes();
				break;
			}
		}
		iv_icon_delete.setVisibility(View.GONE);
	}

	@Override
	protected void initEvent() {
		btn_left.setOnClickListener(this);
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		search_recode_list.setOnItemClickListener(this);
		tv_find_type.setOnClickListener(this);
		iv_pull_down.setOnClickListener(this);
		iv_icon_delete.setOnClickListener(this);
		// 监听editText事件
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				switch (searchType) {
					case SEARCH_TYPE_ADDRESS: {
						if (TextUtils.isEmpty(et_search.getText())) {
							iv_icon_delete.setVisibility(View.GONE);
							loadAddressSearchRecodes();
						} else {
							iv_icon_delete.setVisibility(View.VISIBLE);
							addressAdapter.clear();
							mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(et_search.getText().toString()));
						}
						break;
					}
					case SEARCH_TYPE_PILE: {
						if (TextUtils.isEmpty(et_search.getText())) {
							iv_icon_delete.setVisibility(View.GONE);
							loadPileSearchRecodes();
						} else {
							iv_icon_delete.setVisibility(View.VISIBLE);
							pileAdapter.clear();
							WebAPIManager.getInstance().searchPiles(et_search.getText().toString(), 0, 999, loadPileHandler);
						}
						break;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

		});

		searchTypeSelcetWindow.setFindPileListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchType = SEARCH_TYPE_PILE;
				pileAdapter.clear();
				search_recode_list.setAdapter(pileAdapter);
				search_recode_list.invalidate();
				et_search.setText("");
				tv_find_type.setText(R.string.find_name);
				searchTypeSelcetWindow.dismiss();
			}
		});
		searchTypeSelcetWindow.setFindAddressListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchType = SEARCH_TYPE_ADDRESS;
				addressAdapter.clear();
				search_recode_list.setAdapter(addressAdapter);
				search_recode_list.invalidate();
				et_search.setText("");
				tv_find_type.setText(R.string.find_address);
				searchTypeSelcetWindow.dismiss();
			}
		});

		search_message_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (searchType) {
					case SEARCH_TYPE_ADDRESS: {
						cleanAddressSearchRecode();
						break;
					}
					case SEARCH_TYPE_PILE: {
						cleanPileSearchRecode();
						break;
					}
				}
			}
		});
		search_recode_list.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.tv_find_type: {
				searchTypeSelcetWindow.showAsDropDown(tv_find_type, -tv_find_type.getWidth() / 2, 5);
				break;
			}
			case R.id.iv_pull_down: {
				searchTypeSelcetWindow.showAsDropDown(tv_find_type, -tv_find_type.getWidth() / 2, 5);
				break;
			}
			case R.id.btn_left: {
				onBackPressed();
				break;
			}
			case R.id.iv_icon_delete: {
				et_search.setText("");
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int itemIndex = position - search_recode_list.getHeaderViewsCount();
		if (itemIndex > -1) {
			switch (searchType) {
				case SEARCH_TYPE_ADDRESS: {
					AdapterItem item = addressAdapter.get(itemIndex);//减去头部的View数量,得出真正的item下标
					AddressSearchRecode searchResult = (AddressSearchRecode) item.getSource();
					Intent data = new Intent();
					data.putExtra(KEY.INTENT.CITY, searchResult.getCity());
					data.putExtra(KEY.INTENT.LATITUDE, searchResult.getLatitude());
					data.putExtra(KEY.INTENT.LONGITUDE, searchResult.getLongitude());
					data.putExtra(KEY.INTENT.ADDRESS, searchResult.getName());
					DbHelper.getInstance(this).getAddressSearchRecodeDao().insertOrReplace(searchResult);
					setResult(RESULT_OK, data);
					finish();
					break;
				}
				case SEARCH_TYPE_PILE: {
					AdapterItem item = pileAdapter.get(itemIndex);//减去头部的View数量,得出真正的item下标
					PileSearchRecode pileSearchRecode = (PileSearchRecode) item.getSource();
					Intent intent = null;
					if (pileSearchRecode.getOperator() == Pile.OPERATOR_PERSONAL) {
						intent = new Intent(self, PileInfoNewActivity.class);
						intent.putExtra(KEY.INTENT.GPRS_TYPE, pileSearchRecode.getGprsType());
					} else {
						intent = new Intent(self, PileStationInfoActivity.class);
					}
					intent.putExtra(KEY.INTENT.PILE_ID, pileSearchRecode.getPileId());
					intent.putExtra(KEY.INTENT.LATITUDE, latitude);
					intent.putExtra(KEY.INTENT.LONGITUDE, longitude);
					DbHelper.getInstance(this).getPileSearchRecodeDao().insertOrReplace(pileSearchRecode);
					startActivity(intent);
//					finish();
					break;
				}
			}
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		List<AddressAdapterItem> results = new ArrayList<>();
		if (et_search.getText().toString().equals("")) {
			return;
		}
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			ToastUtil.show(self, "未找到结果");
			updateAddressSearchResult(results);
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			List<PoiInfo> poiInfos = result.getAllPoi();
			if (poiInfos == null) {
				updateAddressSearchResult(results);
				return;
			}

			for (PoiInfo poiInfo : poiInfos) {
				if (poiInfo == null || poiInfo.location == null) {
					continue;
				}
				AddressSearchRecode addressResult = new AddressSearchRecode(poiInfo);
				results.add(new AddressAdapterItem(addressResult));
			}
			updateAddressSearchResult(results);
			return;
		}
//		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
//			updateAddressSearchResult(results);
//			if (result.getSuggestCityList() == null) {
//				return;
//			}
//			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//			String strInfo = "在";
//			for (CityInfo cityInfo : result.getSuggestCityList()) {
//				strInfo += cityInfo.city;
//				strInfo += ",";
//			}
//			strInfo += "找到结果";
//			ToastUtil.show(self, strInfo);
//		}
	}


	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			ToastUtil.show(self, "抱歉，未找到结果");
		} else {
			ToastUtil.show(self, result.getName() + ": " + result.getAddress());
		}
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

	private List<AddressAdapterItem> getAddressSearchRecodes() {
		List<AddressSearchRecode> searchRecodes = DbHelper.getInstance(self).getAddressSearchRecodeDao().loadAll();
		Collections.reverse(searchRecodes);
		List<AddressAdapterItem> addressAdapterItmes = new ArrayList<>(searchRecodes.size());
		for (AddressSearchRecode addressSearchRecode : searchRecodes) {
			addressSearchRecode.setRecodeType(AddressSearchRecode.FROM_RECODE);
			addressAdapterItmes.add(new AddressAdapterItem(addressSearchRecode));
		}

		return addressAdapterItmes;
	}

	private List<PileAdapterItem> getPileSearchRecodes() {
		List<PileSearchRecode> searchRecodes = DbHelper.getInstance(self).getPileSearchRecodeDao().loadAll();
		Collections.reverse(searchRecodes);
		List<PileAdapterItem> pileAdapterItmes = new ArrayList<>(searchRecodes.size());
		for (PileSearchRecode pileSearchRecode : searchRecodes) {
			pileAdapterItmes.add(new PileAdapterItem(pileSearchRecode));
		}

		return pileAdapterItmes;
	}

	private void updateAddressSearchResult(List<AddressAdapterItem> addressAdapterItems) {
		addressAdapter.clear();
		tv_message.setText(R.string.search_result);
		if (addressAdapterItems.size() > 0) {
			tv_message_detail.setVisibility(View.GONE);
		} else {
			tv_message_detail.setText(R.string.no_search_result);
			tv_message_detail.setVisibility(View.VISIBLE);
		}
		search_message_view.setEnabled(false);
		addressAdapter.add(addressAdapterItems);
	}

	private void loadAddressSearchRecodes() {
		addressAdapter.clear();
		tv_message.setText(R.string.search_recode);
		List<AddressAdapterItem> searchRecodes = getAddressSearchRecodes();
		if (searchRecodes.size() > 0) {
			search_message_view.setEnabled(true);
			tv_message_detail.setText(R.string.delete_all_recode);
		} else {
			search_message_view.setEnabled(false);
			tv_message_detail.setText(R.string.no_search_recode);
		}
		tv_message_detail.setVisibility(View.VISIBLE);
		addressAdapter.add(searchRecodes);
	}

	private void updatePileSearchResult(List<PileAdapterItem> pileAdapterItems) {
		pileAdapter.clear();
		tv_message.setText(R.string.search_result);
		if (pileAdapterItems.size() > 0) {
			tv_message_detail.setVisibility(View.GONE);
		} else {
			tv_message_detail.setText(R.string.no_search_result);
			tv_message_detail.setVisibility(View.VISIBLE);
		}
		search_message_view.setEnabled(false);
		pileAdapter.add(pileAdapterItems);
	}

	private void loadPileSearchRecodes() {
		pileAdapter.clear();
		tv_message.setText(R.string.search_recode);
		List<PileAdapterItem> searchRecodes = getPileSearchRecodes();
		if (searchRecodes.size() > 0) {
			search_message_view.setEnabled(true);
			tv_message_detail.setText(R.string.delete_all_recode);
		} else {
			search_message_view.setEnabled(false);
			tv_message_detail.setText(R.string.no_search_recode);
		}
		tv_message_detail.setVisibility(View.VISIBLE);
		pileAdapter.add(searchRecodes);
	}

	private void cleanPileSearchRecode() {
		pileAdapter.clear();
		search_message_view.setEnabled(false);
		tv_message_detail.setText(R.string.no_search_recode);
		DbHelper.getInstance(this).getPileSearchRecodeDao().deleteAll();
	}

	private void cleanAddressSearchRecode() {
		addressAdapter.clear();
		search_message_view.setEnabled(false);
		tv_message_detail.setText(R.string.no_search_recode);
		DbHelper.getInstance(this).getAddressSearchRecodeDao().deleteAll();
	}


	private class LoadPileHandler extends WebResponseHandler<List<Pile>> {
		@Override
		public void onError(Throwable e) {
			super.onError(e);
			TipsUtil.showTips(self, e);
		}

		@Override
		public void onFailure(WebResponse<List<Pile>> response) {
			TipsUtil.showTips(self, response);
		}

		@Override
		public void onSuccess(WebResponse<List<Pile>> response) {
			super.onSuccess(response);
			if (TextUtils.isEmpty(et_search.getText())) {
				return;
			}
			List<Pile> piles = response.getResultObj();
			List<PileAdapterItem> pileAdapterItems = new ArrayList<>();
			if (piles != null) {
				for (Pile pile : piles) {
					PileSearchRecode searchRecode = new PileSearchRecode();
					searchRecode.setPileId(pile.getPileId());
					searchRecode.setName(pile.getPileName());
					searchRecode.setAddress(pile.getLocation());
					searchRecode.setLatitude(pile.getLatitude());
					searchRecode.setLongitude(pile.getLongitude());
					searchRecode.setOperator(pile.getOperator());
					searchRecode.setGprsType(pile.getGprsType());
					pileAdapterItems.add(new PileAdapterItem(searchRecode));
				}
			}
			updatePileSearchResult(pileAdapterItems);
		}
	}

	private class AddressAdapterItem extends AdapterItem<AddressSearchRecode> {
		public AddressAdapterItem(AddressSearchRecode recode) {
			super(recode);
		}

		@Override
		public String getName() {
			return getSource().getName();
		}

		@Override
		public String getAddress() {
			return getSource().getAddress();
		}

		@Override
		public Double getLongitude() {
			return getSource().getLongitude();
		}

		@Override
		public Double getLatitude() {
			return getSource().getLatitude();
		}
	}

	private class PileAdapterItem extends AdapterItem<PileSearchRecode> {
		public PileAdapterItem(PileSearchRecode recode) {
			super(recode);
		}

		@Override
		public String getName() {
			return getSource().getName();
		}

		@Override
		public String getAddress() {
			return getSource().getAddress();
		}

		@Override
		public Double getLongitude() {
			return getSource().getLongitude();
		}

		@Override
		public Double getLatitude() {
			return getSource().getLatitude();
		}
	}
}
