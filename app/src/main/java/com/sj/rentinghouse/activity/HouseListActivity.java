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
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.widgets.CommonPopupWindow;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.CityPopWindow;
import com.sj.rentinghouse.utils.NameSpace;
import com.sj.rentinghouse.utils.TrainPopWindow;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
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

    String trainLineStr = "";
    String trainSubwayStr = "";

    Map<String, Object> map = new ArrayMap<>();
    @BindView(R.id.tv_item_train)
    TextView tvItemTrain;
    @BindView(R.id.tv_item_area)
    TextView tvItemArea;
    @BindView(R.id.tv_item_filter)
    TextView tvItemFilter;
    @BindView(R.id.tv_item_sort)
    TextView tvItemSort;

    Map<String, Object> selectedFilterMap = new HashMap<>();
    Map<String, Object> selectedSortMap = new HashMap<>();

    CommonPopupWindow filterPopupWindow;
    CommonPopupWindow sortPopupWindow;
    final int[] rd_orders = {R.id.tv_default,R.id.tv_downRent, R.id.tv_upRent, R.id.tv_downArea, R.id.tv_upArea};
    final int[] rd_directions = {R.id.rd_direction_1, R.id.rd_direction_2, R.id.rd_direction_3, R.id.rd_direction_4, R.id.rd_direction_5};
    final int[] rd_rooms = {R.id.rd_room_1, R.id.rd_room_2, R.id.rd_room_3, R.id.rd_room_4};

    @Override
    public int getContentView() {
        return R.layout.activity_house_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
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

//        tvItemArea.setText("区域" + "( " + cityName + districtName + " )");

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
        tvItemSort.setSelected(false);
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

    @OnClick({R.id.layout_train, R.id.layout_area, R.id.layout_filter,R.id.layout_sort, R.id.img_top_right, R.id.tv_top_title})
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
                tvItemTrain.setSelected(true);
                tvItemFilter.setSelected(false);
                tvItemArea.setSelected(false);
                tvItemSort.setSelected(false);
                TrainPopWindow.getDefault(this).init(cityCode,trainLineStr,trainSubwayStr).setOnAdapterItemClickListener(new TrainPopWindow.OnAdapterItemClickListener() {
                    @Override
                    public void onItemSelected(String trainLine, String trainSubway) {
                        if (!TextUtils.isEmpty(trainLine)&&!TextUtils.isEmpty(trainSubway)){
                            trainLineStr =trainLine;
                            trainSubwayStr= trainSubway;
                        }else{
                            trainLineStr = "";
                            trainSubwayStr= "";
                        }
//                        map.put("line", trainLineStr);
                        map.put("subWay", trainSubwayStr);
                        onRefresh();
                    }
                }).setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        tvItemTrain.setSelected(false);
                        tvItemArea.setSelected(false);
                        tvItemFilter.setSelected(false);
                        tvItemSort.setSelected(false);
                    }
                }).show(view);
                break;
            case R.id.layout_area:
                tvItemTrain.setSelected(false);
                tvItemFilter.setSelected(false);
                tvItemArea.setSelected(true);
                tvItemSort.setSelected(false);
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
//                        tvItemArea.setText("区域" + "( " + cityName + districtName + " )");
                        onRefresh();
                    }
                }).setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        tvItemTrain.setSelected(false);
                        tvItemArea.setSelected(false);
                        tvItemFilter.setSelected(false);
                        tvItemSort.setSelected(false);
                    }
                }).show(view);
                break;
            case R.id.layout_filter:
                tvItemTrain.setSelected(false);
                tvItemArea.setSelected(false);
                tvItemFilter.setSelected(true);
                tvItemSort.setSelected(false);
                if (filterPopupWindow == null) {
                    filterPopupWindow = new CommonPopupWindow.Builder(this)
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
                    filterPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            tvItemTrain.setSelected(false);
                            tvItemArea.setSelected(false);
                            tvItemFilter.setSelected(false);
                            tvItemSort.setSelected(false);
                        }
                    });
                } else {
                    initPopData();
                }
                filterPopupWindow.showAsDropDown(view);
                break;
            case R.id.layout_sort:
                tvItemTrain.setSelected(false);
                tvItemArea.setSelected(false);
                tvItemFilter.setSelected(false);
                tvItemSort.setSelected(true);
                if (sortPopupWindow == null) {
                    sortPopupWindow = new CommonPopupWindow.Builder(this)
                            //设置PopupWindow布局
                            .setView(R.layout.pop_house_sort)
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
                                    initSortPopView(view);
                                }
                            })
                            //设置外部是否可点击 默认是true
                            .setOutsideTouchable(true)
                            .create();
                    sortPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            tvItemTrain.setSelected(false);
                            tvItemArea.setSelected(false);
                            tvItemFilter.setSelected(false);
                            tvItemSort.setSelected(false);
                        }
                    });
                } else {
                    initSortPopData();
                }
                sortPopupWindow.showAsDropDown(view);
                break;
        }
    }

    private void initSortPopView(View view) {
        View.OnClickListener popupWindowClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_default:
                        sortPopupWindow.dismiss();
                        for (Map.Entry<String, Object> entry : selectedSortMap.entrySet()) {
                            map.remove(entry.getKey());
                        }
                        selectedSortMap.clear();
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
                                    selectedSortMap.put("order", "");
                                } else {
                                    view.setSelected(true);
                                    selectedSortMap.put("order", view.getTag().toString());
                                }
                            } else {
                                sortPopupWindow.getContentView().findViewById(id).setSelected(false);
                            }
                        }
                        sortPopupWindow.dismiss();
                        map.putAll(selectedSortMap);
                        onRefresh();
                        break;

                }
            }
        };
        for (int id : rd_orders) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
    }
    private void initSortPopData() {
        for (int id : rd_orders) {
            View view = sortPopupWindow.getContentView().findViewById(id);
            if (selectedSortMap.containsKey("order")) {
                if (selectedSortMap.get("order").equals(view.getTag().toString())) {
                    view.setSelected(true);
                } else {
                    view.setSelected(false);
                }
            } else {
                view.setSelected(false);
            }
        }
    }
    private void initPopData() {
//        for (int id : rd_orders) {
//            View view = filterPopupWindow.getContentView().findViewById(id);
//            if (selectedFilterMap.containsKey("order")) {
//                if (selectedFilterMap.get("order").equals(view.getTag().toString())) {
//                    view.setSelected(true);
//                } else {
//                    view.setSelected(false);
//                }
//            } else {
//                view.setSelected(false);
//            }
//        }
        for (int id : rd_directions) {
            View view = filterPopupWindow.getContentView().findViewById(id);
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
            View view = filterPopupWindow.getContentView().findViewById(id);
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
        RangeSeekBar rangeSeekBar = filterPopupWindow.getContentView().findViewById(R.id.rangeSeekBar);
        if (selectedFilterMap.containsKey("startRent")) {
            float leftValue = TextUtils.isEmpty(selectedFilterMap.get("startRent").toString()) ? 0f : selectedFilterMap.get("startRent").equals("200000") ? 100f : Float.valueOf(selectedFilterMap.get("startRent").toString()) / 100f - 1f;
            float rightValue = TextUtils.isEmpty(selectedFilterMap.get("endRent").toString()) ? 0f : selectedFilterMap.get("endRent").equals("200000") ? 100f : Float.valueOf(selectedFilterMap.get("endRent").toString()) / 100f - 1f;
            rangeSeekBar.setValue(leftValue, rightValue);
        } else {
            try {
                rangeSeekBar.setValue(0f, 100f);
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
                        filterPopupWindow.dismiss();
                        for (Map.Entry<String, Object> entry : selectedFilterMap.entrySet()) {
                            map.remove(entry.getKey());
                        }
                        selectedFilterMap.clear();
                        onRefresh();
                        break;
                    case R.id.bt_sure:
                        filterPopupWindow.dismiss();
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
                                filterPopupWindow.getContentView().findViewById(id).setSelected(false);
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
                                filterPopupWindow.getContentView().findViewById(id).setSelected(false);
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
                                filterPopupWindow.getContentView().findViewById(id).setSelected(false);
                            }
                        }
                        break;

                }
            }
        };
        view.findViewById(R.id.tv_clear).setOnClickListener(popupWindowClickListener);
        view.findViewById(R.id.bt_sure).setOnClickListener(popupWindowClickListener);
