package com.xpg.hssy.bean;

public class PileSettingRecord {

	private String order_no_tvs; // 订单号
	private String dianzhuangname_tv; // 电庄名字
	private String contactphone_tvs; // 联系电话
	/*
	 * 充电时间
	 */
	private String charge_year_month_tv;
	private String charge_hour_min_tv;
	private String charge_l_tvs; // 充电电量
	private String liushuihao_tvs; // 流水号
	private String charge_money; // 充电金额

	public String getCharge_money() {
		return charge_money;
	}

	public void setCharge_money(String charge_money) {
		this.charge_money = charge_money;
	}

	public PileSettingRecord() {
	}

	public String getOrder_no_tvs() {
		return order_no_tvs;
	}

	public void setOrder_no_tvs(String order_no_tvs) {
		this.order_no_tvs = order_no_tvs;
	}

	public String getDianzhuangname_tv() {
		return dianzhuangname_tv;
	}

	public void setDianzhuangname_tv(String dianzhuangname_tv) {
		this.dianzhuangname_tv = dianzhuangname_tv;
	}

	public String getContactphone_tvs() {
		return contactphone_tvs;
	}

	public void setContactphone_tvs(String contactphone_tvs) {
		this.contactphone_tvs = contactphone_tvs;
	}

	public String getCharge_year_month_tv() {
		return charge_year_month_tv;
	}

	public void setCharge_year_month_tv(String charge_year_month_tv) {
		this.charge_year_month_tv = charge_year_month_tv;
	}

	public String getCharge_hour_min_tv() {
		return charge_hour_min_tv;
	}

	public void setCharge_hour_min_tv(String charge_hour_min_tv) {
		this.charge_hour_min_tv = charge_hour_min_tv;
	}

	public String getCharge_l_tvs() {
		return charge_l_tvs;
	}

	public void setCharge_l_tvs(String charge_l_tvs) {
		this.charge_l_tvs = charge_l_tvs;
	}

	public String getLiushuihao_tvs() {
		return liushuihao_tvs;
	}

	public void setLiushuihao_tvs(String liushuihao_tvs) {
		this.liushuihao_tvs = liushuihao_tvs;
	}

}
