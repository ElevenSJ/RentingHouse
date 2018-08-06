package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/12.
 */

public class MessagePageSwitchEvent {
    private int mMsg;
    public MessagePageSwitchEvent(int msg) {
        mMsg = msg;
    }
    public int getMsg(){
        return mMsg;
    }
}
