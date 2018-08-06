package com.sj.rentinghouse.bean;

/**
 * Created by Sunj on 2018/7/22.
 */

public class NoticeInfo {

    /**
     * addTime : 2018-07-22 20:34:02
     * receiver : 922337050536849671633e3f634d92646c0b7c00c9ec1cff587
     * id : 1011383580496
     * content : 江宁万达小区2018-7-22 23:12的看房行程已预约成功。
     * status : 1
     */

    private String addTime;
    private String receiver;
    private String id;
    private String content;
    private String status;

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
