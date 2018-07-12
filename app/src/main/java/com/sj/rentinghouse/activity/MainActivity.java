package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import com.sj.module_lib.adapter.FragmentStateAdapter;
import com.sj.module_lib.utils.BottomNavigationViewHelper;
import com.sj.module_lib.utils.StatusBarUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.module_lib.widgets.NoScrollViewPager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;

import butterknife.BindView;

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
                    containerPager.setCurrentItem(0,true);
                    return true;
                case R.id.navigation_track:
                    containerPager.setCurrentItem(1,true);
                    return true;
                case R.id.navigation_notifications:
                    containerPager.setCurrentItem(2,true);
                    return true;
                case R.id.navigation_my:
                    containerPager.setCurrentItem(3,true);
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
    public void initEvent() {
        super.initEvent();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        initViewPager();
    }

    private void initViewPager() {
        mAdapter = new FragmentStateAdapter(getSupportFragmentManager(), ViewManager.getInstance().getAllFragment());
        containerPager.setOffscreenPageLimit(3);
        containerPager.setPagerEnabled(false);
        containerPager.setAdapter(mAdapter);
        containerPager.setCurrentItem(0);
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

}
