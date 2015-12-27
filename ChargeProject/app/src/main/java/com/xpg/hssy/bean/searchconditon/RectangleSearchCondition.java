package com.xpg.hssy.bean.searchconditon;

import java.io.Serializable;
import java.util.Set;

public class RectangleSearchCondition extends SearchCondition implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1671557180843119481L;

	private SearchCondition searchCondition;

	private double endLongitude = -1;
	private double endLatitude = -1;
	private double startLongitude = -1;
	private double startLatitude = -1;
	private float zoom;

	public RectangleSearchCondition() {
		searchCondition = new BaseSearchCondition();
	}

	public RectangleSearchCondition(SearchCondition searchCondition) {
		this.searchCondition = searchCondition;
	}

	public double getEndLongitude() {
		return endLongitude;
	}

	public void setEndLongitude(double endLongitude) {
		this.endLongitude = endLongitude;
	}

	public double getEndLatitude() {
		return endLatitude;
	}

	public void setEndLatitude(double endLatitude) {
		this.endLatitude = endLatitude;
	}

	public double getStartLong() {
		return startLongitude;
	}

	public void setStartLong(double startLongitude) {
		this.startLongitude = startLongitude;
	}

	public double getStartLat() {
		return startLatitude;
	}

	public void setStartLat(double startLatitude) {
		this.startLatitude = startLatitude;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	@Override
	public String getSearch() {
		synchronized (searchCondition) {
			return searchCondition.getSearch();
		}

	}

	@Override
	public void setSearch(String search) {
		synchronized (searchCondition) {
			searchCondition.setSearch(search);
		}
	}

	@Override
	public String getCityId() {
		synchronized (searchCondition) {
			return searchCondition.getCityId();
		}
	}

	@Override
	public void setCityId(String cityId) {
		synchronized (searchCondition) {
			searchCondition.setCityId(cityId);
		}
	}

	@Override
	public String getAddress() {
		synchronized (searchCondition) {
			return searchCondition.getAddress();
		}
	}

	@Override
	public void setAddress(String address) {
		synchronized (searchCondition) {
			searchCondition.setAddress(address);
		}
	}

	@Override
	public Set<Integer> getPileTypes() {
		synchronized (searchCondition) {
			return searchCondition.getPileTypes();
		}
	}

	@Override
	public void setPileTypes(Set<Integer> pileTypes) {
		synchronized (searchCondition) {
			searchCondition.setPileTypes(pileTypes);
		}
	}

	@Override
	public void addPileTypes(Integer type) {
		synchronized (searchCondition) {
			searchCondition.addPileTypes(type);
		}
	}

	@Override
	public void removePileTypes(Integer type) {
		synchronized (searchCondition) {
			searchCondition.removePileTypes(type);
		}
	}

	@Override
	public boolean getIsIdle() {
		synchronized (searchCondition) {
			return searchCondition.getIsIdle();
		}
	}

	@Override
	public void setIsIdle(boolean isIdle) {
		synchronized (searchCondition) {
			searchCondition.setIsIdle(isIdle);
		}
	}

	@Override
	public long getStartTime() {
		synchronized (searchCondition) {
			return searchCondition.getStartTime();
		}
	}

	@Override
	public void setStartTime(long startTime) {
		synchronized (searchCondition) {
			searchCondition.setStartTime(startTime);
		}
	}

	@Override
	public long getEndTime() {
		synchronized (searchCondition) {
			return searchCondition.getEndTime();
		}
	}

	@Override
	public void setEndTime(long endTime) {
		synchronized (searchCondition) {
			searchCondition.setEndTime(endTime);
		}
	}

	@Override
	public double getDuration() {
		synchronized (searchCondition) {
			return searchCondition.getDuration();
		}
	}

	@Override
	public void setDuration(double duration) {
		synchronized (searchCondition) {
			searchCondition.setDuration(duration);
		}
	}

	public SearchCondition getSearchCondition() {
		return searchCondition;
	}
}
