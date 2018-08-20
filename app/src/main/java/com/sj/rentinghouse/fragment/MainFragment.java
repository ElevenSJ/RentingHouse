package com.sj.rentinghouse.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.events.LocationEvent;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.task.SerializeInfoSaveTask;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.HotWordActivity;
import com.sj.rentinghouse.activity.HouseDetailActivity;
import com.sj.rentinghouse.activity.HouseListActivity;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.BannerInfo;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.bean.OrderInfo;
import com.sj.rentinghouse.events.MainRefreshEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.GlideImageLoader;
import com.sj.rentinghouse.utils.NameSpace;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MainFragment extends AppBaseFragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {

    @BindView(R.id.layout_title)
    View layoutTitle;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_whole_rent)
    TextView tvWholeRent;
    @BindView(R.id.tv_joint_rent)
    TextView tvJointRent;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;

    HouseRyvAdapter mAdapter;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    LocatedCity locatedCity;
    String cityName = "";
    String cityCode = "";
    @BindView(R.id.img_top_left)
    TextView imgTopLeft;
    @BindView(R.id.tv_top_title)
    EditText tvTopTitle;
    Map<String, Object> map = new ArrayMap<>();

    List<BannerInfo> bannerInfoList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.fragment_main;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (banner != null && banner.getChildCount() != 0) {
                //开始轮播
                banner.startAutoPlay();
            }
        } else {
            if (banner != null && banner.getChildCount() != 0) {
                //结束轮播
                banner.stopAutoPlay();
            }
        }
        if (isVisibleToUser && rylView != null && mAdapter != null && mAdapter.getCount() == 0) {
            onRefresh();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void bindView() {
        super.bindView();
        layoutTitle.setBackgroundResource(R.color.transparent);
    }

    @Override
    public void init() {
        super.init();
        cityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, "");
        cityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, "");
        map.put("city", cityCode);
        imgTopLeft.setText(cityName);
    }

    @Override
    public void initEvent() {
        tvTopTitle.setFocusable(false);
        tvTopTitle.setFocusableInTouchMode(false);
        Drawable drawable = getResources().getDrawable(R.drawable.img_location);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        imgTopLeft.setCompoundDrawables(drawable, null, null, null);

        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (position<bannerInfoList.size()){
                    if (!TextUtils.isEmpty(bannerInfoList.get(position).getHouseId())){
                        Intent intent = new Intent();
                        intent.putExtra("id", bannerInfoList.get(position).getHouseId());
                        intent.setClass(getHoldingActivity(), HouseDetailActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        mAdapter = new HouseRyvAdapter(getHoldingActivity());
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        if (!TextUtils.isEmpty(cityCode)) {
            onRefresh();
            getBannerList();
            rylView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().register(MainFragment.this);
                }
            },1500);
        }else{
            EventBus.getDefault().register(this);
        }
    }

    private void getBannerList() {
        API.headPicture(new ServerResultBack<BaseResponse<DataList<BannerInfo>>, DataList<BannerInfo>>() {
            @Override
            public void onSuccess(DataList<BannerInfo> data) {
                bannerInfoList.clear();
                if (data.getData() == null || data.getData().isEmpty()) {
                    BannerInfo bannerInfo = new BannerInfo();
                    bannerInfo.setPictureUrl(null);
                    bannerInfoList.add(bannerInfo);
                    banner.setImages(bannerInfoList);
                } else {
                    bannerInfoList.addAll(data.getData());
                    banner.setImages(bannerInfoList);
                }
                banner.start();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(MainRefreshEvent event) {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        nextFirstIndex = "-1";
        getData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receiveLocation(LocationEvent locationEvent) {
        if (locationEvent.getLocation() != null) {
            final String localCityName = locationEvent.getLocation().getCity();
            final String localCityCode = locationEvent.getLocation().getCityCode();
            SPUtils.getInstance().apply(new String[]{NameSpace.LOCAL_CITY_NAME, NameSpace.LOCAL_CITY_CODE}, new Object[]{localCityName, localCityCode});
            Logger.d("定位城市：" + localCityName + "城市编码：" + localCityCode);
            Logger.d("城市编码开始转换");
            boolean isChanged = false;
            if (App.allCities != null) {
                for (City city : App.allCities) {
                    if (localCityName.contains(city.getName())) {
                        Logger.d("检索到对应城市");
                        Logger.d("城市编码开始转换:" + cityCode + ">>>" + city.getCode());
                        this.cityCode = city.getCode();
                        this.cityName = city.getName();
                        SPUtils.getInstance().apply(new String[]{NameSpace.CITY_NAME, NameSpace.CITY_CODE, NameSpace.LOCAL_CITY_NAME, NameSpace.LOCAL_CITY_CODE}, new Object[]{this.cityName, this.cityCode, this.cityName, this.cityCode});
                        isChanged = true;
                        break;
                    }
                }
            }
            Logger.d("城市编码转换完毕");
            if (isChanged) {
                Logger.d("当前城市已开通，城市编码已转换");
                imgTopLeft.setText(this.cityName);
                locatedCity = new LocatedCity(this.cityName, this.cityName, this.cityCode);
                if (!map.get("city").equals(this.cityCode)) {
                    map.put("city", this.cityCode);
                    onRefresh();
                    getBannerList();
                }
                Logger.d("转化后定位城市：" + JSON.toJSONString(locatedCity));
            } else {
                SPUtils.getInstance().apply(new String[]{NameSpace.CITY_NAME, NameSpace.CITY_CODE}, new Object[]{this.cityName, this.cityCode});
                locatedCity = new LocatedCity(localCityName, localCityName, localCityCode);
                DialogUtils.showMessageDialog(this.getContext(), "当前城市（" + localCityName + "）还未开通服务，是否切换城市", "切换", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == -100) {
                            imgTopLeft.setText(localCityName);
                            map.put("city", localCityCode);
                            onRefresh();
                            getBannerList();
                        } else {
                            dialogInterface.dismiss();
                            toOpenCityList();
                        }
                    }
                });
                Logger.d("未转化后定位城市：" + JSON.toJSONString(locatedCity));
            }
        }
    }

    private void getData() {
        Logger.d("城市地址：" + cityName + "城市编码：" + cityCode);
        API.allHouseList(map, nextFirstIndex, new ServerResultBack<BaseResponse<DataList<HouseInfo>>, DataList<HouseInfo>>() {
            @Override
            public void onSuccess(DataList<HouseInfo> data) {
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

    @OnClick({R.id.tv_joint_rent, R.id.tv_whole_rent, R.id.tv_top_title, R.id.img_top_left})
    public void onClickView(View view) {
        int id = view.getId();
        Intent intent = new Intent();
        switch (id) {
            case R.id.tv_whole_rent:
                intent.setClass(getHoldingActivity(), HouseListActivity.class);
                intent.putExtra(HouseListActivity.HOUSE_TYPE, "1");
                startActivity(intent);
                break;
            case R.id.tv_joint_rent:
                intent.setClass(getHoldingActivity(), HouseListActivity.class);
                intent.putExtra(HouseListActivity.HOUSE_TYPE, "2");
                startActivity(intent);
                break;
            case R.id.tv_top_title:
                intent.setClass(getHoldingActivity(), HotWordActivity.class);
//                intent.setClass(getHoldingActivity(), HouseListActivity.class);
//                intent.putExtra(HouseListActivity.HOUSE_SEARCH, true);
                startActivity(intent);
                break;
            case R.id.img_top_left:
                toOpenCityList();
                break;
        }

    }

    private void toOpenCityList() {
        CityPicker.getInstance()
                .setFragmentManager(getChildFragmentManager())
                .enableAnimation(true)
                .setAnimationStyle(R.style.DefaultCityPickerAnimation).setLocatedCity(locatedCity).setAllCities(App.allCities).setOnPickListener(new OnPickListener() {
            @Override
            public void onPick(int position, City data) {
                if (data != null) {
                    cityName = data.getName();
                    cityCode = data.getCode();
                    imgTopLeft.setText(cityName);
                    map.put("city", cityCode);
                    SPUtils.getInstance().commit(new String[]{NameSpace.CITY_NAME, NameSpace.CITY_CODE}, new Object[]{cityName, cityCode});
                    onRefresh();
                    getBannerList();
                }
            }

            @Override
            public void onLocate() {
            }
        }).jsonShow();
    }

}