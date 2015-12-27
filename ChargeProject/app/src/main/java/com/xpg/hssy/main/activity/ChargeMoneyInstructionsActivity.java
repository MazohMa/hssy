package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssychargingpole.R;

/**
 * Created by Administrator on 2015/12/7.
 */
public class ChargeMoneyInstructionsActivity extends BaseActivity {

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_charge_money_instructions);
		((TextView) findViewById(R.id.tv_center)).setText(R.string.charge_money_instructions);
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		findViewById(R.id.btn_left).setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_left:
				onBackPressed();
				break;
		}
	}
}
