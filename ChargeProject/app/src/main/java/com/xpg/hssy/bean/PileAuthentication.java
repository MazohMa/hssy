package com.xpg.hssy.bean;
/**
 * PileAuthentication
 *
 * @author Mazoh 2015 10 10
 * @category 扫码鉴权bean
 * @version 2.4
 *
 */
public class PileAuthentication {
	private int status;//设备状态 1表示空闲桩，2表示只连接未充电，3表示充电中，4表示GPRS通讯中断，5表示检查中，6：表示预约，7表示故障
	private String pileName;//桩名称
	private String ownerName;//所属业主
	private float price;//充电价格
	private String deviceId;//蓝牙Id（广播名或者出厂编码）
	private String stationName ;//站名称
	private String pileCode ;//桩运行编码
	private int moduleType ;////桩类型
	private String ownerId ;//业主id
	private int pileType ;//2为交流，1为直流

	public int getPileType() {
		return pileType;
	}

	public void setPileType(int pileType) {
		this.pileType = pileType;
	}


	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public int getModuleType() {
		return moduleType;
	}

	public void setModuleType(int moduleType) {
		this.moduleType = moduleType;
	}

	public String getPileCode() {
		return pileCode;
	}

	public void setPileCode(String pileCode) {
		this.pileCode = pileCode;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getPileName() {
		return pileName;
	}

	public void setPileName(String pileName) {
		this.pileName = pileName;
	}

	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
}
