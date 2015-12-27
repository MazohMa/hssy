package com.xpg.hssy.bean.searchconditon;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class BaseSearchCondition extends SearchCondition implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3238674855242369431L;

	private String search;// 关键字

	private String cityId;// 城市Id
	private String address;// 地址

	private Set<Integer> pileTypes;
	private boolean isIdle = false;
	private long startTime = -1;
	private long endTime = -1;
	private double duration = -1;

	public BaseSearchCondition() {
		pileTypes = new HashSet<>();
	}

	@Override
	public String getSearch() {
		return search;
	}

	@Override
	public void setSearch(String search) {
		this.search = search;
	}

	@Override
	public String getCityId() {
		return cityId;
	}

	@Override
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public Set<Integer> getPileTypes() {
		return pileTypes;
	}

	@Override
	public void setPileTypes(Set<Integer> pileTypes) {
		this.pileTypes = pileTypes;
	}

	@Override
	public void addPileTypes(Integer type) {
		pileTypes.add(type);
	}

	@Override
	public void removePileTypes(Integer type) {
		pileTypes.remove(type);

	}

	@Override
	public boolean getIsIdle() {
		return isIdle;
	}

	@Override
	public void setIsIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public long getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public void setDuration(double duration) {
		this.duration = duration;
	}
}
