package com.sj.rentinghouse.app;

import android.content.res.TypedArray;

import com.alibaba.fastjson.JSON;
import com.sj.im.SharePreferenceManager;
import com.sj.module_lib.base.BaseApplication;
import com.sj.module_lib.service.LocationService;
import com.sj.module_lib.utils.FileToolUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.events.NotificationClickEventReceiver;
import com.sj.rentinghouse.fragment.MainFragment;
import com.sj.rentinghouse.fragment.MessageFragment;
import com.sj.rentinghouse.fragment.MyFragment;
import com.sj.rentinghouse.fragment.TrackFragment;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.zaaach.citypicker.model.City;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;

/**
 * 创建时间: on 2018/6/17.
 * 创建人: 孙杰
 * 功能描述:
 */
public class App extends BaseApplication {
    public static String[] directionArray;
    public static String[] statusArray;
    public static String[] rentStatusArray;
    public static String[] roomTypeArray;
    public static String[] typeArray;
    public static String[] facilityKeyArray;
    public static String[] facilityArray;
    public static int[] facilitiesResIds;
    public static String[] renovationArray;
    public static String[] payMethodArray;
    public static Map<String, Map<String, String>> allCityMap;
    public static Map<String, String> cityMap = new HashMap<>();

    //城市列表
    public static List<City> allCities;
    //极光IM
    private static String JCHAT_CONFIGS = "jpush_configs";
    public static final String CONV_TITLE = "conv_title";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String GROUP_ID = "groupId";

    public static String PICTURE_DIR = "sdcard/RentHouse/pictures/";
    public static String FILE_DIR = "sdcard/RentHouse/recvFiles/";

    static {
        ViewManager.getInstance().addFragment(0, new MainFragment());
        ViewManager.getInstance().addFragment(1, new TrackFragment());
        ViewManager.getInstance().addFragment(2, new MessageFragment());
        ViewManager.getInstance().addFragment(3, new MyFragment());
    }

    private static App app;
    private LocationService locationService;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        locationService = new LocationService(getApplicationContext());
        initData();
        CrashReport.initCrashReport(getApplicationContext(), "2b0bf1c2d2", false);

        JMessageClient.init(getApplicationContext());
        JMessageClient.setDebugMode(true);
        SharePreferenceManager.init(this, JCHAT_CONFIGS);
        //设置Notification的模式
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), null);
    }

    public void initData() {
        directionArray = app.getResources().getStringArray(R.array.house_direction);
        statusArray = app.getResources().getStringArray(R.array.house_order_status);
        rentStatusArray = app.getResources().getStringArray(R.array.house_rent_status);
        roomTypeArray = app.getResources().getStringArray(R.array.house_type_room);
        typeArray = app.getResources().getStringArray(R.array.house_type);
        facilityArray = app.getResources().getStringArray(R.array.house_facilities);
        facilityKeyArray = app.getResources().getStringArray(R.array.house_facilities_key);
        renovationArray = app.getResources().getStringArray(R.array.house_renovation);
        payMethodArray= app.getResources().getStringArray(R.array.house_pay_method);
        TypedArray ar = getResources().obtainTypedArray(R.array.house_facilities_resid);
        int len = ar.length();
        facilitiesResIds = new int[len];
        for (int i = 0; i < len; i++) {
            facilitiesResIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        allCityMap = (Map<String, Map<String, String>>) JSON.parse(FileToolUtils.ReadAssetsString(app, "city.json"));
        Map<String, String> provinceMap = allCityMap.get("1");
        for (Map.Entry<String, String> province : provinceMap.entrySet()) {
            cityMap.putAll(allCityMap.get(province.getKey()));
        }
    }

    public LocationService getLocationService() {
        return locationService;
    }

    public static App getApp() {
        return app;
    }
}
