package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssychargingpole.R;

public class SubmitPileSuccessActivity extends BaseActivity implements View.OnClickListener {

	private TextView tv_center;
	private ImageButton btn_left;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_submit_pile_success);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		tv_center = (TextView) findViewById(R.id.tv_center);

		tv_center.setText("添加完成");
		btn_left.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left: {
				onBackPressed();
			}
		}
	}
}
