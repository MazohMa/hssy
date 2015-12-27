package com.xpg.hssy.bean;

/**
 * By Mazoh
 */
public class ThemeTopic {

	private String content = null;
	private String userid = null;
	private Double longitude = null;
	private Double latitude = null;
	private String location = null;
	private int type;//1.电站评论，2桩（订单）评论，3优易圈话题
	private String pileId = null;
	private String pileName = null;
	private Short pileScore = null;
	private Short envirScore = null ;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPileName() {
		return pileName;
	}

	public void setPileName(String pileName) {
		this.pileName = pileName;
	}

	public String getPileId() {
		return pileId;
	}

	public void setPileId(String pileId) {
		this.pileId = pileId;
	}

	public Short getPileScore() {
		return pileScore;
	}

	public void setPileScore(Short pileScore) {
		this.pileScore = pileScore;
	}

	public Short getEnvirScore() {
		return envirScore;
	}

	public void setEnvirScore(Short envirScore) {
		this.envirScore = envirScore;
	}

	public Short getFacedScore() {
		return facedScore;
	}

	public void setFacedScore(Short facedScore) {
		this.facedScore = facedScore;
	}

	public Short getFuncScore() {
		return funcScore;
	}

	public void setFuncScore(Short funcScore) {
		this.funcScore = funcScore;
	}

	public Short getServScore() {
		return servScore;
	}

	public void setServScore(Short servScore) {
		this.servScore = servScore;
	}

	private Short facedScore = null ;
	private Short funcScore = null;
	private Short servScore = null;


}
