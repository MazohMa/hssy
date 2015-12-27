package com.xpg.hssy.db.pojo;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

import java.util.Comparator;

/**
 * Entity mapped to table CHARGE_RECORD.
 */
public abstract class Record implements java.io.Serializable {

	// KEEP FIELDS - put your custom fields here
	// KEEP FIELDS END

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Integer getSequence();

	public abstract void setSequence(Integer sequence);

	public abstract String getOrderId();

	public abstract void setOrderId(String orderId);

	public abstract String getUserid();

	public abstract void setUserid(String userid);

	public abstract String getUserName();

	public abstract void setUserName(String userName);

	public abstract String getPhoneNo();

	public abstract void setPhoneNo(String phoneNo);

	public abstract String getPileId();

	public abstract void setPileId(String pileId);

	public abstract Long getStartTime();

	public abstract void setStartTime(Long startTime);

	public abstract Long getEndTime();

	public abstract void setEndTime(Long endTime);

	public abstract Float getQuantity();

	public abstract void setQuantity(Float quantity);

	public abstract String getReceipt();

	public abstract void setReceipt(String receipt);

	public abstract Float getChargePrice();

	public abstract void setChargePrice(Float chargeMoney);

	public abstract String getData();

	public abstract void setData(String data);

	public abstract Integer getStatus();

	public abstract void setStatus(Integer status);

	public abstract Long getCreateTime();

	public abstract void setCreateTime(Long createTime);

	// KEEP METHODS - put your custom methods here
	public static class RecordSorter implements Comparator<Record> {

		@Override
		public int compare(Record lhs, Record rhs) {
			if (lhs.getStartTime() == null || lhs.getEndTime() == null || rhs.getStartTime() == null || rhs.getEndTime() == null) return 0;
			long a = lhs.getStartTime() - rhs.getStartTime();
			if (a == 0) {
				return (int) (lhs.getEndTime() - rhs.getEndTime());
			} else return (int) a;
		}
	}
	// KEEP METHODS END

}
