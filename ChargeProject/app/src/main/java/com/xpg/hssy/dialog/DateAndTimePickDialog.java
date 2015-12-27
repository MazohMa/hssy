package com.xpg.hssy.dialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年06月16日
 * @version 2.0.0
 */

public class DateAndTimePickDialog extends Dialog implements OnClickListener,
		OnWheelChangedListener {

	private static final int START_YEAR = 2015;
	private int MINUTE_STEP = 1;

	public static interface OnChangedListener {
		void onChanged(DateAndTimePickDialog tpd, int newYear, int newMonth,
				int newDay, int newHour, int newMinute, int newSecond,
				int oldYear, int oldMonth, int oldDay, int oldHour,
				int oldMinute, int oldSecond);
	}

	public static interface OnOkListener {
		void onOk(DateAndTimePickDialog tpd, int year, int month, int day,
				int hour, int minute, int second);
	}

	private List<String> minutes;
	private ArrayWheelAdapter<String> awaMin;
	private List<String> secs;
	private ArrayWheelAdapter<String> awaSec;
	private NumericWheelAdapter nwaHour;
	private WheelView wv_hour;
	private WheelView wv_min;
	private WheelView wv_sec;

	private WheelView wv_year;
	private WheelView wv_month;
	private WheelView wv_day;
	private NumericWheelAdapter nwaYear;
	private NumericWheelAdapter nwaMonth;
	private NumericWheelAdapter nwaDay28;
	private NumericWheelAdapter nwaDay29;
	private NumericWheelAdapter nwaDay30;
	private NumericWheelAdapter nwaDay31;
	private OnChangedListener onChangedListener;
	private OnOkListener onOkListener;
	private android.view.View.OnClickListener onCancelListener;
	private Button btn_cancel;
	private Button btn_ok;

	public DateAndTimePickDialog(Context context) {
		super(context, R.style.DialogQrCodeStyle);
		init(context);
		setTime(System.currentTimeMillis());
	}

	private void init(Context context) {
		
		View view = LayoutInflater.from(context).inflate(
				R.layout.pile_current_time_proofread_dialog_layout, null);
		LayoutParams viewLayoutParams = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, getWindow()
						.getWindowManager().getDefaultDisplay().getHeight() / 2);
		viewLayoutParams.gravity = Gravity.BOTTOM;
		view.setLayoutParams(viewLayoutParams);
		this.addContentView(view, new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.setCanceledOnTouchOutside(false);
		setContentView(view);
		
		
		
		nwaYear = new NumericWheelAdapter(context, START_YEAR, 2100, "%02d");
		nwaMonth = new NumericWheelAdapter(context, 1, 12, "%02d");
		nwaDay28 = new NumericWheelAdapter(context, 1, 28, "%02d");
		nwaDay29 = new NumericWheelAdapter(context, 1, 29, "%02d");
		nwaDay30 = new NumericWheelAdapter(context, 1, 30, "%02d");
		nwaDay31 = new NumericWheelAdapter(context, 1, 31, "%02d");
		nwaYear.setItemResource(R.layout.wheel_text_item_small);
		nwaMonth.setItemResource(R.layout.wheel_text_item_small);
		nwaDay28.setItemResource(R.layout.wheel_text_item_small);
		nwaDay29.setItemResource(R.layout.wheel_text_item_small);
		nwaDay30.setItemResource(R.layout.wheel_text_item_small);
		nwaDay31.setItemResource(R.layout.wheel_text_item_small);
		nwaYear.setItemTextResource(R.id.tv1);
		nwaMonth.setItemTextResource(R.id.tv1);
		nwaDay28.setItemTextResource(R.id.tv1);
		nwaDay29.setItemTextResource(R.id.tv1);
		nwaDay30.setItemTextResource(R.id.tv1);
		nwaDay31.setItemTextResource(R.id.tv1);
		nwaYear.setTextColor(context.getResources()
				.getColor(R.color.water_blue));
		nwaMonth.setTextColor(context.getResources().getColor(
				R.color.water_blue));
		nwaDay28.setTextColor(context.getResources().getColor(
				R.color.water_blue));
		nwaDay29.setTextColor(context.getResources().getColor(
				R.color.water_blue));
		nwaDay30.setTextColor(context.getResources().getColor(
				R.color.water_blue));
		nwaDay31.setTextColor(context.getResources().getColor(
				R.color.water_blue));
		nwaYear.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		nwaMonth.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		nwaDay28.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		nwaDay29.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		nwaDay30.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		nwaDay31.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));

		wv_year = (WheelView) findViewById(R.id.wv_year);
		wv_month = (WheelView) findViewById(R.id.wv_month);
		wv_day = (WheelView) findViewById(R.id.wv_day);
		wv_year.setViewAdapter(nwaYear);
		wv_month.setViewAdapter(nwaMonth);
		// wv_day.setViewAdapter(nwaDay31);
		updateDayAdapter();
		wv_year.setVisibleItems(5);
		wv_month.setVisibleItems(5);
		wv_day.setVisibleItems(5);
		wv_year.setBackgroundVisible(false);
		wv_month.setBackgroundVisible(false);
		wv_day.setBackgroundVisible(false);
		wv_year.setForegroundVisible(false);
		wv_month.setForegroundVisible(false);
		wv_day.setForegroundVisible(false);
		wv_year.setDrawShadows(false);
		wv_month.setDrawShadows(false);
		wv_day.setDrawShadows(false);
		wv_year.setCyclic(true);
		wv_month.setCyclic(true);
		wv_day.setCyclic(true);
		wv_year.addChangingListener(this);
		wv_month.addChangingListener(this);
		wv_day.addChangingListener(this);

		nwaHour = new NumericWheelAdapter(context, 0, 23, "%02d");
		minutes = new ArrayList<String>();
		secs = new ArrayList<String>();
		for (int i = 0; i < 60; i += MINUTE_STEP) {
			minutes.add((i < 10 ? "0" : "") + i);
		}

		for (int i = 1; i < 60; i += MINUTE_STEP) {
			secs.add((i < 10 ? "0" : "") + i);
		}
		awaMin = new ArrayWheelAdapter<String>(context,
				minutes.toArray(new String[minutes.size()]));
		awaSec = new ArrayWheelAdapter<String>(context,
				secs.toArray(new String[secs.size()]));

		nwaHour.setItemResource(R.layout.wheel_text_item_small);
		awaMin.setItemResource(R.layout.wheel_text_item_small);
		awaSec.setItemResource(R.layout.wheel_text_item_small);

		nwaHour.setItemTextResource(R.id.tv1);
		awaMin.setItemTextResource(R.id.tv1);
		awaSec.setItemTextResource(R.id.tv1);

		nwaHour.setTextColor(context.getResources()
				.getColor(R.color.water_blue));
		awaMin.setTextColor(context.getResources().getColor(R.color.water_blue));
		awaSec.setTextColor(context.getResources().getColor(R.color.water_blue));

		nwaHour.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		awaMin.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		awaSec.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));

		wv_hour = (WheelView) findViewById(R.id.wv_hour);
		wv_min = (WheelView) findViewById(R.id.wv_min);
		wv_sec = (WheelView) findViewById(R.id.wv_sec);

		wv_hour.setViewAdapter(nwaHour);
		wv_min.setViewAdapter(awaMin);
		wv_sec.setViewAdapter(awaSec);

		wv_hour.setVisibleItems(5);
		wv_min.setVisibleItems(5);
		wv_sec.setVisibleItems(5);

		wv_hour.setBackgroundVisible(false);
		wv_min.setBackgroundVisible(false);
		wv_sec.setBackgroundVisible(false);

		wv_hour.setForegroundVisible(false);
		wv_min.setForegroundVisible(false);
		wv_sec.setForegroundVisible(false);

		wv_hour.setDrawShadows(false);
		wv_min.setDrawShadows(false);
		wv_sec.setDrawShadows(false);

		wv_hour.setCyclic(true);
		wv_min.setCyclic(true);
		wv_sec.setCyclic(true);

		wv_hour.addChangingListener(this);
		wv_min.addChangingListener(this);
		wv_sec.addChangingListener(this);

		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {

		if (wheel == wv_year) {
			updateDayAdapter();
			if (onChangedListener != null) {
				onChangedListener.onChanged(this, newValue + START_YEAR,
						getMonth(), getDay(), getHour(), getMin(), getSec(),
						oldValue + START_YEAR, getMonth(), getDay(), getHour(),
						getMin(), getSec());
			}
			return;
		}
		if (wheel == wv_month) {
			updateDayAdapter();
			if (onChangedListener != null) {
				onChangedListener.onChanged(this, getYear(), newValue + 1,
						getDay(), getHour(), getMin(), getSec(), getYear(),
						oldValue + 1, getDay(), getHour(), getMin(), getSec());
			}
			return;
		}
		if (wheel == wv_day) {
			if (onChangedListener != null) {
				onChangedListener
						.onChanged(this, getYear(), getMonth(), newValue + 1,
								getHour(), getMin(), getSec(), getYear(),
								getMonth(), oldValue + 1, getHour(), getMin(),
								getSec());
			}
			return;
		}
		if (wheel == wv_hour) {
			if (onChangedListener != null) {
				onChangedListener.onChanged(this, getYear(), getMonth(),
						getDay(), newValue, getMin(), getSec(), getYear(),
						getMonth(), getDay(), oldValue, getMin(), getSec());

			}
			return;
		}
		if (wheel == wv_min) {
			if (onChangedListener != null) {
				onChangedListener.onChanged(this, getYear(), getMonth(),
						getDay(), getHour(), newValue, getSec(), getYear(),
						getMonth(), getDay(), getHour(), oldValue, getSec());
			}
			return;
		}
		if (wheel == wv_sec) {
			if (onChangedListener != null) {
				onChangedListener.onChanged(this, getYear(), getMonth(),
						getDay(), getHour(), getMin(), newValue, getYear(),
						getMonth(), getDay(), getHour(), getMin(), oldValue);
			}
			return;
		}
	}

	private void updateDayAdapter() {
		Calendar c = Calendar.getInstance();
		c.set(getYear(), getMonth() - 1, 1);
		int oldDayIndex = wv_day.getCurrentItem();
		int maxDayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		switch (maxDayOfMonth) {
		case 28:
			wv_day.setViewAdapter(nwaDay28);
			break;
		case 29:
			wv_day.setViewAdapter(nwaDay29);
			break;
		case 30:
			wv_day.setViewAdapter(nwaDay30);
			break;
		case 31:
			wv_day.setViewAdapter(nwaDay31);
			break;
		default:
			break;
		}
		if (oldDayIndex > wv_day.getViewAdapter().getItemsCount() - 1) {
			wv_day.setCurrentItem(wv_day.getViewAdapter().getItemsCount() - 1);
		} else {
			wv_day.setCurrentItem(oldDayIndex);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			dismiss();
			if (onCancelListener != null)
				onCancelListener.onClick(v);
			break;
		case R.id.btn_ok:
			dismiss();
			if (onOkListener != null)
				onOkListener.onOk(this, getYear(), getMonth(), getDay(),
						getHour(), getMin(), getSec());
			break;

		default:
			break;
		}
	}

	public int getYear() {
		return wv_year.getCurrentItem() + START_YEAR;
	}

	public int getMonth() {
		return wv_month.getCurrentItem() + 1;
	}

	public int getDay() {
		return wv_day.getCurrentItem() + 1;
	}

	public int getHour() {
		return wv_hour.getCurrentItem();
	}

	public int getMin() {
		return getMin(wv_min.getCurrentItem());
	}

	public int getSec() {
		return getMin(wv_sec.getCurrentItem());
	}

	private int getMin(int index) {
		try {
			return Integer.parseInt(minutes.get(index));
		} catch (Exception e) {
			return -1;
		}
	}

	public void setTime(int year, int month, int day) {
		wv_year.setCurrentItem(year >= START_YEAR ? year - START_YEAR
				: START_YEAR);
		wv_month.setCurrentItem(month - 1);
		updateDayAdapter();
		wv_day.setCurrentItem(day - 1);
	}

	public void setTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
				c.get(Calendar.DAY_OF_MONTH));
	}

	public OnChangedListener getOnChangedListener() {
		return onChangedListener;
	}

	public void setOnChangedListener(OnChangedListener onChangedListener) {
		this.onChangedListener = onChangedListener;
	}

	public OnOkListener getOnOkListener() {
		return onOkListener;
	}

	public void setOnOkListener(OnOkListener onOkListener) {
		this.onOkListener = onOkListener;
	}

	public android.view.View.OnClickListener getOnCancelListener() {
		return onCancelListener;
	}

	public void setOnCancelListener(
			android.view.View.OnClickListener onCancelListener) {
		this.onCancelListener = onCancelListener;
	}

	public void setLeftBtnText(String text) {
		this.btn_cancel.setText(text);
	}
}
