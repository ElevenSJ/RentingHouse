package com.sj.module_lib.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;

import com.sj.module_lib.widgets.PagerSlidingTabStrip;

import java.util.List;

/**
 * Created by Sunj on 2018/7/8.
 */

public class FragmentTabIconAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    String[] titles;
    int[] resIds;
    private List<Fragment> mFragments;

    public FragmentTabIconAdapter(FragmentManager fm, String[] titles,int[] resIds, List<Fragment> mFragments) {
        super(fm);
        this.titles = titles;
        this.resIds = resIds;
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

    @Override
    public int getPageIconResId(int position) {
        return resIds[position];
    }

    @Override
    public int getIconType() {
        return Gravity.TOP;
    }
}
