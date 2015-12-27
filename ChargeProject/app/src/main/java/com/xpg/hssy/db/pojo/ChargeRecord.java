package com.xpg.hssy.db.pojo;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table CHARGE_RECORD.
 */
public class ChargeRecord extends com.xpg.hssy.db.pojo.Record  implements java.io.Serializable {

    private Long id;
    private Integer sequence;
    private String orderId;
    private String userid;
    private String userName;
    private String phoneNo;
    private String pileId;
    private Long startTime;
    private Long endTime;
    private Float quantity;
    private String receipt;
    private Float chargePrice;
    private String data;
    private Integer status;
    private Long createTime;

    // KEEP FIELDS - put your custom fields here
	private Long payTime;
    // KEEP FIELDS END

    public ChargeRecord() {
    }

    public ChargeRecord(Long id) {
        this.id = id;
    }

    public ChargeRecord(Long id, Integer sequence, String orderId, String userid, String userName, String phoneNo, String pileId, Long startTime, Long endTime, Float quantity, String receipt, Float chargePrice, String data, Integer status, Long createTime) {
        this.id = id;
        this.sequence = sequence;
        this.orderId = orderId;
        this.userid = userid;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.pileId = pileId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.receipt = receipt;
        this.chargePrice = chargePrice;
        this.data = data;
        this.status = status;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPileId() {
        return pileId;
    }

    public void setPileId(String pileId) {
        this.pileId = pileId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public Float getChargePrice() {
        return chargePrice;
    }

    public void setChargePrice(Float chargePrice) {
        this.chargePrice = chargePrice;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    // KEEP METHODS - put your custom methods here
    public Long getPayTime() {
		return payTime;
	}

	public void setPayTime(Long payTime) {
		this.payTime = payTime;
	}

    @Override
    public boolean equals(Object o) {
        if (null == o || null == pileId || null == startTime || null == endTime || null == sequence||null==receipt) return false;
        if (o instanceof ChargeRecord) {
            ChargeRecord record = (ChargeRecord) o;
            return pileId.equals(record.getPileId()) && startTime.equals(record.getStartTime()) && endTime.equals(record.getEndTime()) && sequence.equals(record
                    .getSequence()) &&
                    receipt.equals(record.getReceipt());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        StringBuilder strb = new StringBuilder();
        strb.append(pileId).append(startTime).append(endTime).append(sequence).append(receipt);
        return strb.toString().hashCode();
    }
    // KEEP METHODS END

}
