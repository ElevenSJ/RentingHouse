package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.widgets.CommonPopupWindow;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.CityPopWindow;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/15.
 */

public class HouseListActivity extends AppBaseActivity implements SwipeRefreshLayout.OnRefreshListener, RecyclerArrayAdapter.OnMoreListener {

    public static final String HOUSE_TYPE = "house_type";
    public static final String HOUSE_SEARCH = "house_search";

    @BindView(R.id.img_top_left)
    TextView imgTopLeft;
    @BindView(R.id.tv_top_title)
    EditText tvTopTitle;
    @BindView(R.id.img_top_right)
    TextView imgTopRight;


    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;

    HouseRyvAdapter mAdapter;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    String type = "";
    boolean isSearch = false;
    String cityCode = "";
    String cityName = "";
    String districtCode = "";
    String districtName = "";

    Map<String, Object> map = new ArrayMap<>();
    @BindView(R.id.tv_item_train)
    TextView tvItemTrain;
    @BindView(R.id.tv_item_area)
    TextView tvItemArea;
    @BindView(R.id.tv_item_filter)
    TextView tvItemFilter;

    Map<String, Object> selectedFilterMap = new HashMap<>();

    CommonPopupWindow popupWindow;
    final int[] rd_orders = {R.id.tv_downRent, R.id.tv_upRent, R.id.tv_downArea, R.id.tv_upArea};
    final int[] rd_directions = {R.id.rd_direction_1, R.id.rd_direction_2, R.id.rd_direction_3, R.id.rd_direction_4, R.id.rd_direction_5};
    final int[] rd_rooms = {R.id.rd_room_1, R.id.rd_room_2, R.id.rd_room_3, R.id.rd_room_4};

    @Override
    public int getContentView() {
        return R.layout.activity_house_list;
    }

