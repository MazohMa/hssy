package com.xpg.hssy.db.pojo;

import com.xpg.hssychargingpole.R;

/**
 * Created by Administrator on 2015/12/9.
 */
public class RechargeRecord {

	private float rechargeMoney;
	private long rechargeTime;
	private int rechargeType;

	public static final int RECHARGE_TYPE_BAIFUBAO = 1;
	public static final int RECHARGE_TYPE_ALIPAY = 2;

	public float getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(float rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}

	public long getRechargeTime() {
		return rechargeTime;
	}

	public void setRechargeTime(long rechargeTime) {
		this.rechargeTime = rechargeTime;
	}

	public int getRechargeType() {
		return rechargeType;
	}

	public void setRechargeType(int rechargeType) {
		this.rechargeType = rechargeType;
	}

	public int getRechargeTypeToStringId(){
		switch(rechargeType){
			case RECHARGE_TYPE_BAIFUBAO:
				return R.string.payway_baifubao;
			case RECHARGE_TYPE_ALIPAY:
				return R.string.payway_alipay;
			default:
				return R.string.unknown;
		}
	}
}
