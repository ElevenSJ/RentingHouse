package com.sj.module_lib.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Sunj on 2018/7/8.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    String[] titles;
    private List<Fragment> mFragments;

    public FragmentAdapter(FragmentManager fm,String[] titles,List<Fragment> mFragments) {
        super(fm);
        this.titles = titles;
        this.mFragments = mFragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}