    @Override
    public void init() {
        super.init();
        type = getIntent().getStringExtra(HOUSE_TYPE);
        isSearch = getIntent().getBooleanExtra(HOUSE_SEARCH, false);
        cityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, "");
        cityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, "");
        if (!TextUtils.isEmpty(type)) {
            map.put("type", type);
        }
        map.put("city", cityCode);
    }

    private void toSearchActivty() {
        Intent intent = new Intent(this, HotWordActivity.class);
//        startActivityForResult(intent, 100);
        startActivity(intent);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        tvTopTitle.setFocusable(false);
        tvTopTitle.setFocusableInTouchMode(false);
        tvTopTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null) {
                    //点击搜索要做的操作
                    ToastUtils.showShortToast("开始搜索");
                }
                return false;
            }
        });

        tvItemArea.setText("区域" + "( " + cityName + districtName + " )");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.setRefreshListener(this);
        mAdapter = new HouseRyvAdapter(this);
        mAdapter.setMore(R.layout.layout_load_more, this);
        mAdapter.setNoMore(R.layout.layout_load_no_more);
        rylView.setAdapter(mAdapter);
        rylView.showEmpty();
        onRefresh();
        if (isSearch) {
            toSearchActivty();
        }
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        nextFirstIndex = "-1";
        getData(map);
    }

    @Override
    public void onMoreShow() {
        if (TextUtils.isEmpty(nextFirstIndex)) {
            mAdapter.add(null);
            return;
        }
        getData(map);
    }

    @Override
    public void onMoreClick() {

    }

    private void getData(Map<String, Object> map) {
        tvItemTrain.setSelected(false);
        tvItemArea.setSelected(false);
        tvItemFilter.setSelected(false);
        API.allHouseList(map, nextFirstIndex, new ServerResultBack<BaseResponse<DataList<HouseInfo>>, DataList<HouseInfo>>() {
            @Override
            public void onSuccess(DataList<HouseInfo> data) {
                if (isDestory()) {
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
                if (rylView != null && !isDestory()) {
                    rylView.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && requestCode == 100) {
            if (data != null) {
                String dataStr = data.getStringExtra("data");
                if (!TextUtils.isEmpty(dataStr)) {
                    tvTopTitle.setText(dataStr);
                    imgTopRight.setVisibility(View.VISIBLE);
                    map.put("village", dataStr);
                } else {
                    imgTopRight.setVisibility(View.INVISIBLE);
                    tvTopTitle.setText(null);
                    map.remove("village");
                }
                pageNum = 1;
                nextFirstIndex = "-1";
                getData(map);
            }
        }
    }

    @OnClick({R.id.layout_train, R.id.layout_area, R.id.layout_filter, R.id.img_top_right, R.id.tv_top_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_top_right:
                tvTopTitle.setText(null);
                imgTopRight.setVisibility(View.INVISIBLE);
                map.remove("village");
                pageNum = 1;
                nextFirstIndex = "-1";
                getData(map);
                break;
            case R.id.tv_top_title:
                toSearchActivty();
                break;
            case R.id.layout_train:
                break;
            case R.id.layout_area:
                tvItemTrain.setSelected(false);
                tvItemFilter.setSelected(false);
                tvItemArea.setSelected(true);
//                DialogUtils.showChooseDialog(HouseListActivity.this, App.allCityMap.get(cityCode), new DialogUtils.OnMapSelectedListener() {
//                    @Override
//                    public void callBack(String key, String value) {
//                        Logger.i("选择区县：" + value + "," + key);
//                        if (TextUtils.isEmpty(value)) {
//                            tvItemArea.setText("区域");
//                        } else {
//                            tvItemArea.setText("区域 (" + value + " )");
//                        }
//                        map.put("districtCode", key);
//                        pageNum = 1;
//                        getData(map);
//                    }
//                });
                CityPopWindow.getDefault(this).init().initCityData(cityName).initDistrictData(districtName).setOnAdapterItemClickListener(new CityPopWindow.OnAdapterItemClickListener() {
                    @Override
                    public void onItemSelected(CityInfo cityInfo, CityInfo cityInfo1) {
                        if (cityInfo != null && cityInfo1 != null) {
                            cityName = cityInfo.getName();
                            cityCode = cityInfo.getCode();
                            districtName = cityInfo1.getName();
                            districtCode = cityInfo1.getCode();
                            map.put("city", cityCode);
                            map.put("districtCode", districtCode);
                        } else {
                            cityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, "");
                            cityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, "");
                            districtName = "";
                            districtCode = "";
                            map.put("city", cityCode);
                            map.put("districtCode", "");
                        }
                        tvItemArea.setText("区域" + "( " + cityName + districtName + " )");
                        onRefresh();
                    }
                }).setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        tvItemTrain.setSelected(false);
                        tvItemArea.setSelected(false);
                        tvItemFilter.setSelected(false);
                    }
                }).show(view);
                break;
            case R.id.layout_filter:
                tvItemTrain.setSelected(false);
                tvItemArea.setSelected(false);
                tvItemFilter.setSelected(true);
                if (popupWindow == null) {
                    popupWindow = new CommonPopupWindow.Builder(this)
                            //设置PopupWindow布局
                            .setView(R.layout.pop_house_filter)
                            //设置宽高
                            .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT)
                            //设置动画
                            .setAnimationStyle(R.style.DefaultCityPickerAnimation)
                            //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                            .setBackGroundLevel(1.0f)
                            //设置PopupWindow里的子View及点击事件
                            .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                                @Override
                                public void getChildView(View view, int layoutResId) {
                                    initPopView(view);
                                }
                            })
                            //设置外部是否可点击 默认是true
                            .setOutsideTouchable(true)
                            .create();
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            tvItemTrain.setSelected(false);
                            tvItemArea.setSelected(false);
                            tvItemFilter.setSelected(false);
                        }
                    });
                } else {
                    initPopData();
                }
                popupWindow.showAsDropDown(view);

                break;
        }
    }

    private void initPopData() {
        for (int id : rd_orders) {
            View view = popupWindow.getContentView().findViewById(id);
            if (selectedFilterMap.containsKey("order")) {
                if (selectedFilterMap.get("order").equals(view.getTag().toString())) {
                    view.setSelected(true);
                } else {
                    view.setSelected(false);
                }
            } else {
                view.setSelected(false);
            }
        }
        for (int id : rd_directions) {
            View view = popupWindow.getContentView().findViewById(id);
            if (selectedFilterMap.containsKey("direction")) {
                if (selectedFilterMap.get("direction").equals(view.getTag().toString())) {
                    view.setSelected(true);
                } else {
                    view.setSelected(false);
                }
            } else {
                view.setSelected(false);
            }
        }
        for (int id : rd_rooms) {
            View view = popupWindow.getContentView().findViewById(id);
            if (selectedFilterMap.containsKey("bedroom")) {
                if (selectedFilterMap.get("bedroom").equals(view.getTag().toString())) {
                    view.setSelected(true);
                } else {
                    view.setSelected(false);
                }
            } else {
                view.setSelected(false);
            }
        }
        RangeSeekBar rangeSeekBar = popupWindow.getContentView().findViewById(R.id.rangeSeekBar);
        if (selectedFilterMap.containsKey("startRent")) {
            float leftValue = TextUtils.isEmpty(selectedFilterMap.get("startRent").toString()) ? 0f : selectedFilterMap.get("startRent").equals("200000") ? 101f : Float.valueOf(selectedFilterMap.get("startRent").toString()) / 100f;
            float rightValue = TextUtils.isEmpty(selectedFilterMap.get("endRent").toString()) ? 0f : selectedFilterMap.get("endRent").equals("200000") ? 101f : Float.valueOf(selectedFilterMap.get("endRent").toString()) / 100f;
            rangeSeekBar.setValue(leftValue, rightValue);
        } else {
            try {
                rangeSeekBar.setValue(0f, 101f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initPopView(View view) {
        View.OnClickListener popupWindowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_clear:
                        popupWindow.dismiss();
                        for (Map.Entry<String, Object> entry : selectedFilterMap.entrySet()) {
                            map.remove(entry.getKey());
                        }
                        selectedFilterMap.clear();
                        onRefresh();
                        break;
                    case R.id.bt_sure:
                        popupWindow.dismiss();
                        map.putAll(selectedFilterMap);
//                        if(selectedFilterMap.containsKey("startRent")&& !TextUtils.isEmpty(selectedFilterMap.get("startRent").toString())&&!selectedFilterMap.get("endRent").toString().equals("0")){
//                            for (Map.Entry<String, Object> entry : selectedFilterMap.entrySet()) {
//                                if (!entry.getKey().equals("startRent")&&!entry.getKey().equals("endRent")) {
//                                    map.remove(entry.getKey());
//                                    selectedFilterMap.put(entry.getKey(),"");
//                                }
//                            }
//                        }
                        onRefresh();
                        break;
                    case R.id.tv_downRent:
                    case R.id.tv_upRent:
                    case R.id.tv_downArea:
                    case R.id.tv_upArea:
                        for (int id : rd_orders) {
                            if (id == view.getId()) {
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                    selectedFilterMap.put("order", "");
                                } else {
                                    view.setSelected(true);
                                    selectedFilterMap.put("order", view.getTag().toString());
                                }
                            } else {
                                popupWindow.getContentView().findViewById(id).setSelected(false);
                            }
                        }
                        break;
                    case R.id.rd_direction_1:
                    case R.id.rd_direction_2:
                    case R.id.rd_direction_3:
                    case R.id.rd_direction_4:
                    case R.id.rd_direction_5:
                        for (int id : rd_directions) {
                            if (id == view.getId()) {
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                    selectedFilterMap.put("direction", "");
                                } else {
                                    view.setSelected(true);
                                    selectedFilterMap.put("direction", view.getTag().toString());
                                }
                            } else {
                                popupWindow.getContentView().findViewById(id).setSelected(false);
                            }
                        }
                        break;
                    case R.id.rd_room_1:
                    case R.id.rd_room_2:
                    case R.id.rd_room_3:
                    case R.id.rd_room_4:
                        for (int id : rd_rooms) {
                            if (id == view.getId()) {
                                if (view.isSelected()) {
                                    view.setSelected(false);
                                    selectedFilterMap.put("bedroom", "");
                                } else {
                                    view.setSelected(true);
                                    selectedFilterMap.put("bedroom", view.getTag().toString());
                                }
                            } else {
                                popupWindow.getContentView().findViewById(id).setSelected(false);
                            }
                        }
                        break;

                }
            }
        };
        view.findViewById(R.id.tv_clear).setOnClickListener(popupWindowClickListener);
        view.findViewById(R.id.bt_sure).setOnClickListener(popupWindowClickListener);
        for (int id : rd_orders) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
        for (int id : rd_directions) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
        for (int id : rd_rooms) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
        final TextView tvRentRange = view.findViewById(R.id.tv_rent_rang);
        RangeSeekBar rangeSeekBar = view.findViewById(R.id.rangeSeekBar);
        rangeSeekBar.setRange(0, 101);
        rangeSeekBar.setValue(0f, 101f);
        rangeSeekBar.setRangeInterval(0f);
        rangeSeekBar.setTickMarkMode(RangeSeekBar.TRICK_MARK_MODE_NUMBER);
        rangeSeekBar.setTickMarkGravity(RangeSeekBar.TRICK_MARK_GRAVITY_CENTER);
        rangeSeekBar.setTickMarkTextColor(rangeSeekBar.getProgressDefaultColor());
        rangeSeekBar.setTickMarkInRangeTextColor(rangeSeekBar.getProgressColor());
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                if ((leftValue == 0f && rightValue >= view.getMaxProgress())) {
                    tvRentRange.setVisibility(View.INVISIBLE);
                    selectedFilterMap.put("startRent", "");
                    selectedFilterMap.put("endRent", "200000");
                }else if (leftValue >= view.getMaxProgress()){
                    tvRentRange.setVisibility(View.VISIBLE);
                    tvRentRange.setText("不限");
                    selectedFilterMap.put("startRent", "200000");
                    selectedFilterMap.put("endRent", "200000");
                }else{
                    tvRentRange.setVisibility(View.VISIBLE);
                    selectedFilterMap.put("startRent", (int) leftValue * 100);
                    if (rightValue >= view.getMaxProgress()){
                        selectedFilterMap.put("endRent", "200000");
                        tvRentRange.setText("¥ " + ((int) leftValue * 100) + "-不限" + " 元/月");
                    }else{
                        tvRentRange.setText("¥ " + ((int) leftValue * 100) + "-" + ((int) rightValue * 100) + " 元/月");
                        selectedFilterMap.put("endRent", (int) rightValue * 100);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        CityPopWindow.getDefault(this).dissmiss();
        super.onDestroy();
    }
}
