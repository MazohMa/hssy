package com.xpg.hssy.db.pojo;

/**
 * Created by Administrator on 2015/12/9.
 */
public class ExpenseRecord {

	private float payMoney;
	private String orderNo;
	private long payTime;

	public float getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(float payMoney) {
		this.payMoney = payMoney;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public long getPayTime() {
		return payTime;
	}

	public void setPayTime(long payTime) {
		this.payTime = payTime;
	}
}
