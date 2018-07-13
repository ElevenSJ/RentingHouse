package com.sj.rentinghouse.app;

import com.sj.module_lib.base.BaseApplication;
import com.sj.module_lib.utils.ViewManager;
import com.sj.rentinghouse.fragment.MainFragment;
import com.sj.rentinghouse.fragment.MessageFragment;
import com.sj.rentinghouse.fragment.MyFragment;
import com.sj.rentinghouse.fragment.TrackFragment;

/**
 * 创建时间: on 2018/6/17.
 * 创建人: 孙杰
 * 功能描述:
 */
public class App extends BaseApplication {

    static {
        ViewManager.getInstance().addFragment(0, new MainFragment());
        ViewManager.getInstance().addFragment(1, new TrackFragment());
        ViewManager.getInstance().addFragment(2, new MessageFragment());
        ViewManager.getInstance().addFragment(3, new MyFragment());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
