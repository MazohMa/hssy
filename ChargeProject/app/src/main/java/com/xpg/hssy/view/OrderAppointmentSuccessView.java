package com.xpg.hssy.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.xpg.hssy.main.activity.MyOrderFragmentActivity;
import com.xpg.hssychargingpole.R;

public class OrderAppointmentSuccessView extends LinearLayout {
	private Context context;
	private Button btn_order_success;

	public OrderAppointmentSuccessView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.orderappointment_success, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		addView(view, params);
		btn_order_success = (Button) view.findViewById(R.id.btn_order_success);
	}

	private void initLinstener() {
		btn_order_success.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myorder_intent = new Intent(context,
						MyOrderFragmentActivity.class);
				context.startActivity(myorder_intent);
			}
		});
	}

}
