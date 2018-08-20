package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.TrainLineInfo;

/**
 * Created by Sunj on 2018/7/8.
 */

public class TrainAdapter extends RecyclerArrayAdapter<TrainLineInfo> {
    public TrainAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new TrainRyvHolder(parent);
    }

    public static class TrainRyvHolder extends BaseViewHolder<TrainLineInfo> {
        TextView tvWord;
        public TrainRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_city_info);
            tvWord = $(R.id.tv_word);
        }

        @Override
        public void setData(TrainLineInfo data) {
            super.setData(data);
            tvWord.setText(data.getName());
            if (!TextUtils.isEmpty(data.getStatus())&&
                    data.getStatus().equals("-100")){
                tvWord.setSelected(true);
            }else{
                tvWord.setSelected(false);
            }
        }
    }
}
