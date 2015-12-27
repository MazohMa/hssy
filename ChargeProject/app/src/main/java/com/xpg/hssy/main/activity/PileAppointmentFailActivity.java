package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.view.DianzhuangAppointmentFailView;
import com.xpg.hssychargingpole.R;

public class PileAppointmentFailActivity extends BaseActivity {
	private LinearLayout content_layout;
	private TextView tv_title;
	private DianzhuangAppointmentFailView dianzhuangAppointmentFailView;
	private LinearLayout.LayoutParams params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		View view = LayoutInflater.from(this).inflate(
				R.layout.dianzhaungappointment, null);
		setContentView(view);
		hideSoftInput(view);
		tv_title = (TextView) findViewById(R.id.tv_center);
		tv_title.setText("电桩预约");
		content_layout = (LinearLayout) findViewById(R.id.dianzhuang_appointment_content_layout);
		dianzhuangAppointmentFailView = new DianzhuangAppointmentFailView(this);
		params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		content_layout.removeAllViews();
		/*
		 * 填充添加自定义view
		 */
		content_layout.addView(dianzhuangAppointmentFailView, params);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
