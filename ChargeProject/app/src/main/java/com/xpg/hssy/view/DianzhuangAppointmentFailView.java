package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.easy.util.ToastUtil;
import com.xpg.hssychargingpole.R;

public class DianzhuangAppointmentFailView extends LinearLayout {
	private Context context;
	private Button btn_dianzhuangappoint_fail;

	public DianzhuangAppointmentFailView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.dianzhuangappointment_fail, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		addView(view, params);
		btn_dianzhuangappoint_fail = (Button) view
				.findViewById(R.id.btn_dianzhuangappoint_fail);
	}

	private void initLinstener() {
		// TODO Auto-generated method stub
		btn_dianzhuangappoint_fail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*
				 * Intent intent = new Intent(context, MainActivity.class);
				 * context.startActivity(intent); ((Activity) context).finish();
				 */
				ToastUtil.show(context, "订单预约失败，重新提交订单");
			}
		});
	}

}
