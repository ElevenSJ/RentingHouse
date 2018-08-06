package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.MyItem;
import com.sj.rentinghouse.bean.SearchWord;

/**
 * Created by Sunj on 2018/7/8.
 */

public class SearchWordAdapter extends RecyclerArrayAdapter<String> {
    public SearchWordAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new SearchWordRyvHolder(parent);
    }

    public static class SearchWordRyvHolder extends BaseViewHolder<String> {
        TextView tvSearchWord;
        public SearchWordRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_search_word);
            tvSearchWord = $(R.id.tv_search_word);
        }

        @Override
        public void setData(String data) {
            super.setData(data);
            tvSearchWord.setText(data);
        }
    }
}
