package com.sj.rentinghouse.bean;

/**
 * Created by Sunj on 2018/7/17.
 */

public class SearchWord {
    private String cityCode;
    private String village;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    @Override
    public String toString() {
        return "{" +
                "cityCode='" + cityCode + '\'' +
                ", village='" + village + '\'' +
                '}';
    }
}
