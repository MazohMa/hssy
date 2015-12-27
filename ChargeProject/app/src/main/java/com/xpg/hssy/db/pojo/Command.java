package com.xpg.hssy.db.pojo;

public class Command implements java.io.Serializable {
	private long chargeEndTime;
	private int chargeEnergy;
	private int count;
	private long createTime;
	private String evaluate;
	private String id;
	private int level;
	private String orderId;
	private String pileId;
	private int start;
	private String userId;
	private String userName;
	private String userPhone;
	private String avatarUrl;
	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public long getChargeEndTime() {
		return chargeEndTime;
	}

	public void setChargeEndTime(long chargeEndTime) {
		this.chargeEndTime = chargeEndTime;
	}

	public int getChargeEnergy() {
		return chargeEnergy;
	}

	public void setChargeEnergy(int chargeEnergy) {
		this.chargeEnergy = chargeEnergy;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getEvaluate() {
		return evaluate;
	}

	public void setEvaluate(String evaluate) {
		this.evaluate = evaluate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPileId() {
		return pileId;
	}

	public void setPileId(String pileId) {
		this.pileId = pileId;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
}
