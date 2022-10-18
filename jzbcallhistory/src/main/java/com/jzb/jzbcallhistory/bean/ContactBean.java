package com.jzb.jzbcallhistory.bean;

import java.io.Serializable;
import java.util.List;

/**
 * create：2022/8/17 14:50
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class ContactBean implements Serializable {

    private String code;
    private String msg;
    private List<ListDTO> list;

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

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public static class ListDTO implements Serializable {
        private String userId;
        private String username;
        private String nickname;
        private String phone;
        private String areacode;
        private String depId;
        private String idcardNo;
        private String jobStatus;
        private String jobType;
        private String sort;
        private Integer attendanceLevel;
        private String type;
        private String employeeType;
        private DepartDTO depart;
        private Boolean admin;
        private InfoDTO info;
        private ComDTO com;
        private String authScope;
        private String createTime;
        private String unionid;
        private String inviteStatus;
        private String avatarUrl;
        private String signUrl;
        private String email;
        private String staffNo;
        private List<?> roles;
        private List<SubDepartsDTO> subDeparts;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setAreacode(String areacode) {
            this.areacode = areacode;
        }

        public String getAreacode() {
            return areacode;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public DepartDTO getDepart() {
            return depart;
        }

        public void setDepart(DepartDTO depart) {
            this.depart = depart;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getUnionid() {
            return unionid;
        }

        public static class DepartDTO implements Serializable {
            private String depId;
            private String depName;
            private Boolean main;
            private Boolean leader;
            private Boolean manager;
            private Boolean agent;

            public String getDepName() {
                return depName;
            }

            public void setDepName(String depName) {
                this.depName = depName;
            }

            public void setDepId(String depId) {
                this.depId = depId;
            }

            public String getDepId() {
                return depId;
            }
        }

        public static class InfoDTO implements Serializable {
            private String gender;
        }

        public static class ComDTO implements Serializable {
            private String comId;
        }

        public static class SubDepartsDTO implements Serializable {
            private String depId;
            private String depName;
            private Boolean main;
            private Boolean leader;
            private Boolean manager;
            private Boolean agent;

            public void setDepId(String depId) {
                this.depId = depId;
            }

            public String getDepId() {
                return depId;
            }

            public void setDepName(String depName) {
                this.depName = depName;
            }

            public String getDepName() {
                return depName;
            }
        }
    }
}
