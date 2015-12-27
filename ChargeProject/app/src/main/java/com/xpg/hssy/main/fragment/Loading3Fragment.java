package com.xpg.hssy.main.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssychargingpole.R;

@SuppressLint("NewApi")
public class Loading3Fragment extends BaseFragment implements OnClickListener {
	private Button jump_to_main;

	public Loading3Fragment() {
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
		View v = inflater.inflate(R.layout.loading3, container, false);
		jump_to_main = (Button) v.findViewById(R.id.jump_to_main);
		jump_to_main.setOnClickListener(this);
		return v;
	}

	@Override
	public String getFragmentName() {
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jump_to_main:
			Intent Intent = new Intent(getActivity(), MainActivity.class);
			startActivity(Intent);
			getActivity().finish();
			break;

		default:
			break;
		}
	}

}
