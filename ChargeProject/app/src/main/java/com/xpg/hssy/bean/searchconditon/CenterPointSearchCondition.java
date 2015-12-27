package com.xpg.hssy.bean.searchconditon;

import java.io.Serializable;
import java.util.Set;

/**
 * @description
 * @author Joke
 * @email 113979462@qq.com
 * @create 2015年3月25日
 * @version 1.0.0
 */

public class CenterPointSearchCondition extends SearchCondition implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8242943053561744763L;

	public static final int SORT_HOT = 0;
	public static final int SORT_DISTANCE = 1;
	public static final int SORT_GRADE = 2;
	public static final int SORT_PRICE = 3;

	private SearchCondition searchCondition;

	// private String keyWord;// 搜索关键字

	private double centerLongitude = -1;// 中心经度
	private double centerLatitude = -1;// 中心纬度
	private long distance = -1;// 距离
	private int sortType = -1;// 排序
	private float zoom = -1;// 缩放等级

	// private int[] operatorTypes;// 拥有者类型（个人、华商、国家）

	// private boolean isFiltrateFree = false;

	// public String getKeyWord() {
	// return keyWord;
	// }

	// public void setKeyWord(String keyWord) {
	// this.keyWord = keyWord;
	// }

	public CenterPointSearchCondition() {
		searchCondition = new BaseSearchCondition();
	}

	public CenterPointSearchCondition(SearchCondition searchCondition) {
		this.searchCondition = searchCondition;
	}

	public double getCenterLongitude() {
		return centerLongitude;
	}

	public void setCenterLongitude(double longitude) {
		this.centerLongitude = longitude;
	}

	public double getCenterLatitude() {
		return centerLatitude;
	}

	public void setCenterLatitude(double latitude) {
		this.centerLatitude = latitude;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public int getSortType() {
		return sortType;
	}

	public void setSortType(int sortType) {
		this.sortType = sortType;
	}

	// public int[] getOperatorTypes() {
	// return operatorTypes;
	// }
	//
	// public void setOperatorTypes(int[] operatorTypes) {
	// this.operatorTypes = operatorTypes;
	// }
	//
	// public boolean isFiltrateFree() {
	// return isFiltrateFree;
	// }
	//
	// public void setFiltrateFree(boolean filtrateFree) {
	// this.isFiltrateFree = filtrateFree;
	// }

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

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
}
