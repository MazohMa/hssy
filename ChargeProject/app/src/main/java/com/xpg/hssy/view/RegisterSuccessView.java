package com.xpg.hssy.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.easy.manager.EasyActivityManager;
import com.xpg.hssy.main.activity.MainActivity;
import com.xpg.hssychargingpole.R;

public class RegisterSuccessView extends LinearLayout {
	private Context context;
	private Button btn_charge;

	public RegisterSuccessView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.register_success, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		addView(view, params);
		btn_charge = (Button) view.findViewById(R.id.btn_charge);
	}

	private void initLinstener() {
		btn_charge.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EasyActivityManager.getInstance().finishAll();
				Intent intent = new Intent(context, MainActivity.class);
				context.startActivity(intent);

			}
		});
	}

}
