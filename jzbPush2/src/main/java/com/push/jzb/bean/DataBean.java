package com.push.jzb.bean;

import java.util.List;

/**
 * create：2022/6/7 9:08
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class DataBean {
    private Integer businessID;
    private String inviteID;
    private String inviter;
    private String data;
    private Integer timeout;
    private Integer actionType;
    private Boolean onlineUserOnly;
    private List<String> inviteeList;
    private int type;
    private List<String> userIdList;
    public Integer getBusinessID() {
        return businessID;
    }

    public void setBusinessID(Integer businessID) {
        this.businessID = businessID;
    }

    public String getInviteID() {
        return inviteID;
    }

    public void setInviteID(String inviteID) {
        this.inviteID = inviteID;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Boolean isOnlineUserOnly() {
        return onlineUserOnly;
    }

    public void setOnlineUserOnly(Boolean onlineUserOnly) {
        this.onlineUserOnly = onlineUserOnly;
    }

    public List<String> getInviteeList() {
        return inviteeList;
    }

    public void setInviteeList(List<String> inviteeList) {
        this.inviteeList = inviteeList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }
}
