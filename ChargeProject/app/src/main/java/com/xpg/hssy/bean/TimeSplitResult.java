package com.xpg.hssy.bean;

import java.util.List;

public class TimeSplitResult {
	private long date;
	private List<TimeGroup> data;

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	public List<TimeGroup> getData() {
		return data;
	}

	public void setData(List<TimeGroup> data) {
		this.data = data;
	}

	public DateBookTime createDateBookTime() {
		return new DateBookTime(data);
	}
}
