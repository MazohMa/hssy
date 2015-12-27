package com.xpg.hssy.main.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.fragment.ChargeRecordFragment;
import com.xpg.hssy.view.PageView;
import com.xpg.hssychargingpole.R;

/**
 * @Description 充电记录
 * @author Mazoh
 * @createDate 2015年03月16日
 * @version 2.0.0
 */
public class ChargeRecordActivity extends BaseActivity {

	public static final String TAG = "ChargeRecordActivity";
	private PageView mViewPager;
	private RadioGroup mRadioGroup;
	private List<Fragment> fragments;
	private String pile_id;
    private ImageButton btn_right ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void initData() {
		super.initData();
		pile_id = getIntent().getStringExtra("pile_id");

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.fragment_home);
		setTitle("充电记录");
		// 家人权限，只能拥有充电记录权限
		if (fragments == null) {
			fragments = new ArrayList<Fragment>();
		}
		btn_right = (ImageButton)findViewById(R.id.btn_right) ;
		btn_right.setVisibility(View.INVISIBLE);
		fragments.add(new ChargeRecordFragment(pile_id));
		mViewPager = (PageView) findViewById(R.id.view_pager);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tab_setting);
		mRadioGroup.setVisibility(View.GONE);
		findViewById(R.id.rl_tiao).setVisibility(View.GONE);
		mViewPager.setPages(fragments);
		mViewPager.initAdapter(getSupportFragmentManager());

	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

	}
}
