package com.sj.rentinghouse.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.widgets.CommonPopupWindow;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.TrainAdapter;
import com.sj.rentinghouse.bean.TrainLineInfo;
import com.sj.rentinghouse.http.API;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunj on 2018/7/29.
 */

public class TrainPopWindow {
    CommonPopupWindow popupWindow;
    EasyRecyclerView rylView;
    EasyRecyclerView rylView1;
    TrainAdapter adapter;
    TrainAdapter adapter1;
    Context context;
    String cityCode = "";
    String trainLineStr = "";
    String trainSubwayStr = "";

    OnAdapterItemClickListener onAdapterItemClickListener;
    static volatile TrainPopWindow Instance;

    public TrainPopWindow(Context context) {
        this.context = context;
    }

    public static TrainPopWindow getDefault(Context context) {
        if (Instance == null) {
            synchronized (TrainPopWindow.class) {
                if (Instance == null) {
                    Instance = new TrainPopWindow(context);
                }
            }
        }
        return Instance;
    }

    public TrainPopWindow setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
        return this;
    }

    public TrainPopWindow setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener) {
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(onDissmissListener);
        }
        return this;
    }

    public void show(View view) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAsDropDown(view);
        }
        rylView.showProgress();
        rylView1.showEmpty();
        adapter.clear();
        adapter1.clear();

        rylView.post(new Runnable() {
            @Override
            public void run() {
                getTrainLine();
            }
        });
    }

    public void dissmiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            Instance = null;
            rylView = null;
            rylView1 = null;
            adapter = null;
            adapter1 = null;
            context = null;
            cityCode = "";
            trainLineStr = "";
            trainSubwayStr = "";
            onAdapterItemClickListener = null;
        }
    }

    private void getTrainLine() {
        API.getLine(cityCode, new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                if (!TextUtils.isEmpty(data)) {
                    initTrainData(data);
                } else {
//                    ToastUtils.showShortToast("地铁线路数据为空");
                    rylView.showEmpty();
                }
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                rylView.showError();
            }
        });
    }

    private void getSubwayData(final String lineId) {
        rylView1.showProgress();
        API.getSubway(cityCode, lineId, new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                if (!TextUtils.isEmpty(data)) {
                    initSubwayData(lineId, data);
                } else {
//                    ToastUtils.showShortToast("地铁站点数据为空");
                    rylView1.showEmpty();
                }
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                rylView1.showError();
            }
        });
    }

    public TrainPopWindow init(String cityCode, String trainLine, String trainSubway) {
        this.cityCode = cityCode;
        this.trainLineStr = trainLine;
        this.trainSubwayStr = trainSubway;
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

    List<TrainLineInfo> trainSubwayList = new ArrayList<>();

    public void initSubwayData(String lineId, String subwayData) {
        trainSubwayList.clear();
        adapter1.clear();
        String[] trainSubwayArray = subwayData.split(",");
        for (String subway : trainSubwayArray) {
            TrainLineInfo trainLineInfo = new TrainLineInfo();
            trainLineInfo.setName(subway);
            trainLineInfo.setId(lineId);
            if (!TextUtils.isEmpty(trainSubwayStr) && trainSubwayStr.equals(subway)) {
                trainLineInfo.setStatus("-100");
            }
            trainSubwayList.add(trainLineInfo);
        }
        adapter1.addAll(trainSubwayList);
    }

    List<TrainLineInfo> trainLineList = new ArrayList<>();

    public void initTrainData(final String trainLines) {
        trainLineList.clear();
        String[] trainLineArray = trainLines.split(",");
        if (trainLineArray != null && trainLineArray.length > 0) {
            boolean hasChoose = false;
            String lineId = "";
            for (String line : trainLineArray) {
                String[] lineArray = line.split("-");
                TrainLineInfo trainLineInfo = new TrainLineInfo();
                if (lineArray.length > 0) {
                    trainLineInfo.setName(lineArray[0]);
                }
                if (lineArray.length > 1) {
                    trainLineInfo.setId(lineArray[1]);
                }
                if (!TextUtils.isEmpty(trainLineStr) && trainLineStr.equals(line)) {
                    trainLineInfo.setStatus("-100");
                    hasChoose = true;
                    lineId = trainLineInfo.getId();
                }
                trainLineList.add(trainLineInfo);
            }
            if (!hasChoose) {
                if (!trainLineList.isEmpty()) {
                    trainLineList.get(0).setStatus("-100");
                    lineId = trainLineList.get(0).getId();
                    this.trainLineStr = trainLineList.get(0).toDataString();
                }
            }
            adapter.addAll(trainLineList);
            getSubwayData(lineId);
        }
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
        adapter = new TrainAdapter(context);
        adapter1 = new TrainAdapter(context);
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TrainLineInfo trainLineInfo = adapter.getItem(position);
                if (!TextUtils.isEmpty(trainLineInfo.getStatus()) && trainLineInfo.getStatus().equals("-100")) {
                    return;
                }
                trainLineStr = trainLineInfo.toDataString();
                adapter.getItem(position).setStatus("-100");
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (i != position) {
                        adapter.getItem(i).setStatus("");
                    }
                }
                adapter.notifyDataSetChanged();
                getSubwayData(trainLineInfo.getId());
            }
        });
        adapter1.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                TrainLineInfo trainLineInfo = adapter1.getItem(position);
                if (!TextUtils.isEmpty(trainLineInfo.getStatus()) && trainLineInfo.getStatus().equals("-100")) {
                    return;
                }
                for (int i = 0; i < adapter1.getCount(); i++) {
                    if (i == position) {
                        trainSubwayStr = trainLineInfo.getName();
                        trainLineInfo.setStatus("-100");
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
                dissmiss();
                trainLineStr = null;
                trainSubwayStr = null;
                if (onAdapterItemClickListener != null) {
                    onAdapterItemClickListener.onItemSelected(null, null);
                }
            }
        });
        view.findViewById(R.id.bt_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(trainLineStr)) {
                    ToastUtils.showShortToast("请选择地铁线路");
                    return;
                }
                if (TextUtils.isEmpty(trainSubwayStr)) {
                    ToastUtils.showShortToast("请选择地铁站点");
                    return;
                }
                dissmiss();
                if (onAdapterItemClickListener != null) {
                    onAdapterItemClickListener.onItemSelected(trainLineStr, trainSubwayStr);
                }
            }
        });
    }

    public interface OnAdapterItemClickListener {
        void onItemSelected(String trainLine, String trainSubway);
    }
}
