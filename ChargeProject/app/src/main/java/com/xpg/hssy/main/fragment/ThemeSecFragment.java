package com.xpg.hssy.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.dialog.LoadingDialog;
import com.xpg.hssy.main.activity.ThemeDisplayTopicActivity;
import com.xpg.hssy.main.activity.TopicDetailActivity;
import com.xpg.hssy.util.ImageLoaderManager;
import com.xpg.hssychargingpole.R;

public class ThemeSecFragment extends BaseFragment implements OnClickListener {

	private DisplayImageOptions option;
	private LoadingDialog loadingDialog = null;
	private SharedPreferences sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("tag","ThemeSecFragment ===onCreate") ;
		super.onCreate(savedInstanceState);
		option = new DisplayImageOptions.Builder().cloneFrom(ImageLoaderManager.getInstance().getDefaultDisplayOptions()).showImageForEmptyUri(R.drawable
				.touxiang).showImageOnFail(R.drawable.touxiang).showImageOnLoading(R.drawable.touxiang).displayer(new
				RoundedBitmapDisplayer((int) getResources().getDimension(R.dimen.h23))).build();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}



	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.bt_display :
				Intent login_intents = new Intent(getActivity(), ThemeDisplayTopicActivity.class);
				getActivity().startActivity(login_intents);
				break;
			case R.id.bt_topic_detail :
				Intent login_intent = new Intent(getActivity(), TopicDetailActivity.class);
				getActivity().startActivity(login_intent);
				break;


		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i("tag","ThemeSecFragment ===onCreateView") ;
		initData();
		View v = initView(inflater);
		return v;
	}

	private void initData() {
		sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	private View initView(LayoutInflater inflater) {
		View v = inflater.inflate(R.layout.pile_display_quick_exist,null) ;
		Button bt_display = (Button)v.findViewById(R.id.bt_display) ;
		Button bt_topic_detail = (Button)v.findViewById(R.id.bt_topic_detail) ;
		bt_display.setOnClickListener(this);
		bt_topic_detail.setOnClickListener(this);
		return v;
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
}