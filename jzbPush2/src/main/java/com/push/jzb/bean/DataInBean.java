package com.push.jzb.bean;

import java.util.List;

/**
 * create：2022/6/7 9:08
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class DataInBean {

    private String businessID;
    private Integer callTime;
    private Integer callAction;
    private Integer callType;
    private Integer callingType;
    private String corporate;
    private Boolean isEndGroupCall;
    private String platform;
    private Integer roomId;
    private Integer version;

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }

    public Integer getCallTime() {
        return callTime;
    }

    public void setCallTime(Integer callTime) {
        this.callTime = callTime;
    }

    public Integer getCallAction() {
        return callAction;
    }

    public void setCallAction(Integer callAction) {
        this.callAction = callAction;
    }

    public Integer getCallType() {
        return callType;
    }

    public void setCallType(Integer callType) {
        this.callType = callType;
    }

    public Integer getCallingType() {
        return callingType;
    }

    public void setCallingType(Integer callingType) {
        this.callingType = callingType;
    }

    public String getCorporate() {
        return corporate;
    }

    public void setCorporate(String corporate) {
        this.corporate = corporate;
    }

    public Boolean isIsEndGroupCall() {
        return isEndGroupCall;
    }

    public void setIsEndGroupCall(Boolean isEndGroupCall) {
        this.isEndGroupCall = isEndGroupCall;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
