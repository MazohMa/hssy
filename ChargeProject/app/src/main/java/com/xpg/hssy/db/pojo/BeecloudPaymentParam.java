package com.xpg.hssy.db.pojo;

/**
 * PaymentParams
 *
 * @author Jason 2015 11 09
 * @version 1.0
 * @category beecloud请求参数
 */
public class BeecloudPaymentParam {

	private String billTitle;
	private int billTotalFee;
	private String billNum;
	private boolean enoughFlag;

	public String getBillTitle() {
		return billTitle;
	}

	public void setBillTitle(String billTitle) {
		this.billTitle = billTitle;
	}

	public int getBillTotalFee() {
		return billTotalFee;
	}

	public void setBillTotalFee(int billTotalFee) {
		this.billTotalFee = billTotalFee;
	}

	public String getBillNum() {
		return billNum;
	}

	public void setBillNum(String billNum) {
		this.billNum = billNum;
	}

	public boolean isEnoughFlag() {
		return enoughFlag;
	}

	public void setEnoughFlag(boolean enoughFlag) {
		this.enoughFlag = enoughFlag;
	}

	public String toString() {
		return "billTitle:" + billTitle + " | billTotalFee:" + billTotalFee + " | billNum:" + billNum + " |  enoughFlag:"+enoughFlag;
	}
}
