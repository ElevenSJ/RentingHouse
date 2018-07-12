package com.sj.rentinghouse.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.http.API;
import com.youth.banner.Banner;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MainFragment extends AppBaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_whole_rent)
    TextView tvWholeRent;
    @BindView(R.id.tv_joint_rent)
    TextView tvJointRent;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;

    HouseRyvAdapter mAdapter;

    final int pageNum = 10;
    @Override
    public int getContentView() {
        return R.layout.fragment_main;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        mAdapter = new HouseRyvAdapter(getHoldingActivity());
        rylView.setAdapter(mAdapter);
        rylView.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        if (mAdapter!=null&&mAdapter.getCount()!=0){
            mAdapter.clear();
        }
        getData();
    }

    private void getData() {
        String firstIndex = mAdapter.getCount()==0||pageNum == 1 ?"":mAdapter.getItem(mAdapter.getCount()-1).getId();
        API.allHouseList(firstIndex,pageNum,new ServerResultBack<BaseResponse<DataList<HouseInfo>>,DataList<HouseInfo>>() {
            @Override
            public void onSuccess(DataList<HouseInfo> data) {
                if (data != null && data.getData() != null) {
                    if (pageNum == 1 && mAdapter.getCount() > 0) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(data.getData());
                }
            }
        } );
    }
}
