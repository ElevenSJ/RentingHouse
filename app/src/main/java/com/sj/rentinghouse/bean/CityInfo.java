package com.sj.rentinghouse.bean;

import com.sj.module_lib.utils.PinyinUtils;
import com.zaaach.citypicker.model.City;

import java.io.Serializable;

/**
 * Created by Sunj on 2018/7/14.
 */

public class CityInfo  extends City implements Serializable{


    /**
     * addTime : 2018-07-16 16:34:03
     * cityName : 长沙
     * id : 9223370505124732426549c1d65ad7c403ea067374e5c3314f9
     * status : 1
     */

    private String addTime;
    private String cityName;
    private String id;
    private String status;

    public CityInfo() {
    }

    public CityInfo(String name, String province, String pinyin, String code) {
        super(name, province, pinyin, code);
        this.cityName = name;
        this.id  = code;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return cityName;
    }

    public String getPinyin() {
        return PinyinUtils.getPingYin(cityName);
    }

    public String getProvince() {
        return cityName;
    }

    public String getCode() {
        return id;
    }

}
