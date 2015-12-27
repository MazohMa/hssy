package com.xpg.hssy.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * @author Joke
 * @version 1.0.0
 * @description
 * @email 113979462@qq.com
 * @create 2015年3月26日
 */

public abstract class BaseFragment extends Fragment implements OnClickListener {
	public String TAG = this.getClass().getSimpleName();
	protected boolean isVisiable;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView");

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "onCreate");

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.e(TAG, "onViewCreated");

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.e(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.e(TAG, "onResume");

		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e(TAG, "onActivityCreated");

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onPause() {
		Log.e(TAG, "onPause");

		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e(TAG, "onStop");

		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");

		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e(TAG, "onDetach");

		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		Log.e(TAG, "onDestroyView");

		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.e(TAG, "onAttach");

		super.onAttach(activity);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.e(TAG, "setUserVisibleHint: " + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			isVisiable = true;
			onVisible();
		} else {
			isVisiable = false;
			onInvisible();
		}
	}

	protected void onVisible() {
		// Log.e(this.getClass().getSimpleName(), "onVisible");
	}

	protected void onInvisible() {
		// Log.e(this.getClass().getSimpleName(), "onInvisible");
	}

	public void onChanged() {

	}

	@Override
	public void onClick(View view) {
	}

	public String getFragmentName() {
		return getClass().getSimpleName();
	}
}
