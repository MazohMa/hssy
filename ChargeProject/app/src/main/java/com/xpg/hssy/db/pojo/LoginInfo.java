package com.xpg.hssy.db.pojo;

import java.io.Serializable;

/**
 * Created by Gunter on 2015/10/22.
 */
public class LoginInfo implements Serializable {

    private String token;
    private String userId;
    private String nickName;
    private String userAvaterUrl;
    private int gender;
    private int userType;

    public LoginInfo(String token, String userId, int gender, String nickName, int userType) {
        this.token = token;
        this.userId = userId;
        this.gender = gender;
        this.nickName = nickName;
        this.userType = userType;
    }

    public LoginInfo() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserAvaterUrl() {
        return userAvaterUrl;
    }

    public void setUserAvaterUrl(String userAvaterUrl) {
        this.userAvaterUrl = userAvaterUrl;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
