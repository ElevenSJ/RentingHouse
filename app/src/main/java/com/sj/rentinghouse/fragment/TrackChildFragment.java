package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.CallInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.MyHouseInfo;
import com.sj.rentinghouse.bean.MyLetHouseInfo;
import com.sj.rentinghouse.bean.OrderInfo;
import com.sj.rentinghouse.events.LoginEvent;
import com.sj.rentinghouse.events.MyRefreshEvent;
import com.sj.rentinghouse.events.TrackRefreshEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.NameSpace;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class TrackChildFragment extends AppBaseFragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {

    int index = 0;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    HouseRyvAdapter mAdapter;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    public static TrackChildFragment newInstance(int index) {
        final TrackChildFragment f = new TrackChildFragment();
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
        return R.layout.fragment_track_child;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        mAdapter = new HouseRyvAdapter(getHoldingActivity());
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        rylView.showEmpty();
        loginEvent(new LoginEvent((Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loginEvent(LoginEvent event) {
        onRefresh();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshData(MyRefreshEvent event) {
        if (index>=3) {
            onRefresh();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshData(TrackRefreshEvent event) {
        if (index<3) {
            onRefresh();
        }
    }
    @Override
    public void onRefresh() {
        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)){
            rylView.setRefreshing(false);
            return;
        }
        pageNum = 1;
        nextFirstIndex = "-1";
        getData();
    }
    private void getData() {
        Logger.d("TrackChildFragment index=" + index);
        String firstIndex = mAdapter.getCount() == 0 || pageNum == 1 ? "" : mAdapter.getItem(mAdapter.getCount() - 1).getId();
        switch (index) {
            //我的约看
            case 0:
                API.orderList(firstIndex, new ServerResultBack<BaseResponse<DataList<OrderInfo>>, DataList<OrderInfo>>() {
                    @Override
                    public void onSuccess(DataList<OrderInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //我的约看完成
            case 1:
                API.closeOrderList(firstIndex, new ServerResultBack<BaseResponse<DataList<OrderInfo>>, DataList<OrderInfo>>() {
                    @Override
                    public void onSuccess(DataList<OrderInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //拨号记录
            case 2:
                API.dialingRecordList(firstIndex, new ServerResultBack<BaseResponse<DataList<CallInfo>>, DataList<CallInfo>>() {
                    @Override
                    public void onSuccess(DataList<CallInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //全部房产
            case 3:
                API.houseList(firstIndex, new ServerResultBack<BaseResponse<DataList<MyHouseInfo>>, DataList<MyHouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<MyHouseInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //已租房产
            case 4:
                API.houseListLet(firstIndex, new ServerResultBack<BaseResponse<DataList<MyHouseInfo>>, DataList<MyHouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<MyHouseInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //待租房产
            case 5:
                API.houseListLetNo(firstIndex, new ServerResultBack<BaseResponse<DataList<MyHouseInfo>>, DataList<MyHouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<MyHouseInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
            //即将到期房产
            case 6:
                API.houseListLetExpired(firstIndex, new ServerResultBack<BaseResponse<DataList<MyLetHouseInfo>>, DataList<MyLetHouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<MyLetHouseInfo> data) {
                        if (getHoldingActivity().isDestory()){
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
                        if (rylView==null||getHoldingActivity().isDestory()){
                            return;
                        }
                        rylView.setRefreshing(false);
                    }
                });
                break;
        }


    }

    @Override
    public void onMoreShow() {
        if (TextUtils.isEmpty(nextFirstIndex)){
            mAdapter.add(null);
            return;
        }
        getData();
    }

    @Override
    public void onMoreClick() {

    }

}
