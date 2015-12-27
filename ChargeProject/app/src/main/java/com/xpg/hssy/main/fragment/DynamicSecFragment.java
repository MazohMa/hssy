package com.xpg.hssy.main.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.adapter.DynamicFragmentAdapter;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.bean.DynamicInfo;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.view.DropDownListView;
import com.xpg.hssy.view.PilePhotoPager;
import com.xpg.hssy.view.RefreshListView;
import com.xpg.hssy.web.WebAPIManager;
import com.xpg.hssy.web.WebResponse;
import com.xpg.hssy.web.WebResponseHandler;
import com.xpg.hssychargingpole.BuildConfig;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicSecFragment extends BaseFragment implements OnClickListener, DropDownListView.OnDropDownListener {

	LayoutInflater inflater;
	private final String WEB_PREFIX = BuildConfig.BASE_URL + "/news/view?infoid=";
	private DisplayImageOptions option;
	private LoadingDialog loadingDialog = null;
	private SharedPreferences sp;
	private RefreshListView refreshListView;
	private DynamicFragmentAdapter dynamicFragmentAdapter;
	private PilePhotoPager pilePhotoPager;
	private LinearLayout ll_indicator;
	private ViewGroup head;
	private View contentView;
	private List<String> imgUrls = new ArrayList<>();
	private Map<String, String> imgExtra = new HashMap<>();
	private List<DynamicInfo> info = new ArrayList<>();
	private int page;

	// 加载列表
	private OnClickListener onBottomListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			getInfoList(page);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new RoundedBitmapDisplayer((int)
				getResources().getDimension(R.dimen.h23))).build();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (pilePhotoPager != null) {
			if (isVisibleToUser) {
				pilePhotoPager.startAutoScroll();
			} else {
				pilePhotoPager.pauseAutoScroll();
			}
		}
	}

	@Override
	public void onClick(View view) {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		this.inflater = inflater;
		contentView = inflater.inflate(R.layout.fragment_dynamic_list, null);
		initData();
		initView();
		loadingData();
		return contentView;
	}

	private void loadingData(){
		refreshListView.onDropDown();
	}

	private void initData() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	private void initView() {
		head = (ViewGroup) inflater.inflate(R.layout.layout_dynamic_head_pager, null);
		pilePhotoPager = (PilePhotoPager) head.findViewById(R.id.ppp_photo);
		//		pilePhotoPager.setLoop(true);//循环轮播
		//		pilePhotoPager.setAutoScroll(true);//自动
		pilePhotoPager.loop();
		pilePhotoPager.setJumpType(PilePhotoPager.JUMP_TYPE_TO_WEBVIEW);
		ll_indicator = (LinearLayout) head.findViewById(R.id.ll_indicator);
		refreshListView = (RefreshListView) contentView.findViewById(R.id.rf_lv);
		dynamicFragmentAdapter = new DynamicFragmentAdapter(getActivity(), info);
		refreshListView.setAdapter(dynamicFragmentAdapter);
		refreshListView.setOnDropDownListener(this);

		refreshListView.setOnBottomListener(onBottomListener);
		refreshListView.addHeaderView(head);
		pilePhotoPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (ll_indicator != null && ll_indicator.getChildCount() > 0) {
					int childCount = ll_indicator.getChildCount();
					if (childCount > 0) {
						for (int i = 0; i < childCount; i++) {
							ll_indicator.getChildAt(i).setSelected(false);
						}
						ll_indicator.getChildAt(position).setSelected(true);
					}
					if (childCount != 0) {//必须判断是否为0 fixbug by Mazoh
						ll_indicator.getChildAt(position % childCount).setSelected(true);
					}

				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDropDown() {
		getContentData();
	}

	//获取初始化的列表数据
	private void getInfoList(int pageIndex) {

		WebAPIManager.getInstance().getInfoList(pageIndex, new WebResponseHandler<List<DynamicInfo>>(getActivity()) {
			@Override
			public void onStart() {
				super.onStart();
			}

			@Override
			public void onSuccess(WebResponse<List<DynamicInfo>> response) {
				super.onSuccess(response);
				List<DynamicInfo> infoList = response.getResultObj();
				//当页码等于0时，表示当前是一个初始化操作或者下拉刷新操作
				if (page == 0) {
					getHeaderImgs(infoList);
					setHeadData();
					dynamicFragmentAdapter.clear();
				}
				refreshListView.completeRefresh();
				if (infoList.size() == 0) {
					refreshListView.showNoMore();
				} else {
					page++;
					refreshListView.prepareLoad();
					dynamicFragmentAdapter.add(infoList);
				}
			}

			@Override
			public void onFailure(WebResponse<List<DynamicInfo>> response) {
				super.onFailure(response);
				handleError();
			}

			@Override
			public void onError(Throwable e) {
				super.onError(e);
				handleError();
			}
		});
	}

	private void handleError(){
		refreshListView.completeLoad();
		if(page == 0) {
			refreshListView.removeHeaderView(head);
			refreshListView.showRefreshFail();
		} else {
			refreshListView.showLoadFail();
		}
	}

	//从InfoList中获取头部图片
	private void getHeaderImgs(List<DynamicInfo> infoList) {
		imgUrls.clear();
		imgExtra.clear();
		List<DynamicInfo> tmp_info = new ArrayList<>();
		for (DynamicInfo info : infoList) {
			if (info.isTop()) {
				imgUrls.add(info.getCoverImg());
				imgExtra.put(info.getCoverImg(), WEB_PREFIX + info.getId());
				tmp_info.add(info);
			}
		}
		infoList.removeAll(tmp_info);
	}

	private void setHeadData() {
		pilePhotoPager.clearItems();
		if (imgUrls != null && imgUrls.size() > 0) {
			if(refreshListView.getHeaderViewsCount()<2)
			{
				refreshListView.addHeaderView(head);
			}
			ll_indicator.removeAllViews();
			for (String url : imgUrls) {
				String link = imgExtra.get(url);
				pilePhotoPager.loadPhoto(url, link);
				ll_indicator.addView(inflater.inflate(R.layout.iv_indicator, null));
			}
		} else {
			refreshListView.removeHeaderView(head);
		}
		if (pilePhotoPager.getCount() > 0) {
			if (pilePhotoPager.getCount() > 1) {
				ll_indicator.getChildAt(0).setSelected(true);
				pilePhotoPager.setScrollable(true);
				pilePhotoPager.setAutoScroll(true);
			} else {
				ll_indicator.removeAllViews();
				pilePhotoPager.setScrollable(false);
				pilePhotoPager.setAutoScroll(false);
			}
		}
		pilePhotoPager.notifyDataSetChanged();
	}

	private void getContentData() {
		getInfoList(page = 0);
	}
}