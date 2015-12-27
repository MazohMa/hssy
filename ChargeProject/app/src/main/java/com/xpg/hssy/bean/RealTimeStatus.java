package com.xpg.hssy.bean;
/**
 * RealTimeStatus
 *
 * @author Mazoh 2015 10 10
 * @category 扫码充电过程中获取电桩实时状态信息bean
 * @version 2.4
 *
 */
public class RealTimeStatus {
	private double voltage;
	private double current;
	private double power;
	private int soc ;
	private String  pileId ;
	private  int status ;//设备状态（1表示充电中，2表示充电完成或空闲，3表示设备故障）
	private double curElect;//充点电量
	private  int electType ;//桩类型（1是直流，2是交流）,失效
	private  int interType ;//桩类型（ 1:交流, 2:直流）

	public int getInterType() {
		return interType;
	}

	public void setInterType(int interType) {
		this.interType = interType;
	}

	public int getSoc() {
		return soc;
	}

	public void setSoc(int soc) {
		this.soc = soc;
	}

	public int getElectType() {
		return electType;
	}

	public void setElectType(int electType) {
		this.electType = electType;
	}

	public double getCurElect() {
		return curElect;
	}

	public void setCurElect(double curElect) {
		this.curElect = curElect;
	}


	public String getPileId() {
		return pileId;
	}

	public void setPileId(String pileId) {
		this.pileId = pileId;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}