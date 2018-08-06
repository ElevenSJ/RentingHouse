package com.sj.rentinghouse.bean;

import android.os.Parcel;

/**
 * Created by Sunj on 2018/7/18.
 */

public class MyOrderInfo extends HouseInfo{
    String houseId;
    String showTime;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.houseId);
        dest.writeString(this.showTime);
    }

    public MyOrderInfo() {
    }

    protected MyOrderInfo(Parcel in) {
        super(in);
        this.houseId = in.readString();
        this.showTime = in.readString();
    }

    public static final Creator<MyOrderInfo> CREATOR = new Creator<MyOrderInfo>() {
        @Override
        public MyOrderInfo createFromParcel(Parcel source) {
            return new MyOrderInfo(source);
        }

        @Override
        public MyOrderInfo[] newArray(int size) {
            return new MyOrderInfo[size];
        }
    };
}
