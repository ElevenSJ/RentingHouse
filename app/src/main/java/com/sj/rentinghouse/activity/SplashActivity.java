package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.jady.retrofitclient.HttpManager;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.BuildConfig;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.task.SerializeInfoSaveTask;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.http.UrlConfig;
import com.sj.rentinghouse.utils.NameSpace;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zaaach.citypicker.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间: on 2018/3/30.
 * 创建人: 孙杰
 * 功能描述:
 */
public class SplashActivity extends AppCompatActivity {

    List<CityInfo> cityInfos ;

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
//            if ((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
            intent.setClass(SplashActivity.this, MainActivity.class);
//            intent.setClass(SplashActivity.this, IMChatActivity.class);
//            } else {
//                intent.setClass(SplashActivity.this, LoginActivity.class);
//            }
            startActivity(intent);
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTools();
        AndPermission.with(SplashActivity.this)
                .permission(Permission.ACCESS_COARSE_LOCATION,
                        Permission.ACCESS_FINE_LOCATION,
                        Permission.WRITE_EXTERNAL_STORAGE,
                        Permission.READ_PHONE_STATE,
                        Permission.RECORD_AUDIO,
                        Permission.CAMERA)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        getCityList();
                        App.getApp().getLocationService().startDefault();
                        PictureFileUtils.deleteCacheDirFile(SplashActivity.this);
                    }
                }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {
                // TODO what to do
                finish();
            }
        }).start();
    }

    private void initTools() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
        HttpManager.init(this.getApplicationContext(), UrlConfig.USERSERVICE_BASE_URL);
//        HttpManager.getInstance().setOnGetHeadersListener(new HttpManager.OnGetHeadersListener() {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-type", "application/json; charset=utf-8");
//                return headers;
//            }
//        });
    }


    private void toGoNext() {
        handler.sendEmptyMessageDelayed(0,800);
    }
    private void getCityList() {
        API.cityList(new ServerResultBack<BaseResponse<DataList<CityInfo>>, DataList<CityInfo>>() {
            @Override
            public void onSuccess(DataList<CityInfo> data) {
                cityInfos = data.getData();
                if (cityInfos!=null&&cityInfos.size() > 0) {
                    if (App.allCities != null && !App.allCities.isEmpty()) {
                        App.allCities.clear();
                    } else {
                        App.allCities = new ArrayList<>();
                    }
                    for (CityInfo cityInfo : cityInfos) {
                        App.allCities.add(new City(cityInfo.getName(), cityInfo.getProvince(), cityInfo.getPinyin(), cityInfo.getCode()));
                    }
                    new SerializeInfoSaveTask().execute(data.getData(), NameSpace.FILE_CITY_NAME);
                }
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                if (App.allCities != null && !App.allCities.isEmpty()) {
                    App.allCities.clear();
                } else {
                    App.allCities = new ArrayList<>();
                }
                new SerializeInfoGetTask(){
                    @Override
                    protected void onPostExecute(Object obj) {
                        super.onPostExecute(obj);
                        if (obj!=null) {
                            cityInfos = (List<CityInfo>) obj;
                            for (CityInfo cityInfo : cityInfos) {
                                App.allCities.add(new City(cityInfo.getName(), cityInfo.getProvince(), cityInfo.getPinyin(), cityInfo.getCode()));
                            }
                        }
                    }
                }.execute(cityInfos, NameSpace.FILE_CITY_NAME);
//                new SerializeInfoGetTask(){
//                    @Override
//                    protected void onPostExecute(Boolean aBoolean) {
//                        super.onPostExecute(aBoolean);
//                        if (aBoolean&&cityInfos!=null) {
//                            for (CityInfo cityInfo : cityInfos) {
//                                App.allCities.add(new City(cityInfo.getName(), cityInfo.getProvince(), cityInfo.getPinyin(), cityInfo.getCode()));
//                            }
//                        }
//                    }
//                }.execute(cityInfos, NameSpace.FILE_CITY_NAME);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                toGoNext();
            }
        });
    }
}
