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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xpg.hssychargingpole.R;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月6日
 * @version 1.0.0
 */

public class TimePickDialog extends Dialog implements OnClickListener,
		OnWheelChangedListener {
	private int MINUTE_STEP = 1;

	public static interface OnChangedListener {
		void onChanged(TimePickDialog tpd, int newHour, int newMin,
				int oldHour, int oldMin);
	}

	public static interface OnOkListener {
		void onOk(TimePickDialog tpd, int hour, int min);
	}

	private TextView tv_title;
	private WheelView wv_hour;
	private WheelView wv_min;
	private ArrayWheelAdapter<String> awaMin;
	private List<String> minutes;
	private NumericWheelAdapter nwaHour;
	private OnChangedListener onChangedListener;
	private OnOkListener onOkListener;
	private android.view.View.OnClickListener onCancelListener;
	private Button btn_cancel;
	private Button btn_ok;
	private Window window = null;
	private ImageView cancel;

	public TimePickDialog(Context context) {
		super(context, R.style.dialog_no_frame);
		init(context);
		setTime(System.currentTimeMillis());
	}

	public TimePickDialog(Context context, int minuteStep) {
		this(context);
		this.MINUTE_STEP = minuteStep;
	}

	public void showDialog() {
		window = getWindow(); // 得到对话框
		window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
		this.show();
	}

	private void init(Context context) {
		setContentView(R.layout.dialog_time_picker2);

		tv_title = (TextView) findViewById(R.id.tv_title);
		cancel = (ImageView) findViewById(R.id.cancel);
		nwaHour = new NumericWheelAdapter(context, 0, 23, "%02d");
		minutes = new ArrayList<String>();
		for (int i = 0; i < 60; i += MINUTE_STEP) {
			minutes.add((i < 10 ? "0" : "") + i);
		}
		awaMin = new ArrayWheelAdapter<String>(context,
				minutes.toArray(new String[minutes.size()]));
		nwaHour.setItemResource(R.layout.wheel_text_item);
		awaMin.setItemResource(R.layout.wheel_text_item);
		nwaHour.setItemTextResource(R.id.tv1);
		awaMin.setItemTextResource(R.id.tv1);
		nwaHour.setTextColor(context.getResources()
				.getColor(R.color.water_blue));
		awaMin.setTextColor(context.getResources().getColor(R.color.water_blue));
		nwaHour.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));
		awaMin.setTextColorUnselect(context.getResources().getColor(
				R.color.text_gray));

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
		wv_hour.setCyclic(true);
		wv_min.setCyclic(true);
		wv_hour.addChangingListener(this);
		wv_min.addChangingListener(this);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (onChangedListener == null)
			return;

		if (wheel == wv_hour) {
			onChangedListener.onChanged(this, newValue, getMin(), oldValue,
					getMin());
			return;
		}
		if (wheel == wv_min) {
			onChangedListener.onChanged(this, getHour(), getMin(newValue),
					getHour(), getMin(oldValue));
			return;
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
		case R.id.cancel:
			dismiss();
			if (onCancelListener != null)
				onCancelListener.onClick(v);
			break;
		case R.id.btn_ok:
			dismiss();
			if (onOkListener != null)
				onOkListener.onOk(this, getHour(), getMin());
			break;

		default:
			break;
		}
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		tv_title.setText(titleId);
	}

	public int getHour() {
		return wv_hour.getCurrentItem();
	}

	public int getMin() {
		return getMin(wv_min.getCurrentItem());
	}

	private int getMin(int index) {
		try {
			return Integer.parseInt(minutes.get(index));
		} catch (Exception e) {
			return -1;
		}
	}

	public void setTime(int hour, int min) {
		wv_hour.setCurrentItem(hour);
		wv_min.setCurrentItem(minutes.indexOf((min < 10 ? "0" : "") + min));
	}

	public void setTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
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
