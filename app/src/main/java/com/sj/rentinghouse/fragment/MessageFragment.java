package com.sj.rentinghouse.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.module_lib.adapter.FragmentAdapter;
import com.sj.module_lib.adapter.FragmentTabIconAdapter;
import com.sj.module_lib.widgets.PagerSlidingTabStrip;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MessageFragment extends AppBaseFragment {

    @BindView(R.id.img_top_left)
    ImageView imgTopLeft;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    @BindString(R.string.title_notifications)
    String title;

    String[] titles = {"聊天消息","系统消息"};
    int[] resIds = {R.drawable.img_im_message,R.drawable.img_system_message};
    List<Fragment> fragmentList = new ArrayList<>(2);
    FragmentTabIconAdapter pageAdapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_message;
    }
    @Override
    public void createInit() {
        super.createInit();
        fragmentList.add(MessageChildFragment.newInstance(0));
        fragmentList.add(MessageChildFragment.newInstance(1));
    }
    @Override
    public void initEvent() {
        super.initEvent();
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitle.setText(title);

        pageAdapter = new FragmentTabIconAdapter(getChildFragmentManager(),titles,resIds,fragmentList);
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
    }
}
