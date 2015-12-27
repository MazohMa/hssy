package com.xpg.hssy.db.pojo;

/**
 * @Description
 * @author Joke Huang
 * @createDate 2015年1月23日
 * @version 1.0.0
 */

public class PileStatus {
	public static final byte STATUS_IDLE = 0;
	public static final byte STATUS_CHARGING = 1;

	public static final byte STATUS_CMD_STOP_CHARGE = 0;
	public static final byte STATUS_CMD_START_CHARGE = 1;

	public static final byte STATUS_DELAY_END = 0;
	public static final byte STATUS_DELAYING = 1;

	private byte alarmByte;
	// 交流过压
	private boolean isVoltageOver;
	// 交流欠压
	private boolean isVoltageLower;
	// 交流过流
	private boolean isCurrentOver;
	// 交流漏电
	private boolean isCurrentLeak;
	// 交流短路
	private boolean isCurrentShort;
	// 继电器状态
	private byte chargeStatus;
	// 充电枪连接状态
	private boolean gunConnected;
	// 车辆连接状态
	private boolean carConnected;
	// 车辆准备状态
	private boolean carReady;
	// 命令状态
	private byte cmdStatus;
	// 定时状态
	private byte delayStatus;
	// 定时时间戳
	private long delayTime;

	public boolean isVoltageOver() {
		return isVoltageOver;
	}

	public void setVoltageOver(boolean isVoltageOver) {
		this.isVoltageOver = isVoltageOver;
	}

	public boolean isVoltageLower() {
		return isVoltageLower;
	}

	public void setVoltageLower(boolean isVoltageLower) {
		this.isVoltageLower = isVoltageLower;
	}

	public boolean isCurrentOver() {
		return isCurrentOver;
	}

	public void setCurrentOver(boolean isCurrentOver) {
		this.isCurrentOver = isCurrentOver;
	}

	public boolean isCurrentLeak() {
		return isCurrentLeak;
	}

	public void setCurrentLeak(boolean isCurrentLeak) {
		this.isCurrentLeak = isCurrentLeak;
	}

	public boolean isCurrentShort() {
		return isCurrentShort;
	}

	public void setCurrentShort(boolean isCurrentShort) {
		this.isCurrentShort = isCurrentShort;
	}

	public byte getChargeStatus() {
		return chargeStatus;
	}

	public void setChargeStatus(byte chargeStatus) {
		this.chargeStatus = chargeStatus;
	}

	public byte getCmdStatus() {
		return cmdStatus;
	}

	public void setCmdStatus(byte cmdStatus) {
		this.cmdStatus = cmdStatus;
	}

	public byte getDelayStatus() {
		return delayStatus;
	}

	public void setDelayStatus(byte delayStatus) {
		this.delayStatus = delayStatus;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public boolean isGunConnected() {
		return gunConnected;
	}

	public void setGunConnected(boolean gunConnected) {
		this.gunConnected = gunConnected;
	}

	public boolean isCarConnected() {
		return carConnected;
	}

	public void setCarConnected(boolean carConnected) {
		this.carConnected = carConnected;
	}

	public boolean isCarReady() {
		return carReady;
	}

	public void setCarReady(boolean carReady) {
		this.carReady = carReady;
	}

	public boolean isError() {
		return isVoltageOver || isVoltageLower || isCurrentOver
				|| isCurrentLeak || isCurrentShort;
	}

	public byte getAlarmByte() {
		return alarmByte;
	}

	public void setAlarmByte(byte alarmByte) {
		this.alarmByte = alarmByte;
	}
}
