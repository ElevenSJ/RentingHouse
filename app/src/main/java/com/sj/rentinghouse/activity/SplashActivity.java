package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jady.retrofitclient.HttpManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.BuildConfig;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.rentinghouse.fragment.MainFragment;
import com.sj.rentinghouse.fragment.MessageFragment;
import com.sj.rentinghouse.fragment.MyFragment;
import com.sj.rentinghouse.fragment.TrackFragment;
import com.sj.rentinghouse.http.UrlConfig;
import com.sj.rentinghouse.utils.SPName;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间: on 2018/3/30.
 * 创建人: 孙杰
 * 功能描述:
 */
public class SplashActivity extends AppCompatActivity {

    static {
        ViewManager.getInstance().addFragment(0, new MainFragment());
        ViewManager.getInstance().addFragment(1, new TrackFragment());
        ViewManager.getInstance().addFragment(2, new MessageFragment());
        ViewManager.getInstance().addFragment(3, new MyFragment());
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toGoNext();
    }

    private void initTools() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
        HttpManager.init(this.getApplicationContext(), UrlConfig.USERSERVICE_BASE_URL);
        HttpManager.getInstance().setOnGetHeadersListener(new HttpManager.OnGetHeadersListener() {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-type", "application/json; charset=utf-8");
                return headers;
            }
        });
    }


    private void toGoNext() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                if ((Boolean) SPUtils.getInstance().getSharedPreference(SPName.IS_LOGIN, false)) {
                    intent.setClass(SplashActivity.this, MainActivity.class);
                } else {
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
            }
        }, 800);
        initTools();
    }
}
