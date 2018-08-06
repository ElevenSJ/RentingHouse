package com.sj.rentinghouse.events;

import com.sj.rentinghouse.bean.UserInfo;

/**
 * Created by Sunj on 2018/7/19.
 */

public class UserInfoEvent {

    UserInfo userInfo;

    public UserInfoEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
