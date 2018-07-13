package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/12.
 */

public class MyPageSwitchEvent {
    private int mMsg;
    public MyPageSwitchEvent(int msg) {
        mMsg = msg;
    }
    public int getMsg(){
        return mMsg;
    }
}
