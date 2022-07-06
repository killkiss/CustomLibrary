package com.push.jzb.notification;

/**
 * create：2022/6/20 10:49
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class NotifyBean {

    public NotifyBean(String ext){
        this.ext = ext;
    }

    private String ext;

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }
}
