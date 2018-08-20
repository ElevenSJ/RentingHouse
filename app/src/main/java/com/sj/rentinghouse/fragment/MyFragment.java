package com.sj.rentinghouse.fragment;

import android.support.v4.app.Fragment;

import com.sj.module_lib.adapter.FragmentAdapter;
import com.sj.module_lib.widgets.NoScrollViewPager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.events.MyPageSwitchEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MyFragment extends AppBaseFragment {
    @BindView(R.id.my_pager)
    NoScrollViewPager containerPager;

    List<Fragment> fragmentList = new ArrayList<>(2);
    FragmentAdapter mAdapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_my;
    }

    @Override
    public void init() {
        super.init();
        fragmentList.add(MyRenterFragment.newInstance());
        fragmentList.add(MyLandlordFragment.newInstance());
    }


    @Override
    public void initEvent() {
        super.initEvent();
        mAdapter = new FragmentAdapter(getChildFragmentManager(),new String[]{"房客","房东"}, fragmentList);
        containerPager.setPagerEnabled(false);
        containerPager.setAdapter(mAdapter);
        containerPager.setCurrentItem(0);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageSwitch(MyPageSwitchEvent event){
        containerPager.setCurrentItem(event.getMsg(),true);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();

    }
}
