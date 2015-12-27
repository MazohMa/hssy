package com.xpg.hssy.main.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xpg.hssy.base.BaseFragment;
import com.xpg.hssychargingpole.R;

/**
 * Created by black-Gizwits on 2015/11/24.
 */
public class ChargeRecordSynFragment extends BaseFragment {

	public final static String RECEIVE_ACTION_RECORD_SYN = "com.xpg.hssy.main.fragment.ChargeRecordSynFragment.record.syn";
	public final static String KEY_RECORD_SYN_PERCENT = "syn_percent";

	private TextView tv_head_title;
	private TextView tv_second_title;
	private ImageView iv_syn_icon;
	private ProgressBar pb_syn_progress_bar;
	private TextView tv_progress_percent;
	private Button btn_left;
	private Button btn_right;
	private SynRecordReceiver synRecordReceiver;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_charge_record_syn, null);
		tv_head_title=(TextView) v.findViewById(R.id.tv_head_title);
		tv_second_title=(TextView) v.findViewById(R.id.tv_second_title);
		iv_syn_icon = (ImageView) v.findViewById(R.id.iv_syn_icon);
		pb_syn_progress_bar = (ProgressBar) v.findViewById(R.id.pb_syn_progress_bar);
		tv_progress_percent = (TextView) v.findViewById(R.id.tv_progress_percent);
		btn_left = (Button) v.findViewById(R.id.btn_left);
		btn_right = (Button) v.findViewById(R.id.btn_right);
		return v;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_left: {
				if (btn_left.getText().equals(getString(R.string.btn_cancel_syn))) {
					setUserVisibleHint(false);
				} else if (btn_left.getText().equals(getString(R.string.btn_back))) {
					setUserVisibleHint(false);
				}
				break;
			}
			case R.id.btn_right: {
				//查看订单
				break;
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	protected void onInvisible() {
		if (synRecordReceiver != null) {
			getActivity().unregisterReceiver(synRecordReceiver);
		}
		super.onInvisible();
	}

	@Override
	protected void onVisible() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RECEIVE_ACTION_RECORD_SYN);
		synRecordReceiver = new SynRecordReceiver();
		getActivity().registerReceiver(synRecordReceiver, intentFilter);
		super.onVisible();
	}

	private class SynRecordReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int percent = intent.getIntExtra(KEY_RECORD_SYN_PERCENT, 0);
			pb_syn_progress_bar.setProgress(percent);
			tv_progress_percent.setText(percent + "%");
			if (percent == 100) {
				tv_head_title.setText(R.string.synchronized_success);
				tv_second_title.setVisibility(View.INVISIBLE);
				iv_syn_icon.setImageResource(R.drawable.charging_succeed);
				btn_right.setVisibility(View.VISIBLE);
				btn_left.setText(R.string.btn_back);
			}
		}
	}
}
