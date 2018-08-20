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
     * 七牛文件外部地址
     */
    public static final String QINIU_DOMAIN_URL ="https://public.app-storage-node.com/";

    /**
     * 用户服务
     */
    //baseUrl
    public static final String USERSERVICE_BASE_URL = "https://userservice.app-service-node.com";
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
    public static final String ORDERSERVICE_BASE_URL = "https://orderservice.app-service-node.com";
    //添加约看
    public static final String ADD_ORDER_URL = "/addOrder";
    //约看状态更新
    public static final String UPDATE_ORDER_STATUS_URL = "/updateOrderStatus";
    //房东约看信息详情
    public static final String QUERY_ORDER_URL = "/queryOrderInfo";
    //我的约看列表
    public static final String ORDER_LIST_URL = "/orderList";
    //我的约看完成列表
    public static final String CLOSE_ORDER_LIST_URL = "/closeOrderList";
    //房东约看管理列表
    public static final String OWNER_URL = "/ownerList";
    //我的约看总数
    public static final String MY_ORDER_COUNT_URL = "/getOrderCount";
    //系统消息列表
    public static final String NOTICE_LIST_URL = "/noticeList";
    //约看详情获取手机号码
    public static final String QUERY_ORDER_PHONE_URL = "/queryPhone";


    /**
     * 房源服务
     */
    //baseUrl
    public static final String HOUSESERVICE_BASE_URL = "https://houseservice.app-service-node.com";
    //首页查询房产列表
    public static final String ALL_HOUSE_LIST_URL = "/allHouseList";
    //首页查询房产列表价格筛选
    public static final String ALL_HOUSE_RANGE_RENT_LIST_URL = "/rangeRent";
    //添加/修改 房屋信息
    public static final String ADD_HOUSE_URL = "/addHouse";
    //查询房产详情
    public static final String QUERY_HOUSE_URL = "/queryHouseInfo";
    //房东查询房产列表
    public static final String HOUSE_LIST_URL = "/houseList";
    //删除房产
    public static final String DEL_HOUSE_URL = "/delHouse";
    //新增拨号记录接口
    public static final String ADD_DIALING_RECORD_URL = "/addDialingRecord";
    //拨号记录列表
    public static final String DIALING_RECORD_LIST_URL = "/dialingRecordList";
    //房东查询已租房产列表
    public static final String HOUSE_LIST_LET_URL = "/houseListLet";
    //房东查询未租房产列表
    public static final String HOUSE_LIST_LET_NO_URL = "/houseListLetNo";
    //房东查询即将到期房产列表
    public static final String HOUSE_LIST_LET_EXPIRED_URL = "/houseListLetExpired";
    //更新房产信息的租房状态
    public static final String UP_HOUSE_STATUS_URL = "/upHouseStatus";
    //热词接口
    public static final String HOT_WORD_URL = "/hotWord";
    //我的房产总数
    public static final String MY_HOUSE_COUNT_URL = "/getMyHouseCount";
    //获取手机号码
    public static final String QUERY_HOUSE_PHONE_URL = "/queryPhone";

    /**
     * 系统服务
     */
    //baseUrl
    public static final String SYSTEMSERVICE_BASE_URL = "https://systemservices.app-service-node.com";
    //城市列表
    public static final String CITY_LIST_URL = "/cityList";
    //首页轮播图
    public static final String HEAD_PICTURE_URL = "/headPicture";
    //七牛上传token接口
    public static final String QINNIU_TOKEN_URL = "/getToken";
    //获得城市地铁线路
    public static final String GET_TRAIN_LINE_URL = "/getLine";
    //城市地铁线路地铁站
    public static final String GET_SUBWAY_URL = "/getSubway";
}
