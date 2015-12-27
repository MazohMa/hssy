package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.xpg.hssychargingpole.R;

public class DianzhuangkuaijiefabuView extends LinearLayout {
	private Context context;

	public DianzhuangkuaijiefabuView(Context context) {
		super(context);
		this.context = context;
		init();
		initLinstener();
	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.pile_display_quick_layout, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);

		addView(view, params);

	}

	private void initLinstener() {

	}
}
