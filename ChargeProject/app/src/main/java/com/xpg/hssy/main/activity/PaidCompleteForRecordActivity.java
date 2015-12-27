package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.util.CalculateUtil;
import com.xpg.hssychargingpole.R;

/**
 * @author Mazoh
 * @version 2.4.0
 * @description
 */
public class PaidCompleteForRecordActivity extends BaseActivity implements OnClickListener {


	private TextView tv_electric_quantity, tv_start_time, tv_end_time, tv_money, tv_pile_name, tv_price,
			tv_charge_time_period, tv_price_unit,tv_paid_free_unit,tv_paid_free;
	private Button btn_ok;
	private ImageButton btn_left, btn_right;
	private TextView tv_order_num;
	private String orderId = null;
	private float servicePay ;
	private String quantity ;
	private String startTime ;
	private String endTime ;
	private long time_period ;
	private String chargePrice ;
	private String pileName ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent() ;
		if(intent != null){
			orderId = intent.getStringExtra("orderId") ;
			servicePay = intent.getFloatExtra("servicePay", 0.00f) ;
			quantity = intent.getStringExtra("quantity") ;
			startTime = intent.getStringExtra("startTime") ;
			endTime = intent.getStringExtra("endTime") ;
			time_period = intent.getLongExtra("time_period", 0) ;
			chargePrice= intent.getStringExtra("chargePrice") ;
			pileName = intent.getStringExtra("pileName") ;
		}
	}


	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_paid_complete_for_record);
		setTitle("充电完成");
		tv_order_num = (TextView) findViewById(R.id.tv_order_num);
		btn_left = (ImageButton) findViewById(R.id.btn_left);
		btn_right = (ImageButton) findViewById(R.id.btn_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_charge_time_period = (TextView) findViewById(R.id.tv_charge_time_period);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_pile_name = (TextView) findViewById(R.id.tv_pile_name);
		tv_electric_quantity = (TextView) findViewById(R.id.tv_electric_quantity);
		tv_start_time = (TextView) findViewById(R.id.tv_start_time);
		tv_end_time = (TextView) findViewById(R.id.tv_end_time);
		tv_money = (TextView) findViewById(R.id.tv_money);
		tv_price_unit = (TextView) findViewById(R.id.tv_price_unit);
		tv_paid_free_unit = (TextView)findViewById(R.id.tv_paid_free_unit) ;
		tv_paid_free = (TextView)findViewById(R.id.tv_paid_free) ;
		btn_ok = (Button) findViewById(R.id.btn_ok);


		tv_order_num.setText(orderId + "");
		CalculateUtil.infusePrice(tv_money, servicePay);
		tv_electric_quantity.setText(quantity);
		tv_start_time.setText(startTime);
		tv_end_time.setText(endTime);
		initTimeForCharge(time_period, tv_charge_time_period);
		tv_price.setText(chargePrice);
		tv_price_unit.setText(R.string.rmb_symbol);
		tv_pile_name.setText(pileName);
		tv_paid_free.setText(chargePrice);
		tv_paid_free_unit.setText(R.string.rmb_symbol);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		btn_ok.setOnClickListener(this);
		btn_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_left:
				this.sendBroadcast(new Intent(KEY.INTENT.ACTIONFORIGNORE));
				finish();
				break;
			case R.id.btn_ok:
				this.sendBroadcast(new Intent(KEY.INTENT.ACTIONFORIGNORE));
				finish();
				break;
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	private void initTimeForCharge(long time_period, View view) {
		if (time_period < 60) {
			if (view instanceof TextView) {
				((TextView) view).setText(time_period + "秒");
			}
			return;
		}
		int MM = (int) time_period / 60; // 共计分钟数
		if (MM < 60) {
			if (MM < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText("0" + MM + "分");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(MM + "分");
				}
			}
		} else {
			int hh = (int) MM / 60; // 共计小时数
			int min = (int) MM % 60; // 截取分钟
			if (min < 10) {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + "0" + min + "分" + "");
				}
			} else {
				if (view instanceof TextView) {
					((TextView) view).setText(hh + "小时" + min + "分" + "");
				}
			}

		}
	}
}
