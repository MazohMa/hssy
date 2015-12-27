package com.xpg.hssy.bean;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeItem implements Comparable<TimeItem> {
	public static final int ITEM_STATE_FREE = 0;
	public static final int ITEM_STATE_BOOKED = 1;
	public static final int ITEM_STATE_OVERDUE = 2;
	private static SimpleDateFormat sdf;
	private Calendar startC;
	private Calendar endC;
	private int itemState;
	private boolean isSelected;
	private static final String BASE_TIME = "1970-01-01 ";
	static {
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public TimeItem(String startTime, double duration, int itemState)
			throws ParseException {
		Date startDate = sdf.parse(BASE_TIME + startTime + ":00");
		startC = Calendar.getInstance();
		startC.setTime(startDate);
		endC = (Calendar) startC.clone();
		endC.add(Calendar.MINUTE, (int) (duration * 60));
		this.itemState = itemState;
	};

	public Calendar getStartTime() {
		return startC;
	}

	public void setStartTime(Calendar startTime) {
		this.startC = startTime;
	}

	public Calendar getEndTime() {
		return endC;
	}

	public void setEndTime(Calendar endTime) {
		this.endC = endTime;
	}

	public int getItemState() {
		return itemState;
	}

	public void setItemState(int itemState) {
		this.itemState = itemState;
	}

	@Override
	public int compareTo(TimeItem another) {
		if (this.getStartTime().after(another.getStartTime())) {
			return 1;
		} else if (this.getStartTime().before(another.getStartTime())) {
			return -1;
		} else {
			return 0;
		}
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
