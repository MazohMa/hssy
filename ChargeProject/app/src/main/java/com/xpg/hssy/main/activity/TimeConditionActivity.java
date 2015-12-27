package com.xpg.hssy.main.activity;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.easy.util.ToastUtil;
import com.xpg.hssy.base.BaseActivity;
import com.xpg.hssy.constant.KEY;
import com.xpg.hssy.dialog.DatePickDialog;
import com.xpg.hssy.dialog.TimePickDialog;
import com.xpg.hssy.dialog.TimePickDialog.OnOkListener;
import com.xpg.hssy.util.TimeUtil;
import com.xpg.hssychargingpole.R;

public class TimeConditionActivity extends BaseActivity implements OnOkListener {

	private DatePickDialog dpd_start;
	private DatePickDialog dpd_end;
	private TimePickDialog tpd_start;
	private TimePickDialog tpd_end;

	private TextView tv_start_date;
	private TextView tv_end_date;
	private TextView tv_start_time;
	private TextView tv_end_time;

	private Button btn_unlimited;
	private Button btn_submit;

	private Calendar startC;
	private Calendar endC;

	@Override
	protected void initData() {
		Intent intent = getIntent();
		startC = Calendar.getInstance();
		endC = Calendar.getInstance();
		startC.setTimeInMillis(intent.getLongExtra(KEY.INTENT.START_TIME, 0));
		endC.setTimeInMillis(intent.getLongExtra(KEY.INTENT.END_TIME, 0));
		super.initData();
	}

	@Override
	protected void initUI() {
		super.initUI();
		setContentView(R.layout.time_condition_activity);
		setTitle("请选择时间");

		tv_start_date = (TextView) findViewById(R.id.tv_date_start);// 起始日期
		tv_end_date = (TextView) findViewById(R.id.tv_date_end);// 结束日期
		tv_start_time = (TextView) findViewById(R.id.start_time);// 起始时间
		tv_end_time = (TextView) findViewById(R.id.end_time); // 结束时间

		btn_unlimited = (Button) findViewById(R.id.btn_unlimited);

		btn_submit = (Button) findViewById(R.id.btn_submit);

		updateTimeUI();

		dpd_start = new DatePickDialog(this);
		dpd_end = new DatePickDialog(this);
		tpd_start = new TimePickDialog(this);
		tpd_end = new TimePickDialog(this);
		dpd_start.setTitle("开始日期");
		dpd_end.setTitle("结束日期");
		tpd_start.setTitle("开始时间");
		tpd_end.setTitle("结束时间");
	}

	@Override
	protected void initEvent() {
		super.initEvent();

		findViewById(R.id.rl_date_start).setOnClickListener(this);
		findViewById(R.id.rl_date_end).setOnClickListener(this);
		findViewById(R.id.rl_time_start).setOnClickListener(this);
		findViewById(R.id.rl_time_end).setOnClickListener(this);
		tpd_start.setOnOkListener(this);
		tpd_end.setOnOkListener(this);

		btn_unlimited.setOnClickListener(this);

		btn_submit.setOnClickListener(this);
		dpd_start.setOnOkListener(new DatePickDialog.OnOkListener() {
			@Override
			public void onOk(DatePickDialog tpd, int year, int month, int day) {
				startC.set(Calendar.YEAR, year);
				startC.set(Calendar.MONTH, month - 1);
				startC.set(Calendar.DAY_OF_MONTH, day);
				updateTimeUI();
			}
		});

		dpd_end.setOnOkListener(new DatePickDialog.OnOkListener() {
			@Override
			public void onOk(DatePickDialog tpd, int year, int month, int day) {
				endC.set(Calendar.YEAR, year);
				endC.set(Calendar.MONTH, month - 1);
				endC.set(Calendar.DAY_OF_MONTH, day);
				updateTimeUI();
			}
		});
	}

	/**
	 * 刷新显示的时间
	 */
	private void updateTimeUI() {
		tv_start_date.setText(formatDate(startC));
		tv_end_date.setText(formatDate(endC));
		tv_start_time.setText(formatTime(startC));
		tv_end_time.setText(formatTime(endC));
	}

	private String formatTime(Calendar c) {
		return TimeUtil.format(c.getTimeInMillis(), "HH:mm");
	}

	private String formatDate(Calendar c) {
		return TimeUtil.format(c.getTimeInMillis(), "yyyy-MM-dd");
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.rl_date_start:
			dpd_start.setTime(startC.getTimeInMillis());
			dpd_start.show();
			break;
		case R.id.rl_date_end:
			dpd_end.setTime(startC.getTimeInMillis());
			dpd_end.show();
			break;
		case R.id.rl_time_start:
			tpd_start.setTime(startC.getTimeInMillis());
			tpd_start.show();
			break;
		case R.id.rl_time_end:
			tpd_end.setTime(endC.getTimeInMillis());
			tpd_end.show();
			break;
		case R.id.btn_unlimited: {
			Intent data = new Intent();
			data.putExtra(KEY.INTENT.START_TIME, -1);
			data.putExtra(KEY.INTENT.END_TIME, -1);
			setResult(Activity.RESULT_OK, data);
			finish();
			break;
		}
		case R.id.btn_submit:
			if (isValid()) {
				submit();
			}
			break;
		}

	}

	@Override
	public void onOk(TimePickDialog tpd, int hour, int min) {
		// 重新设置开始时间和结束时间
		if (tpd == tpd_start) {
			startC.set(Calendar.HOUR_OF_DAY, hour);
			startC.set(Calendar.MINUTE, min);
			startC.set(Calendar.SECOND, 0);
		}
		// 重新设置结束时间
		if (tpd == tpd_end) {
			endC.set(Calendar.HOUR_OF_DAY, hour);
			endC.set(Calendar.MINUTE, min);
			endC.set(Calendar.SECOND, 0);
		}

		// 显示
		updateTimeUI();
	}

	private void submit() {
		Long startTime = startC.getTimeInMillis();
		Long endTime = endC.getTimeInMillis();
		Intent data = new Intent();
		data.putExtra(KEY.INTENT.START_TIME, startTime);
		data.putExtra(KEY.INTENT.END_TIME, endTime);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	/**
	 * 判断开始时间是否比结束时间早
	 * 
	 * @return
	 */
	private boolean isValid() {
		Date curDate = new Date(System.currentTimeMillis() - 60000); // 减一分钟
		// int sHour = startC.get(Calendar.HOUR_OF_DAY);
		// int sMin = startC.get(Calendar.MINUTE);
		// int eHour = endC.get(Calendar.HOUR_OF_DAY);
		// int eMin = endC.get(Calendar.MINUTE);
		if (!startC.before(endC)) {
			ToastUtil.show(self, "开始时间要比结束时间早");
			return false;
		}
		if (curDate.after(startC.getTime())) {
			ToastUtil.show(self, "开始时间不能比现在早");
			return false;
		}
		return true;
	}
}
