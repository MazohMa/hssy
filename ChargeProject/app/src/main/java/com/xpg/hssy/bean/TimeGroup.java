package com.xpg.hssy.bean;

import java.util.List;

public class TimeGroup {
	private String startTime;
	private String endTime;
	private double splitPeriod;
	private List<String> free;
	private List<String> booked;
	private List<String> overdue;
	private boolean bookable;

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public double getSplitPeriod() {
		return splitPeriod;
	}

	public void setSplitPeriod(double splitPeriod) {
		this.splitPeriod = splitPeriod;
	}

	public List<String> getFree() {
		return free;
	}

	public void setFree(List<String> free) {
		this.free = free;
	}

	public List<String> getBooked() {
		return booked;
	}

	public void setBooked(List<String> booked) {
		this.booked = booked;
	}

	public List<String> getOverdue() {
		return overdue;
	}

	public void setOverdue(List<String> overdue) {
		this.overdue = overdue;
	}

	public boolean isBookable() {
		return bookable;
	}

	public void setBookable(boolean bookable) {
		this.bookable = bookable;
	}
}
