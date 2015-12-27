package com.xpg.hssy.db.pojo;
/**
 * Favorite
 * 
 * @author Mazoh 收藏
 * 
 */
public class Favorite {
	private String pileId ;
	private String userid ;
	private int favor ;
	public String getPileId() {
		return pileId;
	}
	public void setPileId(String pileId) {
		this.pileId = pileId;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public int getFavor() {
		return favor;
	}
	public void setFavor(int favor) {
		this.favor = favor;
	}
}
