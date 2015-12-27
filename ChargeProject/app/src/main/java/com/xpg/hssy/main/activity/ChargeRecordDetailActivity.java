package com.xpg.hssy.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.db.pojo.ChargeRecord;
import com.xpg.hssy.view.EvaluateColumn;
import com.xpg.hssychargingpole.R;

/**
 * ChargeRecordDetailActivity
 *
 * @author Mazoh 充电记录信息详情
 */
public class ChargeRecordDetailActivity extends BaseActivity {

	public static final String TAG = "ChargeRecordDetailActivity";
	private String pile_id;
	private EvaluateColumn eva_star_point;
	private RelativeLayout rl_order_id, rl_order_condition1, rl_order_condition3, rl_order_condition4, rl_order_condition5, rl_order_condition6,
			rl_order_condition7, rl_order_condition9, rl_order_condition10;

	private ChargeRecord chargeRecord;

	private TextView tv_order_id;// 订单号

	private TextView tv_order_action2; // 订单详情
	private TextView tv_username;// 付款人

	private TextView tv_contact_phone;// 联系电话
	private TextView tv_charge_time;// 充电时间
	private TextView tv_charge_electric;// 充电电量
	private TextView tv_charge_money;// 充电费用
	private TextView tv_sequence;// 充电流水号
	private TextView tv_pay_time; // 付款时间

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void updateUI() {
		tv_order_id.setText(chargeRecord.getOrderId() + "");
	}

	@Override
	protected void initData() {
		super.initData();
		Intent intent = getIntent();
		pile_id = intent.getStringExtra("pile_id");
		chargeRecord = (ChargeRecord) intent.getSerializableExtra("chargeRecord");

	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.item_charge_record_detail);
		setTitle("充电信息详情");
		eva_star_point = (EvaluateColumn) findViewById(R.id.eva_star_point);
		rl_order_id = (RelativeLayout) findViewById(R.id.rl_order_id);
		rl_order_condition1 = (RelativeLayout) findViewById(R.id.rl_order_condition1);
		rl_order_condition3 = (RelativeLayout) findViewById(R.id.rl_order_condition3);
		rl_order_condition4 = (RelativeLayout) findViewById(R.id.rl_order_condition4);
		rl_order_condition5 = (RelativeLayout) findViewById(R.id.rl_order_condition5);
		rl_order_condition6 = (RelativeLayout) findViewById(R.id.rl_order_condition6);
		rl_order_condition7 = (RelativeLayout) findViewById(R.id.rl_order_condition7);
		rl_order_condition9 = (RelativeLayout) findViewById(R.id.rl_order_condition9);
		rl_order_condition10 = (RelativeLayout) findViewById(R.id.rl_order_condition10);

		tv_order_id = (TextView) findViewById(R.id.tv_order_id);
		tv_order_action2 = (TextView) findViewById(R.id.tv_order_action2);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tv_contact_phone = (TextView) findViewById(R.id.tv_contact_phone);
		tv_charge_time = (TextView) findViewById(R.id.tv_charge_time);
		tv_charge_electric = (TextView) findViewById(R.id.tv_charge_electric);
		tv_charge_money = (TextView) findViewById(R.id.tv_charge_money);
		tv_sequence = (TextView) findViewById(R.id.tv_sequence);
		tv_pay_time = (TextView) findViewById(R.id.tv_pay_time);

	}

	@Override
	protected void initEvent() {
		super.initEvent();
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);

	}
}
