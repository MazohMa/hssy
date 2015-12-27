package com.xpg.hssy.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssy.view.PageView;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

public class FocusFragment extends BaseFragment implements OnClickListener {

	private DisplayImageOptions option;
	private LoadingDialog loadingDialog = null;
	private SharedPreferences sp;
	private ChargeTimeLineFragment themeSecFragment;
	//	private ProjectSecFragment projectSecFragment ;
	private DynamicSecFragment dynamicSecFragment;
	private PageView view_pager;

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
		if (themeSecFragment != null)
			themeSecFragment.setUserVisibleHint(themeSecFragment.getUserVisibleHint());
		if (dynamicSecFragment != null)
			dynamicSecFragment.setUserVisibleHint(dynamicSecFragment.getUserVisibleHint());
	}

	@Override
	public void onClick(View view) {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		initData();
		View v = initView(inflater);
		return v;
	}

	private void initData() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		themeSecFragment = new ChargeTimeLineFragment();
//		projectSecFragment = new ProjectSecFragment() ;
		dynamicSecFragment = new DynamicSecFragment();
	}

	private View initView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.focus_fragment, null);
		view_pager = (PageView) v.findViewById(R.id.sec_view_pager);
		RadioGroup rg_focus_tab = (RadioGroup) v.findViewById(R.id.rg_focus_tab);
		view_pager.setRadioGroup(rg_focus_tab);
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(themeSecFragment);
//		fragments.add(projectSecFragment);
		fragments.add(dynamicSecFragment);
		view_pager.setPages(fragments);
		view_pager.initAdapter(getChildFragmentManager());
		view_pager.setCurrentItem(0);
		setScrollable(true);
		return v;
	}

	public void setScrollable(boolean b) {
		if (view_pager != null) view_pager.setScrollable(b);
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

	private boolean isLogin() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		return sp.getBoolean("isLogin", false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (view_pager.getCurrentPage() instanceof ChargeTimeLineFragment) {
			view_pager.getCurrentPage().onActivityResult(requestCode, resultCode, data);
		}
	}
}