package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.module_lib.adapter.FragmentStateAdapter;
import com.sj.module_lib.utils.ViewManager;
import com.sj.module_lib.widgets.NoScrollViewPager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MyFragment extends AppBaseFragment {
    @BindView(R.id.my_pager)
    NoScrollViewPager containerPager;

    List<Fragment> fragmentList = new ArrayList<>(2);
    FragmentStateAdapter mAdapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_my;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        initViewPager();
    }

    private void initViewPager() {
        fragmentList.add(MyRenterFragment.newInstance());
        fragmentList.add(MyLandlordFragment.newInstance());

        mAdapter = new FragmentStateAdapter(getChildFragmentManager(), fragmentList);
        containerPager.setPagerEnabled(false);
        containerPager.setAdapter(mAdapter);
        containerPager.setCurrentItem(0);
    }
}
