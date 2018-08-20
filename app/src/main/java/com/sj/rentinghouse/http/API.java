package com.sj.rentinghouse.http;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.HttpCallback;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.bean.HouseDetail;
import com.sj.rentinghouse.bean.MyHouseInfo;
import com.sj.rentinghouse.utils.NameSpace;

import java.util.Map;

/**
 * Created by Sunj on 2018/7/7.
 */

public class API {

    //------------------------------------------------------用户服务------------------------------------------

    /**
     * 获取短信验证码
     *
     * @param phoneNum
     * @param callback
     */
    public static void getSMSCode(String phoneNum, String from, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("telephone", phoneNum);
        parameters.put("from", from);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.GET_SMSCODE_URL, parameters, callback);
    }

    /**
     * 注册
     *
     * @param phoneNum
     * @param smsCode
     * @param pwd
     * @param callback
     */
    public static void doRegister(String phoneNum, String smsCode, String pwd, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", smsCode);
        parameters.put("registerCityName", SPUtils.getInstance().getSharedPreference(NameSpace.LOCAL_CITY_NAME, ""));
        parameters.put("pwd", pwd);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.REGISTER_URL, parameters, callback);
    }

    /**
     * 密码登录
     *
     * @param phoneNum
     * @param pwd
     * @param deviceId
     * @param callback
     */
    public static void doLoginPwd(String phoneNum, String pwd, String deviceId, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("pwd", pwd);
        parameters.put("deviceId", deviceId);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.LOGIN_PWD_URL, parameters, callback);
    }

    /**
     * 验证码登录
     *
     * @param phoneNum
     * @param msgCode
     * @param deviceId
     * @param callback
     */
    public static void doLoginCode(String phoneNum, String msgCode, String deviceId, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", msgCode);
        parameters.put("deviceId", deviceId);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.LOGIN_CODE_URL, parameters, callback);
    }

    /**
     * 忘记密码
     *
     * @param phoneNum
     * @param msgCode
     * @param newPwd1
     * @param newPwd2
     * @param callback
     */
    public static void forgetPwd(String phoneNum, String msgCode, String newPwd1, String newPwd2, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", msgCode);
        parameters.put("newPwd1", newPwd1);
        parameters.put("newPwd2", newPwd2);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.FORGET_PWD_URL, parameters, callback);
    }

    /**
     * 获取个人信息
     *
     * @param callback
     */
    public static void queryUserInfo(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QUERY_USERINFO_URL, parameters, callback);
    }

    /**
     * 更新个人信息
     *
     * @param parameters
     * @param callback
     */
    public static void updateUserInfo(Map<String, Object> parameters, HttpCallback callback) {
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.UPDATE_USERINFO_URL, parameters, callback);
    }

    /**
     * 退出
     *
     * @param callback
     */
    public static void out(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.LOGIN_OUT_URL, parameters, callback);
    }

    /**
     * 密码修改
     *
     * @param oldPwd
     * @param newPwd1
     * @param newPwd2
     * @param callback
     */
    public static void changePassword(String oldPwd, String newPwd1, String newPwd2, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("oldPwd", oldPwd);
        parameters.put("newPwd1", newPwd1);
        parameters.put("newPwd2", newPwd2);
        HttpManager.setTmpBaseUrl(UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.CHANGE_PWD_URL, parameters, callback);
    }

    //------------------------------------------------------约看服务------------------------------------------

    /**
     * 添加约看
     *
     * @param houseId
     * @param name
     * @param showTime
     * @param ownerId
     * @param note
     * @param callback
     */
    public static void addOrder(String houseId, String name, String showTime, String ownerId, String note, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("houseId", houseId);
        parameters.put("name", name);
        parameters.put("showTime", showTime);
        parameters.put("ownerId", ownerId);
        parameters.put("note", note);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ADD_ORDER_URL, parameters, callback);
    }

    /**
     * 约看状态更新接口
     *
     * @param id
     * @param status
     * @param callback
     */
    public static void updateOrderStatus(String id, String status, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        parameters.put("status", status);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.UPDATE_ORDER_STATUS_URL, parameters, callback);
    }

    /**
     * 房东约看信息详情页面
     *
     * @param id
     * @param callback
     */
    public static void queryOrderInfo(String id, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QUERY_ORDER_URL, parameters, callback);
    }

    /**
     * 我的约看
     *
     * @param firstIndex
     * @param callback
     */
    public static void orderList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ORDER_LIST_URL, parameters, callback);
    }

    /**
     * 我的约看完成列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void closeOrderList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.CLOSE_ORDER_LIST_URL, parameters, callback);
    }

    /**
     * 房东约看管理列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void ownerList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.OWNER_URL, parameters, callback);
    }

    /**
     * 我的约看总数
     *
     * @param callback
     */
    public static void getOrderCount(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.MY_ORDER_COUNT_URL, parameters, callback);
    }

    /**
     * 系统消息列表
     *
     * @param callback
     */
    public static void noticeList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.NOTICE_LIST_URL, parameters, callback);
    }

    /**
     * 约看查询手机号
     *
     * @param callback
     */
    public static void queryOrderPhone(String id, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QUERY_ORDER_PHONE_URL, parameters, callback);
    }

    //------------------------------------------------------房源服务------------------------------------------

    /**
     * 房源查询手机号
     *
     * @param callback
     */
    public static void queryHousePhone(String id, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QUERY_HOUSE_PHONE_URL, parameters, callback);
    }

    /**
     * 添加/修改 房屋信息
     *
     * @param houseDetail
     * @param callback
     */
    public static void addHouse(HouseDetail houseDetail, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters = (Map<String, Object>) JSON.parse(JSON.toJSONString(houseDetail));
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ADD_HOUSE_URL, parameters, callback);
    }

    /**
     * 查询房产详情
     *
     * @param id
     * @param callback
     */
    public static void queryHouseInfo(String id, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QUERY_HOUSE_URL, parameters, callback);
    }

    /**
     * 首页查询房产列表
     *
     * @param map
     * @param firstIndex
     * @param callback
     */
    public static void allHouseList(Map<String, Object> map, String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.putAll(map);
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        //价格刷选使用另外一个地址
        if (parameters.containsKey("startRent") && !TextUtils.isEmpty(parameters.get("startRent").toString()) && !parameters.get("endRent").toString().equals("0")) {
            HttpManager.get(UrlConfig.ALL_HOUSE_RANGE_RENT_LIST_URL, parameters, callback);
        } else {
            HttpManager.get(UrlConfig.ALL_HOUSE_LIST_URL, parameters, callback);
        }
    }

    /**
     * 房东查询房产列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void houseList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HOUSE_LIST_URL, parameters, callback);
    }

    /**
     * 删除房产
     *
     * @param id
     * @param callback
     */
    public static void delHouse(String id, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.DEL_HOUSE_URL, parameters, callback);
    }

    /**
     * 新增拨号记录
     *
     * @param houseId
     * @param callback
     */
    public static void addDialingRecord(String houseId, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("houseId", houseId);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ADD_DIALING_RECORD_URL, parameters, callback);
    }

    /**
     * 拨号记录列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void dialingRecordList(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.DIALING_RECORD_LIST_URL, parameters, callback);
    }

    /**
     * 房东查询已租房产列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void houseListLet(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HOUSE_LIST_LET_URL, parameters, callback);
    }

    /**
     * 房东查询未租房产列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void houseListLetNo(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HOUSE_LIST_LET_NO_URL, parameters, callback);
    }

    /**
     * 房东查询即将到期房产列表
     *
     * @param firstIndex
     * @param callback
     */
    public static void houseListLetExpired(String firstIndex, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("firstIndex", firstIndex == null ? "" : firstIndex);
        parameters.put("pageNum", 10);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HOUSE_LIST_LET_EXPIRED_URL, parameters, callback);
    }

    /**
     * 更新房产信息的租房状态
     *
     * @param id
     * @param startTime
     * @param endTime
     * @param callback
     */
    public static void upHouseStatus(String id, String startTime, String endTime, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        parameters.put("id", id);
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.UP_HOUSE_STATUS_URL, parameters, callback);
    }


    /**
     * 我的房产总数
     *
     * @param callback
     */
    public static void getMyHouseCount(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.MY_HOUSE_COUNT_URL, parameters, callback);
    }


    //------------------------------------------------------系统服务------------------------------------------

    /**
     * 热词接口
     *
     * @param callback
     */
    public static void hotWord(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("cityCode", SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HOT_WORD_URL, parameters, callback);
    }

    /**
     * 获取城市列表
     *
     * @param callback
     */
    public static void cityList(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.CITY_LIST_URL, parameters, callback);
    }

    /**
     * 获取轮播图
     *
     * @param callback
     */
    public static void headPicture(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
//        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID,""));
        parameters.put("cityId", SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.HEAD_PICTURE_URL, parameters, callback);
    }

    /**
     * 获取七牛token
     *
     * @param callback
     */
    public static void getQINIUToken(HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(NameSpace.TOKEN_ID, ""));
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.QINNIU_TOKEN_URL, parameters, callback);
    }

    /**
     * 获得城市地铁线路
     *
     * @param cityId
     * @param callback
     */
    public static void getLine(String cityId, HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("cityId", cityId);
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.GET_TRAIN_LINE_URL, parameters, callback);
    }

    /**
     * 城市地铁线路地铁站
     *
     * @param callback
     */
    public static void getSubway(String cityId,String line,HttpCallback callback) {
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("cityId", cityId);
        parameters.put("line", line==null?"":line);
        HttpManager.setTmpBaseUrl(UrlConfig.SYSTEMSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.GET_SUBWAY_URL, parameters, callback);
    }

}
