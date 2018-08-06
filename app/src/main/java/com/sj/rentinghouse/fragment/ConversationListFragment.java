package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.orhanobut.logger.Logger;
import com.sj.im.SortConvList;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.ConversationRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.events.IMLoginEvent;
import com.sj.rentinghouse.events.MessageRefreshEvent;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;

/**
 * Created by Sunj on 2018/7/8.
 */

public class ConversationListFragment extends AppBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    int index = 0;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    ConversationRyvAdapter mAdapter;

    private List<Conversation> mDatas;

    public static ConversationListFragment newInstance(int index) {
        final ConversationListFragment f = new ConversationListFragment();
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
        mAdapter = new ConversationRyvAdapter(getHoldingActivity());
        rylView.setAdapter(mAdapter);
        rylView.showEmpty();
        imLoginEvent(new IMLoginEvent((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)));
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void imLoginEvent(IMLoginEvent event) {
        if (event.isSuccess()) {
            getData();
        }
    }

    /**
     * 接收消息类事件
     *
     * @param event 消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageRefreshEvent event) {
        Logger.d("消息列表接受到消息");
        EventManger.getDefault().postMyRefreshEvent();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
            rylView.setRefreshing(false);
            return;
        }
        getData();
        rylView.setRefreshing(false);
    }

    private void getData() {
        if (mDatas != null && !mDatas.isEmpty()) {
            mDatas.clear();
        }
        if (mAdapter.getCount() > 0) {
            mAdapter.clear();
        }
        mDatas = JMessageClient.getConversationList();
        if (mDatas != null && mDatas.size() > 0) {
            SortConvList sortConvList = new SortConvList();
            Collections.sort(mDatas, sortConvList);
        }
        mAdapter.addAll(mDatas);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
