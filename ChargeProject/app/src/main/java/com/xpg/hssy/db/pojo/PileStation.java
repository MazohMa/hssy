package com.xpg.hssy.db.pojo;

import android.content.Context;

import com.easy.util.EmptyUtil;
import com.xpg.hssy.constant.MyConstant;
import com.xpg.hssychargingpole.R;

import java.util.List;

/**
 * Created by Administrator on 2015/10/12.
 */
public class PileStation {


	private String id;
	private String stationName;
	private String location;
	private int type;
	private Float avgLevel;
	private String remark;//站描述
	private Double chargeFee;
	private String payment; //计费方式
	private String funcType;//功能区
	private String facilities;//便利设施
	private double latitude;
	private double longitude;
	private String operatorName;
	private String operatorPhone;
	private int operator; //运营商类型
	private int isIdle; //是否空闲
	private boolean isBookable;//是否可预约
	private String coverImg;
	private List<String> imgurl;//图片链接
	private int acount;//评论总数
	private int favor;//是否收藏 0：没收藏；1：收藏
	private int freeNum;
	private int alterNum;
	private int directNum;

	//是否空闲
	public static final int STATE_STATION_HAVE_FREE = 1; //公共站有空闲
	public static final int STATE_STATION_FULL_LOAD = 2; //公共站满载
	public static final int STATE_STATION_OFFLINE = 3;//公共站离网

	//站类型

	public static final int STATION_TYPE_PUBLIC = 0x01;//公共开放站
	public static final int STATION_TYPE_SPECIAL = 0x02;//专用站
	public static final int STATION_TYPE_OTHER = 0x03;//其他站点

	public static final int TYPE_GPRS_NOT = 2;
	public static final int TYPE_GPRS_YES = 1;

	//计费方式
	public static final int PAY_TYPE_CARD = 0x01;//刷卡
	public static final int PAY_TYPE_CASH = 0x02;//现金
	public static final int PAY_TYPE_ONLINE = 0x03;//在线支付
	public static final int PAY_TYPE_FREE = 0x04;//免费
	public static final int PAY_TYPE_OTHER = 0x05;//其他

	//是否收藏
	public static final int FAVOR_NOT = 0;
	public static final int FAVOR_YES = 1;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStationName() {
		return stationName;
	}

	public String getStationNameAsString() {
		return stationName == null ? "" : stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getLocation() {
		return location;
	}

	public int getIsIdle() {
		return isIdle;
	}

	public int getIsIdleAsStringID() {
		if (isIdle == STATE_STATION_HAVE_FREE) {
			return R.string.idle;
		} else if(isIdle == STATE_STATION_FULL_LOAD){
			return R.string.full_load;
		} else if(isIdle == STATE_STATION_OFFLINE){
			return R.string.offline;
		}else{
			return R.string.unknown;
		}
	}

	public void setIsIdle(int isIdle) {
		this.isIdle = isIdle;
	}

	public String getLocationAsString() {
		return location == null ? "" : location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getChargeFee() {
		return chargeFee == null ? 0 : chargeFee;
	}

	public void setChargeFee(Double chargeFee) {
		this.chargeFee = chargeFee;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public String getOperatorNameAsString() {
		return operatorName == null ? "" : operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperatorPhone() {
		return operatorPhone;
	}

	public String getOperatorPhoneAsString() {
		return operatorPhone == null ? "" : operatorPhone;
	}

	public void setOperatorPhone(String operatorPhone) {
		this.operatorPhone = operatorPhone;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public int getFavor() {
		return favor;
	}

	public void setFavor(int favor) {
		this.favor = favor;
	}

	public Float getAvgLevel() {
		return avgLevel;
	}

	public void setAvgLevel(Float avgLevel) {
		this.avgLevel = avgLevel;
	}

	public int getAcount() {
		return acount;
	}

	public void setAcount(int acount) {
		this.acount = acount;
	}

	public String getPayment() {
		return payment;
	}

	public String getPaymentAsString(Context context) {
		StringBuffer sb = new StringBuffer();
		if (payment != null) {
			String[] temp = payment.split(",");
			for (String i : temp) {
				if (i.equals("1")) {
					sb.append(context.getResources().getString(R.string.pay_swiping_card) + " ");
				}
				if (i.equals("2")) {
					sb.append(context.getResources().getString(R.string.pay_cash) + " ");
				}
				if (i.equals("3")) {
					sb.append(context.getResources().getString(R.string.pay_online) + " ");
				}
				if (i.equals("4")) {
					sb.append(context.getResources().getString(R.string.pay_free) + " ");
				}
				if (i.equals("5")) {
					sb.append(context.getResources().getString(R.string.other) + " ");
				}
			}
		}
		return sb.toString();
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getFacilities() {
		return facilities;
	}

	public int[] getFacilitiesAsInt() {
		if (facilities != null && !"".equals(facilities)) {
			String[] temp = facilities.split(",");
			int[] int_amenities = new int[temp.length];
			for (int i = 0; i < temp.length; i++) {
				int_amenities[i] = Integer.parseInt(temp[i]);
			}
			return int_amenities;
		}
		return null;
	}

	public void setAmenities(String facilities) {
		this.facilities = facilities;
	}

	public List<String> getImgurl() {
		return imgurl;
	}

	public void setImgurl(List<String> imgurl) {
		this.imgurl = imgurl;
	}

	public String getCoverImg() {
		return coverImg;
	}

	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}

	public int getType() {
		return type;
	}

//	public int getTypeAsStringID() {
//		if (type == STATION_TYPE_OTHER) {
//			return R.string.type_other_station;
//		} else {
//			return R.string.type_public_station;
//		}
//	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFuncType() {
		return funcType;
	}

	public int getFuncTypeAsStringID() {
		if (EmptyUtil.isEmpty(funcType)) {
			return R.string.nothing;
		} else {
			int funType = Integer.parseInt(funcType);
			switch (funType) {
				case MyConstant.FUNCTION_TYPE_TRAFFIC:
					return R.string.traffic;
				case MyConstant.FUNCTION_TYPE_LIVING:
					return R.string.living;
				case MyConstant.FUNCTION_TYPE_MARKET:
					return R.string.super_market;
				case MyConstant.FUNCTION_TYPE_4S_SHOP:
					return R.string.car_shop;
				case MyConstant.FUNCTION_TYPE_SCHOOL:
					return R.string.school;
				case MyConstant.FUNCTION_TYPE_SCENIC:
					return R.string.scenic;
				case MyConstant.FUNCTION_TYPE_SERVICE_AREA:
					return R.string.service_area;
				case MyConstant.FUNCTION_TYPE_OFFICE:
					return R.string.office;
				case MyConstant.FUNCTION_TYPE_OTHER:
					return R.string.other;
				default:
					return R.string.nothing;
			}
		}
	}

	public void setFuncType(String funcType) {
		this.funcType = funcType;
	}

	public int getOperator() {
		return operator;
	}

	public void setOperator(int operator) {
		this.operator = operator;
	}

	public boolean isBookable() {
		return isBookable;
	}

	public void setIsBookable(boolean isBookable) {
		this.isBookable = isBookable;
	}

	public int getFreeNum() {
		return freeNum;
	}

	public void setFreeNum(int freeNum) {
		this.freeNum = freeNum;
	}

	public int getAlterNum() {
		return alterNum;
	}

	public void setAlterNum(int alterNum) {
		this.alterNum = alterNum;
	}

	public int getDirectNum() {
		return directNum;
	}

	public void setDirectNum(int directNum) {
		this.directNum = directNum;
	}

}
