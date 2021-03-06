package com.xpg.hssy.db.pojo;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
import java.util.List;
// KEEP INCLUDES END
/**
 * Entity mapped to table CHARGE_ORDER.
 */
public class ChargeOrder implements java.io.Serializable {

    private String orderId;
    private Integer action;
    private Integer orderType;
    private Long chargeEndTime;
    private Float chargeEnergy;
    private String chargeId;
    private Float chargePrice;
    private Float servicePay;
    private Long chargeStartTime;
    private Long confirmTime;
    private String contactName;
    private String contactPhone;
    private Long createTime;
    private Long endTime;
    private Double latitude;
    private String location;
    private Double longitude;
    private Long orderTime;
    private String ownerId;
    private Long payTime;
    private String pileId;
    private String pileName;
    private String pilePhone;
    private Long startTime;
    private String tenantName;
    private String tenantPhone;
    private String userid;
    private String evaluateDetial;

    // KEEP FIELDS - put your custom fields here
	// 0.待确认、1.桩主确认、已确定，2.桩主拒绝，3.客户取消，4.已充电、待付款，5.已付款、待评价，6.已过期,7.已评价
	public static final int ACTION_WATTING = 0;
	public static final int ACTION_CONFIRM = 1;
	public static final int ACTION_REJECT = 2;
	public static final int ACTION_CANCEL = 3;
	public static final int ACTION_COMPLETE = 4;
	public static final int ACTION_PAIED = 5;
	public static final int ACTION_TIMEOUT = 6;
	public static final int ACTION_COMMANDED = 7;

	private int pileLevel;
	private List<ChargeRecord> chargeList;
    // KEEP FIELDS END

    public ChargeOrder() {
    }

    public ChargeOrder(String orderId) {
        this.orderId = orderId;
    }

    public ChargeOrder(String orderId, Integer action, Integer orderType, Long chargeEndTime, Float chargeEnergy, String chargeId, Float chargePrice, Float servicePay, Long chargeStartTime, Long confirmTime, String contactName, String contactPhone, Long createTime, Long endTime, Double latitude, String location, Double longitude, Long orderTime, String ownerId, Long payTime, String pileId, String pileName, String pilePhone, Long startTime, String tenantName, String tenantPhone, String userid, String evaluateDetial) {
        this.orderId = orderId;
        this.action = action;
        this.orderType = orderType;
        this.chargeEndTime = chargeEndTime;
        this.chargeEnergy = chargeEnergy;
        this.chargeId = chargeId;
        this.chargePrice = chargePrice;
        this.servicePay = servicePay;
        this.chargeStartTime = chargeStartTime;
        this.confirmTime = confirmTime;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.createTime = createTime;
        this.endTime = endTime;
        this.latitude = latitude;
        this.location = location;
        this.longitude = longitude;
        this.orderTime = orderTime;
        this.ownerId = ownerId;
        this.payTime = payTime;
        this.pileId = pileId;
        this.pileName = pileName;
        this.pilePhone = pilePhone;
        this.startTime = startTime;
        this.tenantName = tenantName;
        this.tenantPhone = tenantPhone;
        this.userid = userid;
        this.evaluateDetial = evaluateDetial;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getChargeEndTime() {
        return chargeEndTime;
    }

    public void setChargeEndTime(Long chargeEndTime) {
        this.chargeEndTime = chargeEndTime;
    }

    public Float getChargeEnergy() {
        return chargeEnergy;
    }

    public void setChargeEnergy(Float chargeEnergy) {
        this.chargeEnergy = chargeEnergy;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public Float getChargePrice() {
        return chargePrice;
    }

    public void setChargePrice(Float chargePrice) {
        this.chargePrice = chargePrice;
    }

    public Float getServicePay() {
        return servicePay;
    }

    public void setServicePay(Float servicePay) {
        this.servicePay = servicePay;
    }

    public Long getChargeStartTime() {
        return chargeStartTime;
    }

    public void setChargeStartTime(Long chargeStartTime) {
        this.chargeStartTime = chargeStartTime;
    }

    public Long getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
    }

    public String getPileId() {
        return pileId;
    }

    public void setPileId(String pileId) {
        this.pileId = pileId;
    }

    public String getPileName() {
        return pileName;
    }

    public void setPileName(String pileName) {
        this.pileName = pileName;
    }

    public String getPilePhone() {
        return pilePhone;
    }

    public void setPilePhone(String pilePhone) {
        this.pilePhone = pilePhone;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEvaluateDetial() {
        return evaluateDetial;
    }

    public void setEvaluateDetial(String evaluateDetial) {
        this.evaluateDetial = evaluateDetial;
    }

    // KEEP METHODS - put your custom methods here
	public int getPileLevel() {
		return pileLevel;
	}

	public void setPileLevel(int pileLevel) {
		this.pileLevel = pileLevel;
	}

	public List<ChargeRecord> getChargeList() {
		return chargeList;
	}

	public void setChargeList(List<ChargeRecord> charge) {
		chargeList = charge;
	}
    // KEEP METHODS END

}
