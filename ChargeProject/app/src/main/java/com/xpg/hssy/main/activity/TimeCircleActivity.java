package com.xpg.hssy.main.activity;

import java.util.Calendar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.easy.util.BitmapUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.view.TimeCircleListener;
import com.xpg.hssy.view.TimeCircleView;
import com.xpg.hssychargingpole.R;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年5月14日
 * @version 1.0.0
 */

public class TimeCircleActivity extends BaseActivity {

	private int year;
	private TimeCircleView tcv;
	private TextView tv_duration;
	private SeekBar sb_duration;
	private int maxDuraion;
	private int minDuraion;

	@Override
	protected void initData() {
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.activity_time_circle);
		setTitle("预约充电");

		tcv = (TimeCircleView) findViewById(R.id.tcv);
		tv_duration = (TextView) findViewById(R.id.tv_duration);
		sb_duration = (SeekBar) findViewById(R.id.sb_duration);

		Bitmap bmp = BitmapUtil.get(this, R.drawable.zhao_bg1);
		findViewById(R.id.bg).setBackground(
				new BitmapDrawable(getResources(), bmp));

		boolean showTime = getIntent().getBooleanExtra(KEY.INTENT.SHOW_TIME,
				false);
		long dateTiem = getIntent().getLongExtra(KEY.INTENT.DATE_TIME, -1);
		long startTime = getIntent().getLongExtra(KEY.INTENT.START_TIME, -1);
		long endTime = getIntent().getLongExtra(KEY.INTENT.END_TIME, -1);
		double duration = getIntent().getDoubleExtra(KEY.INTENT.DURATION, -1);
		Calendar dateC = Calendar.getInstance();
		dateC.setTimeInMillis(dateTiem);
		Calendar startC = Calendar.getInstance();
		startC.setTimeInMillis(startTime);
		Calendar endC = Calendar.getInstance();
		endC.setTimeInMillis(endTime);
		year = dateC.get(Calendar.YEAR);
		tcv.setDate(dateC.get(Calendar.MONTH) + 1,
				dateC.get(Calendar.DAY_OF_MONTH));
		tcv.setShowTime(showTime);
		if (startTime != -1) {
			tcv.setBeginMinutes(startC.get(Calendar.HOUR_OF_DAY) * 60
					+ startC.get(Calendar.MINUTE));
			tcv.setBeginOn();
		}
		if (endTime != -1) {
			int minutes = endC.get(Calendar.HOUR_OF_DAY) * 60
					+ endC.get(Calendar.MINUTE);
			// 如果结束时间是0点证明是第二天的0点，这里显示为24点
			if (minutes == 0) {
				minutes = 24 * 60;
			}
			tcv.setEndMinutes(minutes);
			tcv.setEndOn();
		}
		if (duration != -1) {
			sb_duration.setProgress((int) (duration * 2));
		}
		updateDuration();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		findViewById(R.id.btn_reset).setOnClickListener(this);
		findViewById(R.id.btn_ok).setOnClickListener(this);
		tcv.setListener(new TimeCircleListener() {
			@Override
			public void onStart(int beginMinutes, int endMinutes) {
			}

			@Override
			public void onFinish(int beginMinutes, int endMinutes) {
				updateDuration();
			}

			@Override
			public void onChanged(int beginMinutes, int endMinutes) {
			}
		});
		sb_duration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekbar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress,
					boolean isFromUser) {
				if (isFromUser) {
					tcv.setBeginOn();
					updateDuration();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_reset:
			tcv.reset();
			sb_duration.setProgress(0);
			updateDuration();
			break;
		case R.id.btn_ok:
			Intent data = new Intent();
			Calendar dateC = Calendar.getInstance();
			dateC.set(Calendar.YEAR, year);
			dateC.set(Calendar.MONTH, tcv.getMonth() - 1);
			dateC.set(Calendar.DAY_OF_MONTH, tcv.getDay());
			Calendar startC = Calendar.getInstance();
			startC.set(Calendar.YEAR, year);
			startC.set(Calendar.MONTH, tcv.getMonth() - 1);
			startC.set(Calendar.DAY_OF_MONTH, tcv.getDay());
			startC.set(Calendar.HOUR_OF_DAY, tcv.getBeginMinutes() / 60);
			startC.set(Calendar.MINUTE, tcv.getBeginMinutes() % 60);
			startC.set(Calendar.SECOND, 0);
			startC.set(Calendar.MILLISECOND, 0);
			Calendar endC = Calendar.getInstance();
			endC.set(Calendar.YEAR, year);
			endC.set(Calendar.MONTH, tcv.getMonth() - 1);
			endC.set(Calendar.DAY_OF_MONTH, tcv.getDay());
			// 如果是24点，则加一天
			if (tcv.getEndMinutes() == 24 * 60) {
				endC.add(Calendar.DATE, 1);
				endC.set(Calendar.HOUR_OF_DAY, 0);
			} else {
				endC.set(Calendar.HOUR_OF_DAY, tcv.getEndMinutes() / 60);
			}
			endC.set(Calendar.MINUTE, tcv.getEndMinutes() % 60);
			endC.set(Calendar.SECOND, 0);
			endC.set(Calendar.MILLISECOND, 0);
			double duration = sb_duration.getProgress() / 2f;
			if (tcv.isBeginOn()) {
				data.putExtra(KEY.INTENT.START_TIME, startC.getTimeInMillis());
			}
			if (tcv.isEndOn()) {
				data.putExtra(KEY.INTENT.END_TIME, endC.getTimeInMillis());
			}
			if (duration != 0) {
				data.putExtra(KEY.INTENT.DURATION, duration);
			}
			setResult(RESULT_OK, data);
			finish();
			break;

		default:
			break;
		}
	}

	private void updateDuration() {
		if (tcv.isEndOn()) {
			minDuraion = 1;
			maxDuraion = (tcv.getEndMinutes() - tcv.getBeginMinutes()) / 30;
		} else {
			minDuraion = 0;
			maxDuraion = sb_duration.getMax();
		}
		if (!tcv.isBeginOn()) {
			sb_duration.setProgress(0);
		} else if (sb_duration.getProgress() > maxDuraion) {
			sb_duration.setProgress(maxDuraion);
		} else if (sb_duration.getProgress() < minDuraion) {
			sb_duration.setProgress(minDuraion);
		}
		int hour = sb_duration.getProgress() / 2;
		int min = (int) (sb_duration.getProgress() % 2f * 30);
		tv_duration.setText(hour + "h" + (min < 10 ? "0" : "") + min + "m");
	}
}
