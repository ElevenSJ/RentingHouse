package com.sj.rentinghouse.events;

import com.sj.rentinghouse.bean.CityInfo;

import java.util.List;

/**
 * Created by Sunj on 2018/7/12.
 */

public class InitCityListEvent {
    List<CityInfo> cityInfos;
    public InitCityListEvent(List<CityInfo> cityInfos) {
        this.cityInfos = cityInfos;
    }

    public  List<CityInfo> getCityInfo() {
        return cityInfos;
    }

    public void setCityInfo( List<CityInfo> cityInfo) {
        this.cityInfos = cityInfo;
    }
}
