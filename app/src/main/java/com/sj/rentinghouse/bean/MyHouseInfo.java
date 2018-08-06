package com.sj.rentinghouse.bean;

import android.os.Parcel;

/**
 * Created by Sunj on 2018/7/22.
 */

public class MyHouseInfo extends HouseInfo {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public MyHouseInfo() {
    }

    protected MyHouseInfo(Parcel in) {
        super(in);
    }

}
