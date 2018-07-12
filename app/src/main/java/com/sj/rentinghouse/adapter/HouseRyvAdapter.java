package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.HouseInfo;

/**
 * Created by Sunj on 2018/7/8.
 */

public class HouseRyvAdapter extends RecyclerArrayAdapter<HouseInfo> {
    public HouseRyvAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new HourseRyvHolder(parent);
    }

    public static class HourseRyvHolder extends BaseViewHolder {
        public HourseRyvHolder(ViewGroup parent) {
            super(parent, R.layout.hourse_card);
        }

        @Override
        public void setData(Object data) {
            super.setData(data);
        }
    }
}
