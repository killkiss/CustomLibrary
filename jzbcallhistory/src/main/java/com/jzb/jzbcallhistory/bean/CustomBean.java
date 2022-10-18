package com.jzb.jzbcallhistory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * create：2022/8/11 10:05
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录列表
 */
public class CustomBean implements Serializable {

    private String code;
    private String msg;
    private Integer total;
    private Integer size;
    private Integer current;
    private Integer pages;
    private List<RecordsDTO> records;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<RecordsDTO> getRecords() {
        return records;
    }

    public void setRecords(List<RecordsDTO> records) {
        this.records = records;
    }

    public static class RecordsDTO implements Serializable {
        private String telId;
        private String toId;
        private String receiveName;
        private String receiveUrl;
        private String telType;
        private Integer duration;
        private Integer unReceivedNum = 0;
        private String phone;
        private String creatorId;
        private String startName;
        private String createTime;
        private String startUrl;
        private boolean isCreate = true;

        public String getTelId() {
            return telId;
        }

        public void setTelId(String telId) {
            this.telId = telId;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public String getReceiveName() {
            return receiveName;
        }

        public void setReceiveName(String receiveName) {
            this.receiveName = receiveName;
        }

        public String getReceiveUrl() {
            return receiveUrl;
        }

        public void setReceiveUrl(String receiveUrl) {
            this.receiveUrl = receiveUrl;
        }

        public String getTelType() {
            return telType;
        }

        public void setTelType(String telType) {
            this.telType = telType;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(String creatorId) {
            this.creatorId = creatorId;
        }

        public String getStartName() {
            return startName;
        }

        public void setStartName(String startName) {
            this.startName = startName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getStartUrl() {
            return startUrl;
        }

        public void setStartUrl(String startUrl) {
            this.startUrl = startUrl;
        }

        public void setCreate(boolean create) {
            isCreate = create;
        }

        public boolean isCreate() {
            return isCreate;
        }

        public void setUnReceivedNum(Integer unReceivedNum) {
            this.unReceivedNum = unReceivedNum;
        }

        public Integer getUnReceivedNum() {
            return unReceivedNum;
        }
    }
}
