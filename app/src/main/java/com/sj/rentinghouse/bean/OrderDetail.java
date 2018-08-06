package com.sj.rentinghouse.bean;

/**
 * Created by Sunj on 2018/7/22.
 */

public class OrderDetail {

    /**
     * note : 单身女性
     * showTime : 2018-03-04 12:22:33
     * name : 李大圣
     * phone : ”15194785896”
     */

    private String note;
    private String showTime;
    private String name;
    private String phone; // FIXME check this code

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
