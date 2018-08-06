package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.module_lib.adapter.FragmentAdapter;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.module_lib.widgets.PagerSlidingTabStrip;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.LoginActivity;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.events.MyPageSwitchEvent;
import com.sj.rentinghouse.events.TrackPageSwitchEvent;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sunj on 2018/7/8.
 */

public class TrackFragment extends AppBaseFragment {

    @BindView(R.id.img_top_left)
    ImageView imgTopLeft;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    @BindString(R.string.title_track)
    String title;

    String[] titles = {"我的约看", "约看完成", "拨号记录"};
    List<Fragment> fragmentList = new ArrayList<>(3);
    FragmentAdapter pageAdapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_track;
    }

    @Override
    public void createInit() {
        super.createInit();
        fragmentList.add(TrackChildFragment.newInstance(0));
        fragmentList.add(TrackChildFragment.newInstance(1));
        fragmentList.add(TrackChildFragment.newInstance(2));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && pageAdapter != null) {
            if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
                DialogUtils.showLoginDialog(this.getContext(), LoginActivity.class);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitle.setText(title);

        pageAdapter = new FragmentAdapter(getChildFragmentManager(), titles, fragmentList);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(pageAdapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageSwitch(TrackPageSwitchEvent event) {
        pager.setCurrentItem(event.getMsg(), true);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
