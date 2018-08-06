package com.sj.rentinghouse.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.base.BaseActivity;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.utils.AndroidWorkaround;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.utils.NameSpace;
import com.zaaach.citypicker.model.City;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * 创建时间: on 2018/6/19.
 * 创建人: 孙杰
 * 功能描述:
 */
public abstract class AppBaseActivity extends BaseActivity {
    Unbinder unbinder;
    List<CityInfo> cityInfos ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {                                  //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));                   //需要在setContentView()方法后面执行
        }
        unbinder = ButterKnife.bind(this);

        if (App.allCities==null||App.allCities.isEmpty()){
            if (App.allCities==null){
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
        }
        if (App.directionArray==null||App.directionArray.length==0){
            App.getApp().initData();
        }
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
    @Optional
    @OnClick(R.id.img_top_left)
    public void OnBackViewClick(View view){
        finish();
    }

}
