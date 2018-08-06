package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.adapter.NoticeRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.bean.NoticeInfo;
import com.sj.rentinghouse.events.LoginEvent;
import com.sj.rentinghouse.events.MessageRefreshEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MessageChildFragment extends AppBaseFragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {

    int index = 0;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    NoticeRyvAdapter mAdapter;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    public static MessageChildFragment newInstance(int index) {
        final MessageChildFragment f = new MessageChildFragment();
        final Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public void createInit() {
        super.createInit();
        index = getArguments().getInt("index");
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_message_child;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray_AD), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new NoticeRyvAdapter(getHoldingActivity());
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        rylView.showEmpty();
        loginEvent(new LoginEvent((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)));
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginEvent(LoginEvent event) {
        if ((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
            nextFirstIndex = "";
            getData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(MessageRefreshEvent event) {
        nextFirstIndex = "";
        pageNum = 1;
        getData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && rylView != null && mAdapter != null && mAdapter.getCount() == 0) {
            if ((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
                pageNum = 1;
                nextFirstIndex = "-1";
                onRefresh();
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onRefresh() {
        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
            rylView.setRefreshing(false);
            return;
        }
        pageNum = 1;
        nextFirstIndex = "-1";
        getData();
    }

    private void getData() {
        Logger.d("MessageChildFragment index=" + index);
        API.noticeList(nextFirstIndex, new ServerResultBack<BaseResponse<DataList<NoticeInfo>>, DataList<NoticeInfo>>() {
            @Override
            public void onSuccess(DataList<NoticeInfo> data) {
                if (getHoldingActivity().isDestory()){
                    return;
                }
                if (data.getNextFirstIndex()==null||!data.getNextFirstIndex().equals(nextFirstIndex)){
                    nextFirstIndex = data.getNextFirstIndex();
                }else{
                    nextFirstIndex = "";
                }
                if (pageNum == 1 && mAdapter.getCount() > 0) {
                    mAdapter.clear();
                }
                mAdapter.addAll(data.getData());
                pageNum++;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (rylView==null||getHoldingActivity().isDestory()){
                    return;
                }
                rylView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onMoreShow() {
        if (TextUtils.isEmpty(nextFirstIndex)) {
            mAdapter.add(null);
            return;
        }
        getData();
    }

    @Override
    public void onMoreClick() {

    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
