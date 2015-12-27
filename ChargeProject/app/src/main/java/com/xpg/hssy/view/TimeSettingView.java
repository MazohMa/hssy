package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssychargingpole.R;

public class TimeSettingView extends LinearLayout {

	private Context context;

	private ShareTime shareTime;
	private TextView tv_share_time1;
	private TextView tv_share_weekly1;

	public TimeSettingView(Context context) {
		super(context);
		this.context = context;
		init();

	}

	private void init() {
		View view = LayoutInflater.from(context).inflate(R.layout.time_setting,
				null);
		tv_share_time1 = (TextView) view.findViewById(R.id.tv_share_time1);
		tv_share_weekly1 = (TextView) view.findViewById(R.id.tv_share_weekly1);
		addView(view);

	}

	public void set(ShareTime shareTime) {
		this.shareTime = shareTime;
		tv_share_time1.setText(shareTime.getStartTimeAsString() + "-"
				+ shareTime.getEndTimeAsString());
		tv_share_weekly1.setText(shareTime.getWeeks2String());
	}

}
