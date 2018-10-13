package com.sj.rentinghouse.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.roundedimageview.RoundedImageView;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.HtmlActivity;
import com.sj.rentinghouse.activity.LoginActivity;
import com.sj.rentinghouse.activity.SettingActivity;
import com.sj.rentinghouse.activity.UserInfoActivity;
import com.sj.rentinghouse.adapter.MyRentRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.MyItem;
import com.sj.rentinghouse.bean.UserInfo;
import com.sj.rentinghouse.events.LoginEvent;
import com.sj.rentinghouse.events.LoginOutEvent;
import com.sj.rentinghouse.events.MyPageSwitchEvent;
import com.sj.rentinghouse.events.UserInfoEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/8.
 * 我的（租客页）
 */

public class MyRenterFragment extends AppBaseFragment {

    @BindView(R.id.layout_title)
    View view;
    @BindView(R.id.img_top_left)
    ImageView imgTopLeft;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;
    @BindView(R.id.img_top_right)
    TextView tvTopTitleRight;

    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    MyRentRyvAdapter mAdapter;
    @BindView(R.id.img_user_icon)
    RoundedImageView imgUserIcon;
    @BindView(R.id.tv_nike_name)
    TextView tvNikeName;
    @BindView(R.id.tv_account)
    TextView tvAccount;

    UserInfo userInfo;

    public static MyRenterFragment newInstance() {
        MyRenterFragment f = new MyRenterFragment();
        return f;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_my_renter;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        view.setBackgroundResource(R.color.transparent);
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitleRight.setVisibility(View.VISIBLE);
        tvTopTitleRight.setText("切换为房东");
        Drawable drawable = getResources().getDrawable(R.drawable.img_top_title_switch);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        tvTopTitleRight.setCompoundDrawables(drawable, null, null, null);
        tvTopTitle.setText("个人中心");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray_AD), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new MyRentRyvAdapter(getHoldingActivity());
        rylView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.putExtra("title", "客服");
                        intent.putExtra("url","https://www.sobot.com/chat/h5/index.html?sysNum=61818b7110bd4498b7c87f26db6ec760&source=2");
                        intent.setClass(MyRenterFragment.this.getContext(), HtmlActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("title", "客服");
                        intent.putExtra("url","https://www.sobot.com/chat/h5/index.html?sysNum=61818b7110bd4498b7c87f26db6ec760&source=2");
                        intent.setClass(MyRenterFragment.this.getContext(), HtmlActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        DialogUtils.showCallDialog(getHoldingActivity(), null, mAdapter.getItem(position).getItemRemark());
                        break;
                    case 3:
                        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
                            DialogUtils.showLoginDialog(getHoldingActivity(), LoginActivity.class);
                        } else {
                            intent.setClass(MyRenterFragment.this.getContext(), SettingActivity.class);
                            startActivity(intent);
                        }
                        break;
                }

            }
        });
        rylView.setLayoutManager(layoutManager);
        initData();
        loginEvent(new LoginEvent((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)));
        EventBus.getDefault().register(this);
    }

    private void getUserInfo() {
        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)){
            return;
        }
        API.queryUserInfo(new ServerResultBack<BaseResponse<UserInfo>, UserInfo>() {
            @Override
            public void onSuccess(UserInfo data) {
                EventBus.getDefault().post(new UserInfoEvent(data));
            }
        });
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

    private void initData() {
        mAdapter.add(new MyItem(R.drawable.img_my_custom, "客户咨询"));
        mAdapter.add(new MyItem(R.drawable.img_my_suggest, "投诉建议"));
        mAdapter.add(new MyItem(R.drawable.img_my_contact_us, "联系我们", "0551-69131867"));
        mAdapter.add(new MyItem(R.drawable.img_my_suggest, "账号设置"));
    }

    @OnClick({R.id.img_top_right, R.id.constraintLayout})
    public void onClickView(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.img_top_right:
                if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
                    DialogUtils.showLoginDialog(getHoldingActivity(), LoginActivity.class);
                } else {
                    EventBus.getDefault().post(new MyPageSwitchEvent(1));
                }
                break;
            case R.id.constraintLayout:
                if ((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
                    Intent intent = new Intent(view.getContext(), UserInfoActivity.class);
                    intent.putExtra("data", userInfo);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(view.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginEvent(LoginEvent event) {
        getUserInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginOutEvent(LoginOutEvent event) {
        if (event.isSuccess()){
            ImageUtils.loadImageWithError(null, R.drawable.img_user_icon, imgUserIcon);
            tvNikeName.setText("登录/注册");
            tvAccount.setText(null);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (userInfo==null){
            getUserInfo();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userInfoEvent(UserInfoEvent event) {
        initUserData(event.getUserInfo());
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
