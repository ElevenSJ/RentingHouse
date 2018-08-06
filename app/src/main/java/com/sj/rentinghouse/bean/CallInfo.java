package com.sj.rentinghouse.bean;

import android.os.Parcel;

/**
 * Created by Sunj on 2018/7/18.
 */

public class CallInfo extends HouseInfo {
    String houseId;

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.houseId);
    }

    public CallInfo() {
    }

    protected CallInfo(Parcel in) {
        super(in);
        this.houseId = in.readString();
    }

    public static final Creator<CallInfo> CREATOR = new Creator<CallInfo>() {
        @Override
        public CallInfo createFromParcel(Parcel source) {
            return new CallInfo(source);
        }

        @Override
        public CallInfo[] newArray(int size) {
            return new CallInfo[size];
        }
    };
}
