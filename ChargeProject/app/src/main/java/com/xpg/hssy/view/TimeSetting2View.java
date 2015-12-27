package com.xpg.hssy.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xpg.hssy.db.pojo.ShareTime;
import com.xpg.hssychargingpole.R;

public class TimeSetting2View extends LinearLayout {
	private Context context;
	private ShareTime shareTime;
	private TextView tv_share_time1;
	private TextView tv_share_weekly1;

	public TimeSetting2View(Context context, ShareTime shareTime) {
		super(context);
		this.context = context;
		this.shareTime = shareTime;
		init(false);

	}

	public TimeSetting2View(Context context, ShareTime shareTime,
			boolean isConflict) {
		super(context);
		this.context = context;
		this.shareTime = shareTime;
		init(isConflict);

	}

	private void init(boolean isConflict) {
		View view = LayoutInflater.from(context).inflate(R.layout.timelayout,
				null);
		tv_share_time1 = (TextView) view.findViewById(R.id.tv_share_time1);
		tv_share_weekly1 = (TextView) view.findViewById(R.id.tv_share_weekly1);

		if (isConflict) {
			tv_share_time1.setText("! " + shareTime.getStartTimeAsString()
					+ "-" + shareTime.getEndTimeAsString());
			tv_share_time1.setTextColor(getResources().getColor(R.color.red));
			tv_share_weekly1.setText(shareTime.getWeeks2String());
			tv_share_weekly1.setTextColor(getResources().getColor(R.color.red));
		} else {
			tv_share_time1.setText(shareTime.getStartTimeAsString() + "-"
					+ shareTime.getEndTimeAsString());
			tv_share_weekly1.setText(shareTime.getWeeks2String());
		}
		addView(view);

	}

	public ShareTime getShareTime() {
		return shareTime;
	}

	public void setShareTime(ShareTime shareTime) {
		this.shareTime = shareTime;
	}
}
