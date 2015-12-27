package com.xpg.hssy.db.pojo;

/**
 * PaymentParam
 * 
 * @author Mazoh 2015 06 18
 * @category 支付宝请求参数
 * @version 2.0
 * 
 */
public class PaymentParam {
	private String sign;
	private String sellerEmail;
	private String outTradeNo;
	private double totalFee ;
	private String subject ;
	private String channel ;

	public double getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSellerEmail() {
		return sellerEmail;
	}

	public void setSellerEmail(String sellerEmail) {
		this.sellerEmail = sellerEmail;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

}
