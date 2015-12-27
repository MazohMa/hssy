package com.xpg.hssy.main.activity.launch;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.fragment.Loading1Fragment;
import com.xpg.hssy.main.fragment.Loading2Fragment;
import com.xpg.hssy.main.fragment.Loading3Fragment;
import com.xpg.hssy.view.PageView;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity2 extends BaseActivity {
	private PageView mViewPager;
	private List<Fragment> fragments;
	private Loading1Fragment loading1Fragment;
	private Loading2Fragment loading2Fragment;
	private Loading3Fragment loading3Fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mViewPager.setCurrentItem(0);
	}

	@Override
	protected void initData() {
		super.initData();
		loading1Fragment = new Loading1Fragment();
		loading2Fragment = new Loading2Fragment();
		loading3Fragment = new Loading3Fragment();
		if (fragments == null) {
			fragments = new ArrayList<Fragment>();
		}
		fragments.add(loading1Fragment);
		fragments.add(loading2Fragment);
		fragments.add(loading3Fragment);

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.guide_activity2);
		mViewPager = (PageView) findViewById(R.id.view_pager);
		mViewPager.setPages(fragments);
		mViewPager.initAdapter(getSupportFragmentManager());
	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}
}
