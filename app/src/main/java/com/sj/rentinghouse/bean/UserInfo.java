package com.sj.rentinghouse.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sunj on 2018/7/19.
 */

public class UserInfo implements Parcelable {

    /**
     * registTime : 2018-07-13 20:51:19
     * imgUrl :
     * idCard :
     * sex : 0
     * nickname :
     * name :
     * userId : 15605198042
     * email :
     */

    private String registTime;
    private String imgUrl;
    private String idCard;
    private String sex;
    private String nickname;
    private String name;
    private String userId;
    private String email;

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.registTime);
        dest.writeString(this.imgUrl);
        dest.writeString(this.idCard);
        dest.writeString(this.sex);
        dest.writeString(this.nickname);
        dest.writeString(this.name);
        dest.writeString(this.userId);
        dest.writeString(this.email);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.registTime = in.readString();
        this.imgUrl = in.readString();
        this.idCard = in.readString();
        this.sex = in.readString();
        this.nickname = in.readString();
        this.name = in.readString();
        this.userId = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
