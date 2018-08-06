package com.sj.module_lib.events;

import com.baidu.location.BDLocation;

/**
 * Created by Sunj on 2018/7/12.
 */

public class LocationEvent {
    private BDLocation mLocation;
    public LocationEvent(BDLocation location) {
        mLocation = location;
    }
    public BDLocation getLocation(){
        return mLocation;
    }
}