//        for (int id : rd_orders) {
//            view.findViewById(id).setOnClickListener(popupWindowClickListener);
//        }
        for (int id : rd_directions) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
        for (int id : rd_rooms) {
            view.findViewById(id).setOnClickListener(popupWindowClickListener);
        }
        final TextView tvRentRange = view.findViewById(R.id.tv_rent_rang);
        RangeSeekBar rangeSeekBar = view.findViewById(R.id.rangeSeekBar);
        rangeSeekBar.setRange(0, 100);
        rangeSeekBar.setValue(0f, 100f);
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
                } else if (leftValue >= view.getMaxProgress()) {
                    tvRentRange.setVisibility(View.VISIBLE);
                    tvRentRange.setText("不限");
                    selectedFilterMap.put("startRent", "200000");
                    selectedFilterMap.put("endRent", "200000");
                } else {
                    tvRentRange.setVisibility(View.VISIBLE);
                    selectedFilterMap.put("startRent", (int) (leftValue + 1f) * 100);
                    if (rightValue >= view.getMaxProgress()) {
                        selectedFilterMap.put("endRent", "200000");
                        tvRentRange.setText("¥ " + ((int) (leftValue + 1f) * 100) + "-不限" + " 元/月");
                    } else {
                        tvRentRange.setText("¥ " + ((int) (leftValue + 1f) * 100) + "-" + ((int) (rightValue + 1f) * 100) + " 元/月");
//                        tvRentRange.setText("¥ " + ((int) (leftValue + 1f) * 100) + (rightValue == 0 ? "" : "-" + ((int) (rightValue + 1f) * 100)) + " 元/月");
                        selectedFilterMap.put("endRent", ((int) (rightValue + 1f) * 100));

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
        TrainPopWindow.getDefault(this).dissmiss();
        if (sortPopupWindow!=null&&sortPopupWindow.isShowing()){
            sortPopupWindow.dismiss();
        }
        if (filterPopupWindow!=null&&filterPopupWindow.isShowing()){
            filterPopupWindow.dismiss();
        }
        super.onDestroy();
    }
}
