package com.xpg.hssy.main.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easy.util.EmptyUtil;
import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

/**
 * AddTimeActivity
 *
 * @author Mazoh
 * @version 2.0.0
 * @Description
 * @createDate 2015年05月16日
 */
public class AddTimeActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, OnWheelChangedListener {
	private static final int MINUTE_STEP = 30;

	private LinearLayout start_time_layout_id, overtime_layout_id, ll_weeks;
	private TextView time_begins;
	private TextView time_ends;
	private List<View> dividers = new ArrayList<View>();
	private List<CheckBox> cbs = new ArrayList<CheckBox>();
	private WheelView wv_hour;
	private WheelView wv_min;
	private WheelView wv_hour2;
	private WheelView wv_min2;
	private NumericWheelAdapter nwaHour;
	private NumericWheelAdapter nwaHour2;
	private ArrayWheelAdapter<String> awaMin;
	private ArrayWheelAdapter<String> awaMin2;
	private ArrayWheelAdapter<String> awaMin00;
	private List<String> minutes;
	private List<String> minutes00;
	// private List<String> minutes2;
	private String beginTime;
	private String overTime;

	private ArrayList<Integer> shareWeekDate;// 分享的星期天

	public static final String INTENT_KEY_WEEKS = "weeks";
	public static final String INTENT_KEY_BEGIN_TIME = "beginTime";
	public static final String INTENT_KEY_END_TIME = "overTime";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long beginTime = getIntent().getLongExtra(INTENT_KEY_BEGIN_TIME, -1);
		long endTime = getIntent().getLongExtra(INTENT_KEY_END_TIME, -1);
		if (beginTime == -1 || endTime == -1) {
			setBeginTime(System.currentTimeMillis());
			setEndTime(System.currentTimeMillis());
		} else {
			setBeginTime(beginTime);
			setEndTime(endTime);
		}
		shareWeekDate = getIntent().getIntegerArrayListExtra(INTENT_KEY_WEEKS);
		if (shareWeekDate != null) {
			for (Integer week : shareWeekDate) {
				cbs.get(week - 1).setChecked(true);
			}
		}
	}

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.addtime_layout);
		start_time_layout_id = (LinearLayout) findViewById(R.id.start_time_layout_id);
		overtime_layout_id = (LinearLayout) findViewById(R.id.overtime_layout_id);
		ll_weeks = (LinearLayout) findViewById(R.id.ll_weeks);
		time_begins = (TextView) findViewById(R.id.time_begins);
		time_ends = (TextView) findViewById(R.id.time_ends);

		minutes00 = new ArrayList<String>();
		minutes00.add("00");
		awaMin00 = new ArrayWheelAdapter<String>(this, minutes00.toArray(new String[minutes00.size
				()]));
		awaMin00.setItemResource(R.layout.wheel_text_item);
		awaMin00.setItemTextResource(R.id.tv1);
		awaMin00.setTextColor(this.getResources().getColor(R.color.water_blue));
		awaMin00.setTextColorUnselect(this.getResources().getColor(R.color.gray));

		minutes = new ArrayList<String>();
		for (int i = 0; i < 60; i += MINUTE_STEP) {
			minutes.add((i < 10 ? "0" : "") + i);
		}

		nwaHour = new NumericWheelAdapter(this, 0, 23, "%02d");
		awaMin = new ArrayWheelAdapter<String>(this, minutes.toArray(new String[minutes.size()]));

		nwaHour.setItemResource(R.layout.wheel_text_item);
		awaMin.setItemResource(R.layout.wheel_text_item);
		nwaHour.setItemTextResource(R.id.tv1);
		awaMin.setItemTextResource(R.id.tv1);
		nwaHour.setTextColor(this.getResources().getColor(R.color.water_blue));
		awaMin.setTextColor(this.getResources().getColor(R.color.water_blue));
		nwaHour.setTextColorUnselect(this.getResources().getColor(R.color.gray));
		awaMin.setTextColorUnselect(this.getResources().getColor(R.color.gray));

		wv_hour = (WheelView) findViewById(R.id.wv_hour);
		wv_min = (WheelView) findViewById(R.id.wv_min);
		wv_hour.setViewAdapter(nwaHour);
		wv_min.setViewAdapter(awaMin);
		wv_hour.setVisibleItems(5);
		wv_min.setVisibleItems(5);
		wv_hour.setBackgroundVisible(false);
		wv_min.setBackgroundVisible(false);
		wv_hour.setForegroundVisible(false);
		wv_min.setForegroundVisible(false);
		wv_hour.setDrawShadows(false);
		wv_min.setDrawShadows(false);
		wv_hour.setCyclic(false);
		wv_min.setCyclic(false);
		wv_hour.addChangingListener(this);
		wv_min.addChangingListener(this);

		nwaHour2 = new NumericWheelAdapter(this, 0, 24, "%02d");
		awaMin2 = new ArrayWheelAdapter<String>(this, minutes.toArray(new String[minutes.size()]));

		nwaHour2.setItemResource(R.layout.wheel_text_item);
		awaMin2.setItemResource(R.layout.wheel_text_item);
		nwaHour2.setItemTextResource(R.id.tv1);
		awaMin2.setItemTextResource(R.id.tv1);
		nwaHour2.setTextColor(this.getResources().getColor(R.color.water_blue));
		awaMin2.setTextColor(this.getResources().getColor(R.color.water_blue));
		nwaHour2.setTextColorUnselect(this.getResources().getColor(R.color.gray));
		awaMin2.setTextColorUnselect(this.getResources().getColor(R.color.gray));

		wv_hour2 = (WheelView) findViewById(R.id.wv_hour2);
		wv_min2 = (WheelView) findViewById(R.id.wv_min2);
		wv_hour2.setViewAdapter(nwaHour2);
		wv_min2.setViewAdapter(awaMin2);
		wv_hour2.setVisibleItems(5);
		wv_min2.setVisibleItems(5);
		wv_hour2.setBackgroundVisible(false);
		wv_min2.setBackgroundVisible(false);
		wv_hour2.setForegroundVisible(false);
		wv_min2.setForegroundVisible(false);
		wv_hour2.setDrawShadows(false);
		wv_min2.setDrawShadows(false);
		wv_hour2.setCyclic(false);
		wv_min2.setCyclic(false);
		wv_hour2.addChangingListener(this);
		wv_min2.addChangingListener(this);

		for (int i = 0; i < ll_weeks.getChildCount(); i += 2) {
			cbs.add((CheckBox) ll_weeks.getChildAt(i));
		}
		for (int i = 1; i < ll_weeks.getChildCount(); i += 2) {
			dividers.add(ll_weeks.getChildAt(i));
		}
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		start_time_layout_id.setOnClickListener(this);
		overtime_layout_id.setOnClickListener(this);
		for (CheckBox cb : cbs) {
			cb.setOnCheckedChangeListener(this);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		for (int i = 0; i < cbs.size() - 1; i++) {
			if (cbs.get(i).isChecked() && cbs.get(i + 1).isChecked()) {
				dividers.get(i).setVisibility(View.INVISIBLE);
			} else {
				dividers.get(i).setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);

	}

	public int getHour(WheelView wheel) {
		if (wheel == wv_hour) {
			return wv_hour.getCurrentItem();
		} else {
			return wv_hour2.getCurrentItem();
		}
	}

	public int getMin(WheelView wheel) {
		if (wheel == wv_min) {
			return getMin(wv_min.getCurrentItem());
		} else {
			return getMin(wv_min2.getCurrentItem());
		}
	}

	private int getMin(int index) {
		try {
			return Integer.parseInt(minutes.get(index));
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	protected void onRightBtn(View v) {
		super.onRightBtn(v);
		// String beginStr = time_begins.getText().toString();
		// String endStr = time_ends.getText().toString();
		Intent _intent = new Intent();
		_intent.putExtra("time_begins", beginTime);
		_intent.putExtra("time_ends", overTime);
		List<Integer> weeks = new ArrayList<Integer>();
		for (int i = 0; i < cbs.size(); i++) {
			if (cbs.get(i).isChecked()) {
				weeks.add(i + 1);
			}
		}
		if (weeks.size() == 0) {
			ToastUtil.show(self, "请选择星期");
			return;
		}
		if (EmptyUtil.isEmpty(beginTime) || EmptyUtil.isEmpty(overTime)) {
			ToastUtil.show(self, "请选择时间");
			return;
		}
		Date beginTimes = TimeUtil.parse(beginTime, "HH:mm");
		Date endTimes = TimeUtil.parse(overTime, "HH:mm");
		if (!beginTimes.before(endTimes)) {
			ToastUtil.show(self, "开始时间要比结束时间早");
			return;
		}
		_intent.putExtra(INTENT_KEY_WEEKS, (Serializable) weeks);
		setResult(Activity.RESULT_OK, _intent);
		finish();
	}

	@Override
	protected void onLeftBtn(View v) {
		finish();
	}

	public void setBeginTime(int hour, int min) {
		beginTime = getTimeString(hour,min);
		wv_hour.setCurrentItem(hour);
		wv_min.setCurrentItem(minutes.indexOf((min < 10 ? "0" : "") + min));
	}

	public void setBeginTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setBeginTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	public void setEndTime(int hour, int min) {
		overTime = getTimeString(hour,min);
		wv_hour2.setCurrentItem(hour);
		wv_min2.setCurrentItem(minutes.indexOf((min < 10 ? "0" : "") + min));
	}

	public void setEndTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setEndTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {

		if (wheel == wv_hour) {
			int beginCurrentWv_hour = newValue;
			int beginCurrentWv_Minute = getMin(wv_min);
			wv_hour.setCurrentItem(beginCurrentWv_hour);
			beginTime = getTimeString(beginCurrentWv_hour,beginCurrentWv_Minute);
			Log.i("tag", "beginTime:" + beginTime);
			return;
		}
		if (wheel == wv_min) {
			int beginCurrentWv_hour = getHour(wv_hour);
			int beginCurrentWv_Minute = getMin(newValue);
			wv_min.setCurrentItem(newValue);

			beginTime = getTimeString(beginCurrentWv_hour,beginCurrentWv_Minute);
			Log.i("tag", "beginTime:" + beginTime);

			return;
		}
		if (wheel == wv_hour2) {
			// 结束时间是存在24点的，当选择24点的时候，分钟只有00选择
			if (newValue == 24) {
				if (wv_min2.getViewAdapter() != awaMin00) {
					wv_min2.setViewAdapter(awaMin00);
					wv_min2.setCurrentItem(0);
				}
			} else {
				if (wv_min2.getViewAdapter() != awaMin2) wv_min2.setViewAdapter(awaMin2);
			}

			int overCurrentWv_hour = newValue;
			int overCurrentWv_Minute = getMin(wv_min2);
			wv_hour2.setCurrentItem(overCurrentWv_hour);

			overTime = getTimeString(overCurrentWv_hour,overCurrentWv_Minute);
			Log.i("tag", "overTime:" + overTime);

			return;
		}
		if (wheel == wv_min2) {
			int overCurrentWv_hour = getHour(wv_hour2);
			int overCurrentWv_Minute = getMin(newValue);
			wv_min2.setCurrentItem(newValue);

			overTime = getTimeString(overCurrentWv_hour,overCurrentWv_Minute);
			Log.i("tag", "overTime:" + overTime);

			return;
		}
	}


	private String getTimeString(int hour, int min) {
		return (hour < 10 ? "0" + hour : "" + hour) + ":" + (min < 10 ? "0" + min : "" + min);
	}
}
