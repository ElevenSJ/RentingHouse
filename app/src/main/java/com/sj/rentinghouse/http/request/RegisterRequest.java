package com.sj.rentinghouse.http.request;

/**
 * Created by Sunj on 2018/7/8.
 */

public class RegisterRequest {
    String userId;
    String msgCode;
    String pwd;

    public RegisterRequest(String userId, String msgCode, String pwd) {
        this.userId = userId;
        this.msgCode = msgCode;
        this.pwd = pwd;
    }
}
