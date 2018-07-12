package com.sj.rentinghouse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.http.API;

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
    final int pageCount = 10;

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
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()){
            onRefresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser&&rylView!=null&&mAdapter!=null&&mAdapter.getCount()==0){
            onRefresh();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        getData();
    }

    private void getData() {
        Logger.d("TrackChildFragment index="+index);
        String firstIndex = mAdapter.getCount()==0||pageNum ==1?"":mAdapter.getItem(mAdapter.getCount()-1).getId();
        switch (index){
            case 0:
                API.orderList(firstIndex, pageCount, new ServerResultBack<BaseResponse<DataList<HouseInfo>>,DataList<HouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<HouseInfo> data) {
                        if (data != null && data.getData() != null) {
                            if (pageNum == 1 && mAdapter.getCount() > 0) {
                                mAdapter.clear();
                            }
                            mAdapter.addAll(data.getData());
                            pageNum++;
                        }
                    }
                });
                break;
            case 1:
                API.closeOrderList(firstIndex, pageCount, new ServerResultBack<BaseResponse<DataList<HouseInfo>>,DataList<HouseInfo>>() {
                    @Override
                    public void onSuccess(DataList<HouseInfo> data) {
                        if (data != null && data.getData() != null) {
                            if (pageNum == 1 && mAdapter.getCount() > 0) {
                                mAdapter.clear();
                            }
                            mAdapter.addAll(data.getData());
                            pageNum++;
                        }
                    }
                });
                break;
            case 2:

                break;
        }


    }

    @Override
    public void onMoreShow() {
        getData();
    }

    @Override
    public void onMoreClick() {

    }
}
