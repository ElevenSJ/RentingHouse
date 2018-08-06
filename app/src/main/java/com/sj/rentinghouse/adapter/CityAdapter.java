package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.CityInfo;

/**
 * Created by Sunj on 2018/7/8.
 */

public class CityAdapter extends RecyclerArrayAdapter<CityInfo> {
    public CityAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new CityRyvHolder(parent);
    }

    public static class CityRyvHolder extends BaseViewHolder<CityInfo> {
        TextView tvWord;
        public CityRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_city_info);
            tvWord = $(R.id.tv_word);
        }

        @Override
        public void setData(CityInfo data) {
            super.setData(data);
            tvWord.setText(data.getCityName());
            if (data.getStatus()!=null&&
                    data.getStatus().equals("-100")){
                tvWord.setSelected(true);
            }else{
                tvWord.setSelected(false);
            }
        }
    }
}
