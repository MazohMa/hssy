package com.xpg.hssy.db.pojo;

/**
 * @Description
 * @author Mazoh
 * @createDate 2015年07月3日
 * @version 2.0.0
 */
public class ReturnAlipay {
	private String url;
	private String bill_id;
	private int code;
	private boolean result;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	private String message;
	private QrCodeInfo qrCodeInfo;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public QrCodeInfo getQrCodeInfo() {
		return qrCodeInfo;
	}

	public void setQrCodeInfo(QrCodeInfo qrCodeInfo) {
		this.qrCodeInfo = qrCodeInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBill_id() {
		return bill_id;
	}

	public void setBill_id(String bill_id) {
		this.bill_id = bill_id;
	}
	//
	// public String getQrCodeInfo() {
	// return qrCodeInfo;
	// }
	//
	// public void setQrCodeInfo(String qrCodeInfo) {
	// this.qrCodeInfo = qrCodeInfo;
	// }
	//
	// public String getQrCode() {
	// return qrCode;
	// }
	//
	// public void setQrCode(String qrCode) {
	// this.qrCode = qrCode;
	// }
	//
	// public String getPicUrl() {
	// return picUrl;
	// }
	//
	// public void setPicUrl(String picUrl) {
	// this.picUrl = picUrl;
	// }
	//
	// public String getSmallPicUrl() {
	// return smallPicUrl;
	// }
	//
	// public void setSmallPicUrl(String smallPicUrl) {
	// this.smallPicUrl = smallPicUrl;
	// }
}
