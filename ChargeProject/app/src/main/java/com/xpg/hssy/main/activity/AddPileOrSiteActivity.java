package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.fragment.AddPileOrSiteForInfoFragment;
import com.xpg.hssy.main.fragment.AddPileOrSiteForMapFragment;
import com.xpg.hssy.main.fragment.callbackinterface.GeoListenerOperater;
import com.xpg.hssy.util.UIUtil;
import com.xpg.hssychargingpole.R;

/**
 * AddPileOrSiteActivity
 *
 * @author Mazoh
 * @version 2.4.0
 * @description
 * @email 471977848@qq.com
 * @create 2015年9月21日
 */
public class AddPileOrSiteActivity extends BaseActivity implements GeoListenerOperater, View.OnClickListener {
	private LayoutInflater mInflater;
	private Fragment currentFragment;
	private AddPileOrSiteForInfoFragment addPileOrSiteForInfoFragment;
	private AddPileOrSiteForMapFragment addPileOrSiteForMapFragment;
	private TextView tv_left, tv_right;
	private Double longitude = null;
	private Double latitude = null;
	private String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInflater = LayoutInflater.from(this);
		addPileOrSiteForInfoFragment = new AddPileOrSiteForInfoFragment();
		addPileOrSiteForMapFragment = new AddPileOrSiteForMapFragment();

		View v = mInflater.inflate(R.layout.add_pile_or_site,
				null);
		setContentView(v);
		tv_left = (TextView) v.findViewById(R.id.tv_left);
		tv_right = (TextView) v.findViewById(R.id.tv_right);
		tv_right.setText("下一步");
		UIUtil.hideSoftInput(this, v);
		setTitle("设置位置");
		if(savedInstanceState == null){
			//检查一下savedInstanceState,防止Activity被回收后重启所导致的Fragment重复创建和重叠的问题
			getSupportFragmentManager().beginTransaction().add(R.id.fl_content, addPileOrSiteForMapFragment)
					.add(R.id.fl_content,addPileOrSiteForInfoFragment).
					hide(addPileOrSiteForInfoFragment).show(addPileOrSiteForMapFragment).commit();
		}



		tv_left.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		if (addPileOrSiteForMapFragment != null) {
			addPileOrSiteForMapFragment.setGeoListenerOperater(this);
		}
		if (addPileOrSiteForInfoFragment != null) {
			addPileOrSiteForInfoFragment.setIpileOrSiteDataOperater(new AddPileOrSiteForInfoFragment.IpileOrSiteDataOperater() {
				@Override
				public void uplate(double longitude, double latitude) {
					//更新百度地图地址
					switchContent(addPileOrSiteForInfoFragment, addPileOrSiteForMapFragment);
				}
			});
		}

	}

	@Override
	protected void initData() {
		super.initData();

	}

	@Override
	protected void initEvent() {
		super.initEvent();

	}

	public void switchContent(Fragment from, Fragment to) {
		if (currentFragment != to) {
			currentFragment = to;
			if (!to.isAdded()) {    // 先判断是否被add过
				getSupportFragmentManager().beginTransaction().hide(from).add(R.id.fl_content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				getSupportFragmentManager().beginTransaction().hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
			if (currentFragment == addPileOrSiteForInfoFragment) {
				if (this.longitude != null && this.latitude != null && this.address != null) {
					addPileOrSiteForInfoFragment.onListener(longitude, latitude, address);
				}
				setTitle("填写信息");
				tv_right.setVisibility(View.INVISIBLE);
			} else {
				if (longitude != null && latitude != null) {
					addPileOrSiteForMapFragment.onListener(longitude, latitude, "");
				}
				setTitle("设置位置");
				tv_right.setVisibility(View.VISIBLE);

			}
		}
	}

	/**
	 * 切换至信息模式
	 */
	private void change2InfoMode() {
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, addPileOrSiteForInfoFragment).commit();
		currentFragment = addPileOrSiteForInfoFragment;
		tv_right.setVisibility(View.INVISIBLE);
		setTitle("填写信息");
//		System.gc();
	}

	/**
	 * 切换至地图模式
	 */
	private void change2MapMode() {
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, addPileOrSiteForMapFragment).commit();
		currentFragment = addPileOrSiteForMapFragment;
		tv_right.setVisibility(View.VISIBLE);
		setTitle("设置位置");
//		System.gc();
	}

	@Override
	protected void initUI() {
		super.initUI();


	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		currentFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("TAG", "onKeyDown" + "");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (currentFragment == addPileOrSiteForInfoFragment) {
				switchContent(addPileOrSiteForInfoFragment, addPileOrSiteForMapFragment);
			} else {
				finish();
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.tv_right:
				if (longitude == null || latitude == null) {
					return;
				}
				if (currentFragment != null && currentFragment == addPileOrSiteForMapFragment) {
					switchContent(addPileOrSiteForMapFragment, addPileOrSiteForInfoFragment);
				} else {
					switchContent(addPileOrSiteForInfoFragment, addPileOrSiteForMapFragment);

				}

				break;
			case R.id.tv_left:
				if (currentFragment == addPileOrSiteForInfoFragment) {
					switchContent(addPileOrSiteForInfoFragment, addPileOrSiteForMapFragment);
				} else {
					finish();
				}
				break;
		}
	}

	@Override
	public void onListener(double longitude, double latitude, String address) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
//		if (this.longitude != null && this.latitude != null && this.address != null) {
//			addPileOrSiteForInfoFragment.onListener(longitude, latitude, address);
//		}
	}
}
