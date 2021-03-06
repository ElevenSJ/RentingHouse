package com.sj.module_lib.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.utils.Utils;


/**
 * 创建时间: on 2018/3/29.
 * 创建人: 孙杰
 * 功能描述:应用入口基础类
 */

public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        ToastUtils.init(false);
        registerActivityLifecycleCallbacks(new ActivityLifecycleManager());
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 将MultiDex注入到项目中
        MultiDex.install(this);
    }

}
