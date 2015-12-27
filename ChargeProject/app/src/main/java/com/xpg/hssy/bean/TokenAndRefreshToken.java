package com.xpg.hssy.bean;
/**
 * @Description
 * @author Mazoh
 * @createDate 2015年8月13日
 * @version 2.3.0
 */

public class TokenAndRefreshToken {
	private String token ;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	private String refreshToken ;
}
