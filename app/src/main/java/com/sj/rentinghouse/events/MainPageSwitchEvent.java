package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/12.
 */

public class MainPageSwitchEvent {
    private int mMsg;
    public MainPageSwitchEvent(int msg) {
        mMsg = msg;
    }
    public int getMsg(){
        return mMsg;
    }
}
