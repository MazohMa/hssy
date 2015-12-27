package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssychargingpole.R;

@SuppressLint("NewApi")
public class Loading1Fragment extends BaseFragment {

	public Loading1Fragment() {
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {

		super.onResume();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.loading1, container, false);
		return v;
	}

	private void initData() {
	}

	@Override
	public String getFragmentName() {
		return null;
	}

}
