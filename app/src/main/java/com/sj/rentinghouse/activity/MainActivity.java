package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.adapter.FragmentStateAdapter;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.utils.BottomNavigationViewHelper;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.module_lib.widgets.NoScrollViewPager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.events.LoginOutEvent;
import com.sj.rentinghouse.events.MainPageSwitchEvent;
import com.sj.rentinghouse.fragment.MainFragment;
import com.sj.rentinghouse.fragment.MessageFragment;
import com.sj.rentinghouse.fragment.MyFragment;
import com.sj.rentinghouse.fragment.TrackFragment;
import com.sj.rentinghouse.utils.NameSpace;
import com.zaaach.citypicker.model.City;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import cn.jiguang.api.JCoreInterface;
import cn.jpush.im.android.api.JMessageClient;

public class MainActivity extends AppBaseActivity {


    @BindView(R.id.container_pager)
    NoScrollViewPager containerPager;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    FragmentStateAdapter mAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    containerPager.setCurrentItem(0, true);
                    return true;
                case R.id.navigation_track:
                    containerPager.setCurrentItem(1, true);
                    return true;
                case R.id.navigation_notifications:
                    containerPager.setCurrentItem(2, true);
                    return true;
                case R.id.navigation_my:
                    containerPager.setCurrentItem(3, true);
                    return true;
            }
            return false;
        }
    };


    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }


    @Override
    public void init() {
        super.init();
        EventBus.getDefault().register(this);
        if (ViewManager.getInstance().getAllFragment() == null || ViewManager.getInstance().getAllFragment().isEmpty()) {
            ViewManager.getInstance().addFragment(0, new MainFragment());
            ViewManager.getInstance().addFragment(1, new TrackFragment());
            ViewManager.getInstance().addFragment(2, new MessageFragment());
            ViewManager.getInstance().addFragment(3, new MyFragment());
        }
        mAdapter = new FragmentStateAdapter(getSupportFragmentManager(), ViewManager.getInstance().getAllFragment());
    }

    @Override
    public void initEvent() {
        super.initEvent();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        containerPager.setOffscreenPageLimit(3);
        containerPager.setPagerEnabled(false);
        containerPager.setAdapter(mAdapter);
        navigation.setSelectedItemId(navigation.getMenu().getItem(0).getItemId());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //互斥登录，退出当前设备
        if (intent != null) {
            if (intent.getBooleanExtra("LoginOut", false)) {
                backLogin();
            }
        }
    }

    public void backLogin() {
        Intent i = new Intent();
        i.setClass(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainPageSwitch(MainPageSwitchEvent event) {
        Logger.i("主页切换：" + event.getMsg());
        navigation.setSelectedItemId(navigation.getMenu().getItem(event.getMsg()).getItemId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitApp(LoginOutEvent event) {
        SPUtils.getInstance().commit(new String[]{NameSpace.USER_ACCOUNT, NameSpace.TOKEN_ID, NameSpace.IS_LOGIN}, new Object[]{"", "", false});
        JMessageClient.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        super.onResume();
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    /**
     * 双击返回键退出
     */
    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            ToastUtils.showShortToast("再按一次退出程序");
            firstTime = secondTime;
        } else {
            finish();
            System.exit(0);
        }
    }
}
