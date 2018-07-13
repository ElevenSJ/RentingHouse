package com.sj.rentinghouse.fragment;

import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.roundedimageview.RoundedImageView;
import com.sj.module_lib.adapter.FragmentAdapter;
import com.sj.module_lib.widgets.PagerSlidingTabStrip;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.events.MyPageSwitchEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Sunj on 2018/7/8.
 * 我的（房东页）
 */
public class MyLandlordFragment extends AppBaseFragment {

    @BindView(R.id.layout_title)
    View view;
    @BindView(R.id.img_top_left)
    ImageView imgTopLeft;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.img_top_right)
    TextView tvTopTitleRight;
    @BindView(R.id.img_user_icon)
    RoundedImageView imgUserIcon;
    @BindView(R.id.tv_nike_name)
    TextView tvNikeName;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.tv_message_manager)
    TextView tvMessageManager;
    @BindView(R.id.tv_order_manager)
    TextView tvOrderManager;
    @BindView(R.id.tv_my_order_count)
    TextView tvMyOrderCount;
    @BindView(R.id.tv_my_house_count)
    TextView tvMyHouseCount;
    @BindView(R.id.tv_my_message_count)
    TextView tvMyMessageCount;
    @BindView(R.id.tv_house_manager)
    TextView tvHouseManager;
    @BindView(R.id.layout_manager)
    ConstraintLayout layoutManager;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;

    String[] titles = {"全部房产","已租房产","待租房产","即将到期房产"};
    List<Fragment> fragmentList = new ArrayList<>(4);
    FragmentAdapter pageAdapter;

    public static MyLandlordFragment newInstance() {
        MyLandlordFragment f = new MyLandlordFragment();
        return f;
    }

    @Override
    public void createInit() {
        super.createInit();
        fragmentList.add(TrackChildFragment.newInstance(3));
        fragmentList.add(TrackChildFragment.newInstance(4));
        fragmentList.add(TrackChildFragment.newInstance(5));
        fragmentList.add(TrackChildFragment.newInstance(6));
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_my_landlord;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        view.setBackgroundResource(R.color.transparent);
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitleRight.setVisibility(View.VISIBLE);
        tvTopTitleRight.setText("切换为房客");
        tvTopTitle.setText("个人中心");
        Drawable drawable = getResources().getDrawable(R.drawable.img_top_title_switch);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        tvTopTitleRight.setCompoundDrawables(drawable, null, null, null);

        pageAdapter = new FragmentAdapter(getChildFragmentManager(),titles,fragmentList);
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

    @OnClick(R.id.img_top_right)
    public void onClickView() {
        EventBus.getDefault().post(new MyPageSwitchEvent(0));
    }

}
