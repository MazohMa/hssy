package com.xpg.hssy.adapter;

import java.util.Calendar;

import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import android.content.Context;
import android.util.Log;

import com.xpg.hssy.util.TimeUtil;

public class DateWheelAdapter extends AbstractWheelTextAdapter {
	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 6;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	private static final String DEFAULT_FORMAT = "M月d日 E";

	// Values
	private int minValue;
	private int maxValue;

	// format
	private String format;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 */
	public DateWheelAdapter(Context context) {
		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public DateWheelAdapter(Context context, int minValue, int maxValue) {
		this(context, minValue, maxValue, null);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public DateWheelAdapter(Context context, int minValue, int maxValue,
			String format) {
		super(context);

		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index >= 0 && index < getItemsCount()) {
			Calendar value = Calendar.getInstance();
			value.add(Calendar.DAY_OF_YEAR, index);
			String date = formatDate(value);
			if (index == 0) {
				date = "今天";
			}
			return date;
		}
		return null;
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 1;
	}

	private String formatDate(Calendar c) {
		return TimeUtil.format(c.getTimeInMillis(), DEFAULT_FORMAT);
	}
}
