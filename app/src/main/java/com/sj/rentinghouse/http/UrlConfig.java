package com.sj.rentinghouse.http;

import android.support.annotation.Keep;

/**
 * 创建时间: on 2018/3/31.
 * 创建人: 孙杰
 * 功能描述:请求地址配置
 */
@Keep
public class UrlConfig {
    /**
     * 用户服务
     */
    //baseUrl
    public static final String USERSERVICE_BASE_URL = "http://userservice.app-service-node.com";
    //获取验证码
    public static final String GET_SMSCODE_URL = "/sendMsgCode";
    //注册
    public static final String REGISTER_URL = "/register";
    //密码登录
    public static final String LOGIN_PWD_URL = "/pwdLogin";
    //验证码登录
    public static final String LOGIN_CODE_URL = "/msgCodeLogin";
    //忘记密码
    public static final String FORGET_PWD_URL = "/forgetPwd";
    //注销
    public static final String LOGIN_OUT_URL = "/out";
    //获取个人信息
    public static final String QUERY_USERINFO_URL = "/queryUserInfo";
    //更新个人信息
    public static final String UPDATE_USERINFO_URL = "/updateUserInfo";
    //密码修改
    public static final String CHANGE_PWD_URL = "/changePassword";

    /**
     * 约看服务
     */
    //baseUrl
    public static final String ORDERSERVICE_BASE_URL = "http://orderservice.app-service-node.com";
    //我的约看列表
    public static final String ORDER_LIST_URL = "/orderList";
    //我的约看完成列表
    public static final String CLOSE_ORDER_LIST_URL = "/closeOrderList";


    /**
     * 房源服务
     */
    //baseUrl
    public static final String HOUSESERVICE_BASE_URL = "http://houseservice.app-service-node.com";
    //首页查询房产列表
    public static final String ALL_HOUSE_LIST_URL = "/allHouseList";
}
