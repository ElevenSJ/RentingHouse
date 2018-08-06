package com.sj.rentinghouse.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.adapter.FragmentAdapter;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.widgets.PagerSlidingTabStrip;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.AddOrUpdateHouseActivity;
import com.sj.rentinghouse.activity.OrderManagerActivity;
import com.sj.rentinghouse.activity.UserInfoActivity;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.UserInfo;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.events.LoginEvent;
import com.sj.rentinghouse.events.MainPageSwitchEvent;
import com.sj.rentinghouse.events.MyPageSwitchEvent;
import com.sj.rentinghouse.events.MyRefreshEvent;
import com.sj.rentinghouse.events.UserInfoEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.MessageEvent;

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

    String[] titles = {"全部房产", "已租房产", "待租房产", "即将到期房产"};
    List<Fragment> fragmentList = new ArrayList<>(4);
    FragmentAdapter pageAdapter;

    UserInfo userInfo;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser&&pageAdapter!=null&&(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)){
            getMyHouseCount();
            getOrderCount();
            getMyMessageCount();
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();
        view.setBackgroundResource(R.color.transparent);
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitleRight.setVisibility(View.VISIBLE);
        tvTopTitleRight.setText("切换为房客");
        Drawable drawable = getResources().getDrawable(R.drawable.img_top_title_switch);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        tvTopTitleRight.setCompoundDrawables(drawable, null, null, null);
        tvTopTitle.setText("个人中心");

        pageAdapter = new FragmentAdapter(getChildFragmentManager(), titles, fragmentList);
        pager.setOffscreenPageLimit(3);
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
        loginEvent(new LoginEvent((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)));
        EventBus.getDefault().register(this);

    }
    private void getMyMessageCount(){
        tvMyMessageCount.setSelected(JMessageClient.getAllUnReadMsgCount()>0);
        tvMyMessageCount.setText(JMessageClient.getAllUnReadMsgCount()<0?"0":JMessageClient.getAllUnReadMsgCount()+"");
    }
    private void getMyHouseCount() {
        API.getMyHouseCount(new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    tvMyHouseCount.setText(Double.valueOf(data).intValue() + "");
//                    tvMyHouseCount.setSelected(Double.valueOf(data).intValue()>0);
                } catch (NumberFormatException e) {
                    tvMyHouseCount.setText(data);
                    Logger.e("我的房产总数转型异常");
                }
            }
        });
    }
    private void getOrderCount() {
        API.getOrderCount(new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                try {
                    tvMyOrderCount.setText(Double.valueOf(data).intValue() + "");
                    tvMyOrderCount.setSelected(Double.valueOf(data).intValue()>0);
                } catch (NumberFormatException e) {
                    tvMyHouseCount.setText(data);
                    Logger.e("我的约看总数转型异常");
                }
            }
        });
    }

    @OnClick({R.id.img_top_right, R.id.constraintLayout, R.id.tv_house_manager, R.id.tv_order_manager, R.id.tv_message_manager})
    public void onClickView(View view) {
        int id = view.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.img_top_right:
                EventBus.getDefault().post(new MyPageSwitchEvent(0));
                break;
            case R.id.constraintLayout:
                intent.setClass(view.getContext(), UserInfoActivity.class);
                intent.putExtra("data", userInfo);
                startActivity(intent);
                break;
            case R.id.tv_house_manager:
                intent.setClass(view.getContext(), AddOrUpdateHouseActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_order_manager:
                intent.setClass(view.getContext(), OrderManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_message_manager:
                EventBus.getDefault().post(new MainPageSwitchEvent(2));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userInfoEvent(UserInfoEvent event) {
        initUserData(event.getUserInfo());
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginEvent(LoginEvent event) {
        if (event.isSuccess()){
            getMyHouseCount();
            getOrderCount();
            getMyMessageCount();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshData(MyRefreshEvent event) {
        Logger.e("房东页面，刷新event");
        getMyHouseCount();
        getOrderCount();
        getMyMessageCount();
    }
    private void initUserData(UserInfo data) {
        if (null == data) {
            return;
        }
        userInfo = data;
        ImageUtils.loadImageWithError(data.getImgUrl(), R.drawable.img_user_icon, imgUserIcon);
        tvNikeName.setText(TextUtils.isEmpty(data.getNickname()) ? "昵称" : data.getNickname());
        tvAccount.setText(data.getUserId());
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
