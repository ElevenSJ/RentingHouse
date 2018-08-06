package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/12.
 */

public class TrackPageSwitchEvent {
    private int mMsg;
    public TrackPageSwitchEvent(int msg) {
        mMsg = msg;
    }
    public int getMsg(){
        return mMsg;
    }
}
