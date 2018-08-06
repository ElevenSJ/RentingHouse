package com.sj.rentinghouse.bean;

import android.os.Parcel;

/**
 * Created by Sunj on 2018/7/22.
 */

public class MyLetHouseInfo extends MyHouseInfo {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public MyLetHouseInfo() {
    }

    protected MyLetHouseInfo(Parcel in) {
        super(in);
    }

    public static final Creator<MyLetHouseInfo> CREATOR = new Creator<MyLetHouseInfo>() {
        @Override
        public MyLetHouseInfo createFromParcel(Parcel source) {
            return new MyLetHouseInfo(source);
        }

        @Override
        public MyLetHouseInfo[] newArray(int size) {
            return new MyLetHouseInfo[size];
        }
    };
}
