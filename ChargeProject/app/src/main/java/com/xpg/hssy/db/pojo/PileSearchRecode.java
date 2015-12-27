package com.xpg.hssy.db.pojo;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table PILE_SEARCH_RECODE.
 */
public class PileSearchRecode {

    /** Not-null value. */
    private String pileId;
    private Double longitude;
    private Double latitude;
    private String name;
    private String address;
    private int operator;
    private Integer gprsType;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public PileSearchRecode() {
    }

    public PileSearchRecode(String pileId) {
        this.pileId = pileId;
    }

    public PileSearchRecode(String pileId, Double longitude, Double latitude, String name, String address, int operator, Integer gprsType) {
        this.pileId = pileId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.address = address;
        this.operator = operator;
        this.gprsType = gprsType;
    }

    /** Not-null value. */
    public String getPileId() {
        return pileId;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPileId(String pileId) {
        this.pileId = pileId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public Integer getGprsType() {
        return gprsType;
    }

    public void setGprsType(Integer gprsType) {
        this.gprsType = gprsType;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}