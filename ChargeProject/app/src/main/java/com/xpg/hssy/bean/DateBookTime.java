package com.xpg.hssy.bean;

import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DateBookTime {
	private List<TimeGroup> timeGroupList;
	private Map<TimeGroup, Set<TimeItem>> groupToItemsMap;
	private boolean bookable;

	public List<TimeGroup> getTimeGroupList() {
		return timeGroupList;
	}

	public Map<TimeGroup, Set<TimeItem>> getGroupToItemsMap() {
		return groupToItemsMap;
	}

	public boolean isBookable() {
		return bookable;
	}

	public DateBookTime(List<TimeGroup> timeGroupList) {
		this.timeGroupList = timeGroupList;
		if (timeGroupList != null) {
			Collections.sort(this.timeGroupList, new timeGroupSorter());
		}
		groupToItemsMap = new HashMap<>();
		createGroupToItemsMap();
	}

	private void createGroupToItemsMap() {
		if (timeGroupList != null) {
			for (TimeGroup group : timeGroupList) {
				double duration = group.getSplitPeriod();
				Set<TimeItem> timeItemSet = new TreeSet<>();
				List<String> free = group.getFree();
				List<String> booked = group.getBooked();
				List<String> overdue = group.getOverdue();
				if (free != null) {
					for (String startTime : free) {
						try {
							TimeItem timeItem;
							timeItem = new TimeItem(startTime, duration,
									TimeItem.ITEM_STATE_FREE);
							timeItemSet.add(timeItem);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (timeItemSet.size() > 0) {
						group.setBookable(true);
						bookable = true;
					}
				}
				if (booked != null) {
					for (String startTime : booked) {
						try {
							TimeItem timeBlock = new TimeItem(startTime,
									duration, TimeItem.ITEM_STATE_BOOKED);
							timeItemSet.add(timeBlock);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				if (overdue != null) {
					for (String startTime : overdue) {
						try {
							TimeItem timeBlock = new TimeItem(startTime,
									duration, TimeItem.ITEM_STATE_BOOKED);
							timeItemSet.add(timeBlock);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				groupToItemsMap.put(group, timeItemSet);
			}
		}

	}

	private static class timeGroupSorter implements Comparator<TimeGroup> {

		@Override
		public int compare(TimeGroup lhs, TimeGroup rhs) {
			try {
				TimeItem litem = new TimeItem(lhs.getStartTime(), 0,
						TimeItem.ITEM_STATE_FREE);
				TimeItem ritem = new TimeItem(rhs.getStartTime(), 0,
						TimeItem.ITEM_STATE_FREE);
				return litem.compareTo(ritem);
			} catch (ParseException e) {
				e.printStackTrace();
				return 0;
			}

		}
	}
}
