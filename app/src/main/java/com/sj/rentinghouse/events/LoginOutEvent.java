package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/19.
 */

public class LoginOutEvent {
    boolean isSuccess = false;
    public LoginOutEvent(boolean isSuccess){
        this.isSuccess = isSuccess;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
}
