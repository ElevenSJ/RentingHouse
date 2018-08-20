package com.sj.rentinghouse.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.widgets.CommonPopupWindow;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.CityAdapter;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.bean.CityInfo;
import com.zaaach.citypicker.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunj on 2018/7/29.
 */

public class CityPopWindow {
    CommonPopupWindow popupWindow;
    EasyRecyclerView rylView;
    EasyRecyclerView rylView1;
    CityAdapter adapter;
    CityAdapter adapter1;
    Context context;
    String cityCode = "";

    CityInfo cityInfo;
    CityInfo districtInfo;
    OnAdapterItemClickListener onAdapterItemClickListener;
    static volatile CityPopWindow Instance;

    public CityPopWindow(Context context) {
        this.context = context;
    }

    public static CityPopWindow getDefault(Context context) {
        if (Instance == null) {
            synchronized (CityPopWindow.class) {
                if (Instance == null) {
                    Instance = new CityPopWindow(context);
                }
            }
        }
        return Instance;
    }

    public CityPopWindow setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
        return this;
    }

    public CityPopWindow setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener) {
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(onDissmissListener);
        }
        return this;
    }

    public void show(View view) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAsDropDown(view);
        }
    }

    public void dissmiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            Instance = null;
        }
    }

    public CityPopWindow init() {
        if (popupWindow == null) {
            popupWindow = new CommonPopupWindow.Builder(context)
                    //设置PopupWindow布局
                    .setView(R.layout.dialog_city_choose)
                    //设置宽高
                    .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                            DisplayUtils.dip2px(context, 400))
                    //设置动画
                    .setAnimationStyle(R.style.DefaultCityPickerAnimation)
                    //设置背景颜色，取值范围0.0f-1.0f 值越小越暗 1.0f为透明
                    .setBackGroundLevel(1.0f)
                    //设置PopupWindow里的子View及点击事件
                    .setViewOnclickListener(new CommonPopupWindow.ViewInterface() {
                        @Override
                        public void getChildView(View view, int layoutResId) {
                            initPopView(view);
//                            initCityData((String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, ""));
//                            initDistrictData(null);
                        }
                    })
                    //设置外部是否可点击 默认是true
                    .setOutsideTouchable(true)
                    .create();
        }
        return this;
    }

    public CityPopWindow initDistrictData(String districtName) {
        adapter1.clear();
        final List<CityInfo> districts = new ArrayList<>();
        for (Map.Entry<String, String> entry : App.allCityMap.get(cityCode).entrySet()) {
            CityInfo cityInfo = new CityInfo(entry.getValue(), entry.getValue(), null, entry.getKey());
            if (districtName != null && districtName.equals(entry.getValue())) {
                this.districtInfo = cityInfo;
                cityInfo.setStatus("-100");
            }
            districts.add(cityInfo);
        }
        adapter1.addAll(districts);
        return this;
    }
    List<CityInfo> citys = new ArrayList<>();
    public CityPopWindow initCityData(final String cityName) {
        adapter.clear();
        citys.clear();
        if (App.allCities != null && !App.allCities.isEmpty()) {
            for (City city : App.allCities) {
                CityInfo cityInfo = new CityInfo(city.getName(), city.getProvince(), city.getPinyin(), city.getCode());
                if (cityName != null && cityName.contains(cityInfo.getName())) {
                    this.cityInfo = cityInfo;
                    cityCode = cityInfo.getCode();
                    cityInfo.setStatus("-100");
                }
                citys.add(cityInfo);
            }
            adapter.addAll(citys);
        }else{
            new SerializeInfoGetTask(){
                @Override
                protected void onPostExecute(Object obj) {
                    super.onPostExecute(obj);
                    if (obj!=null) {
                        citys = (List<CityInfo>) obj;
                        for (CityInfo cityInfo : citys) {
                            App.allCities.add(new City(cityInfo.getName(), cityInfo.getProvince(), cityInfo.getPinyin(), cityInfo.getCode()));
                        }
                        for (City city : App.allCities) {
                            CityInfo cityInfo = new CityInfo(city.getName(), city.getProvince(), city.getPinyin(), city.getCode());
                            if (cityName != null && cityName.contains(cityInfo.getName())) {
                                CityPopWindow.this.cityInfo = cityInfo;
                                cityCode = cityInfo.getCode();
                                cityInfo.setStatus("-100");
                            }
                            citys.add(cityInfo);
                        }
                        adapter.addAll(citys);
                    }
                }
            }.execute(citys, NameSpace.FILE_CITY_NAME);
        }
        return this;
    }

    private void initPopView(View view) {
        rylView = view.findViewById(R.id.ryl_view);
        rylView1 = view.findViewById(R.id.ryl_view1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView1.setLayoutManager(layoutManager1);
        DividerDecoration dividerDecoration = new DividerDecoration(context.getResources().getColor(R.color.gray_AD), 1, 0, 0);
        dividerDecoration.setDrawLastItem(true);
        DividerDecoration dividerDecoration1 = new DividerDecoration(context.getResources().getColor(R.color.gray_AD), 1, 0, 0);
        dividerDecoration1.setDrawLastItem(true);
        rylView.addItemDecoration(dividerDecoration);
        rylView1.addItemDecoration(dividerDecoration1);
        adapter = new CityAdapter(context);
        adapter1 = new CityAdapter(context);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (i == position) {
                        cityInfo = adapter.getItem(position);
                        adapter.getItem(i).setStatus("-100");
                    } else {
                        adapter.getItem(i).setStatus("");
                    }
                }
                adapter.notifyDataSetChanged();
                cityCode = cityInfo.getCode();
                initDistrictData(null);
            }
        });
        adapter1.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < adapter1.getCount(); i++) {
                    if (i == position) {
                        districtInfo = adapter1.getItem(position);
                        adapter1.getItem(i).setStatus("-100");
                    } else {
                        adapter1.getItem(i).setStatus("");
                    }
                }
                adapter1.notifyDataSetChanged();
            }
        });
        rylView.setAdapter(adapter);
        rylView1.setAdapter(adapter1);

        view.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                districtInfo = null;
                cityInfo = null;
                if (onAdapterItemClickListener != null) {
                    onAdapterItemClickListener.onItemSelected(cityInfo, districtInfo);
                }
            }
        });
        view.findViewById(R.id.bt_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityInfo == null) {
                    ToastUtils.showShortToast("请选择城市");
                    return;
                }
                if (districtInfo == null) {
                    ToastUtils.showShortToast("请选择区域");
                    return;
                }
                popupWindow.dismiss();
                if (onAdapterItemClickListener != null) {
                    onAdapterItemClickListener.onItemSelected(cityInfo, districtInfo);
                }
            }
        });
    }

    public interface OnAdapterItemClickListener {
        void onItemSelected(CityInfo cityInfo, CityInfo cityInfo1);
    }
}
