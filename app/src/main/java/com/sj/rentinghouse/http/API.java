package com.sj.rentinghouse.http;

import android.util.ArrayMap;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.HttpCallback;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.http.request.RegisterRequest;
import com.sj.rentinghouse.utils.SPName;

import java.util.Map;

/**
 * Created by Sunj on 2018/7/7.
 */

public class API {

    /**
     * 获取短信验证码
     * @param phoneNum
     * @param callback
     */
    public static void getSMSCode(String phoneNum , HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("telephone", phoneNum);
        HttpManager.get(UrlConfig.GET_SMSCODE_URL, parameters, callback);
    }

    /**
     * 注册
     * @param phoneNum
     * @param smsCode
     * @param pwd
     * @param callback
     */
    public static void doRegister(String phoneNum ,String smsCode,String pwd, HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", smsCode);
        parameters.put("pwd", pwd);
        HttpManager.get(UrlConfig.REGISTER_URL, parameters, callback);
    }

    /**
     * 密码登录
     * @param phoneNum
     * @param pwd
     * @param deviceId
     * @param callback
     */
    public static void doLoginPwd(String phoneNum ,String pwd,String deviceId, HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("pwd", pwd);
        parameters.put("deviceId", deviceId);
        HttpManager.get(UrlConfig.LOGIN_PWD_URL, parameters, callback);
    }

    /**
     * 验证码登录
     * @param phoneNum
     * @param msgCode
     * @param deviceId
     * @param callback
     */
    public static void doLoginCode(String phoneNum ,String msgCode,String deviceId, HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", msgCode);
        parameters.put("deviceId", deviceId);
        HttpManager.get(UrlConfig.LOGIN_CODE_URL, parameters, callback);
    }

    /**
     * 忘记密码
     * @param phoneNum
     * @param msgCode
     * @param newPwd1
     * @param newPwd2
     * @param callback
     */
    public static void forgetPwd(String phoneNum ,String msgCode,String newPwd1, String newPwd2,HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("userId", phoneNum);
        parameters.put("msgCode", msgCode);
        parameters.put("newPwd1", newPwd1);
        parameters.put("newPwd2", newPwd2);
        HttpManager.get(UrlConfig.FORGET_PWD_URL, parameters, callback);
    }

    /**
     * 获取个人信息
     * @param callback
     */
    public static void queryUserInfo(HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        HttpManager.get(UrlConfig.QUERY_USERINFO_URL, parameters, callback);
    }

    /**
     * 更新个人信息
     * @param parameters
     * @param callback
     */
    public static void updateUserInfo(Map<String, Object> parameters ,HttpCallback callback){
        HttpManager.get(UrlConfig.UPDATE_USERINFO_URL, parameters, callback);
    }

    /**
     * 退出
     * @param callback
     */
    public static void out(HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        HttpManager.get(UrlConfig.QUERY_USERINFO_URL, parameters, callback);
    }

    /**
     * 密码修改
     * @param oldPwd
     * @param newPwd1
     * @param newPwd2
     * @param callback
     */
    public static void changePassword(String oldPwd ,String newPwd1 ,String newPwd2 ,HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        parameters.put("oldPwd", oldPwd);
        parameters.put("newPwd1", newPwd1);
        parameters.put("newPwd2", newPwd2);
        HttpManager.get(UrlConfig.QUERY_USERINFO_URL, parameters, callback);
    }


    /**
     * 我的约看
     * @param firstIndex
     * @param pageNum
     * @param callback
     */
    public static void orderList(String firstIndex ,int pageNum ,HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        parameters.put("firstIndex", firstIndex);
        parameters.put("pageNum", pageNum);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ORDER_LIST_URL, parameters, callback);
    }

    /**
     * 我的约看完成列表
     * @param firstIndex
     * @param pageNum
     * @param callback
     */
    public static void closeOrderList(String firstIndex ,int pageNum ,HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        parameters.put("firstIndex", firstIndex);
        parameters.put("pageNum", pageNum);
        HttpManager.setTmpBaseUrl(UrlConfig.ORDERSERVICE_BASE_URL);
        HttpManager.get(UrlConfig.CLOSE_ORDER_LIST_URL, parameters, callback);
    }

    /**
     * 首页查询房产列表
     * @param firstIndex
     * @param pageNum
     * @param callback
     */
    public static void allHouseList(String firstIndex ,int pageNum ,HttpCallback callback){
        Map<String, Object> parameters = new ArrayMap<>();
        parameters.put("token", SPUtils.getInstance().getSharedPreference(SPName.TOKEN_ID,""));
        parameters.put("firstIndex", firstIndex);
        parameters.put("pageNum", pageNum);
        HttpManager.setTmpBaseUrl(UrlConfig.HOUSESERVICE_BASE_URL);
        HttpManager.get(UrlConfig.ALL_HOUSE_LIST_URL, parameters, callback);
    }

}
