package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/19.
 */

public class LoginEvent {
    boolean isSuccess = false;
    public LoginEvent(boolean isSuccess){
        this.isSuccess = isSuccess;
    }
    public boolean isSuccess() {
        return isSuccess;
    }
}
