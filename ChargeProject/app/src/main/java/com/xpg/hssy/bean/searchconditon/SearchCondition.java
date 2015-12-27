package com.xpg.hssy.bean.searchconditon;

import java.util.Set;

public abstract class SearchCondition {
	public static final int CURRENT_DC = 1;//私人直流
	public static final int CURRENT_AC = 2;//私人交流
	public static final int PUBLIC = 3;//公共

	// 这些常量貌似废弃了
	public static final int OPERATOR_PERSONAL = 0;
	public static final int OPERATOR_HSSY = 1;
	public static final int OPERATOR_NATION = 2;

	public abstract String getSearch();

	public abstract void setSearch(String search);

	public abstract String getCityId();

	public abstract void setCityId(String cityId);

	public abstract String getAddress();

	public abstract void setAddress(String address);

	public abstract Set<Integer> getPileTypes();

	public abstract void setPileTypes(Set<Integer> pileTypes);

	public abstract void addPileTypes(Integer type);

	public abstract void removePileTypes(Integer type);

	public abstract boolean getIsIdle();

	public abstract void setIsIdle(boolean isIdle);

	public abstract long getStartTime();

	public abstract void setStartTime(long startTime);

	public abstract long getEndTime();

	public abstract void setEndTime(long endTime);

	public abstract double getDuration();

	public abstract void setDuration(double duration);
}
