package com.sj.rentinghouse.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.MyOrderInfo;
import com.sj.rentinghouse.events.OrderStatusEvent;
import com.sj.rentinghouse.http.API;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/21.
 */

public class OrderManagerActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;

    HouseRyvAdapter mAdapter;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    @Override
    public int getContentView() {
        return R.layout.activity_order_manager;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "约看管理");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        mAdapter = new HouseRyvAdapter(this);
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        getOrderList();
        EventBus.getDefault().register(this);
    }

    private void getOrderList() {
        API.ownerList(nextFirstIndex, new ServerResultBack<BaseResponse<DataList<MyOrderInfo>>, DataList<MyOrderInfo>>() {
            @Override
            public void onSuccess(DataList<MyOrderInfo> data) {
                if (isDestory()){
                    return;
                }
                nextFirstIndex = data.getNextFirstIndex();
                if (pageNum == 1 && mAdapter.getCount() > 0) {
                    mAdapter.clear();
                }
                mAdapter.addAll(data.getData());
                pageNum++;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (rylView==null||isDestory()){
                    return;
                }
                rylView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        nextFirstIndex = "-1";
        getOrderList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshStatus(OrderStatusEvent event) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mAdapter.getItem(i).getId().equals(event.getId())) {
                if (mAdapter.getItem(i) instanceof MyOrderInfo) {
                    ((MyOrderInfo) mAdapter.getItem(i)).setStatus(event.getStatus());
                    mAdapter.update(mAdapter.getItem(i), i);
                    break;
                }
            }
        }
    }

    @Override
    public void onMoreShow() {
        if (TextUtils.isEmpty(nextFirstIndex)){
            mAdapter.add(null);
            return;
        }
        getOrderList();
    }

    @Override
    public void onMoreClick() {

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
