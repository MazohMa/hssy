package com.xpg.hssy.main.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.main.fragment.PayRecordsFragment;
import com.xpg.hssy.main.fragment.RechargeRecordsFragment;
import com.xpg.hssy.view.PageView;
import com.xpg.hssychargingpole.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 */
public class PaymentRecordsActivity extends BaseActivity {

	private PageView view_pager;
	private RechargeRecordsFragment rechargeRecordsFragment;
	private PayRecordsFragment payRecordsFragment;

	@Override
	protected void initData() {
		super.initData();
		rechargeRecordsFragment = new RechargeRecordsFragment();
		payRecordsFragment = new PayRecordsFragment();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_payment_records);
		((TextView) findViewById(R.id.tv_center)).setText(R.string.payment_records);
		view_pager = (PageView) findViewById(R.id.payment_record_view_pager);
		RadioGroup rg_focus_tab = (RadioGroup) findViewById(R.id.rg_payment_records_tab);
		view_pager.setRadioGroup(rg_focus_tab);
		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(rechargeRecordsFragment);
		fragments.add(payRecordsFragment);
		view_pager.setPages(fragments);
		view_pager.initAdapter(getSupportFragmentManager());
		view_pager.setCurrentItem(0);
		setScrollable(true);
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
				finish();
				break;
		}
	}

	public void setScrollable(boolean b) {
		if (view_pager != null) view_pager.setScrollable(b);
	}
}
