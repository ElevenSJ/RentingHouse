package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.NoticeInfo;

/**
 * Created by Sunj on 2018/7/8.
 */

public class NoticeRyvAdapter extends RecyclerArrayAdapter<NoticeInfo> {
    public NoticeRyvAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoticeRyvHolder(parent);
    }

    public static class NoticeRyvHolder extends BaseViewHolder<NoticeInfo> {
        TextView txtTitle;
        TextView txtdetail;

        public NoticeRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_notice);
            txtTitle = $(R.id.txt_message_title);
            txtdetail = $(R.id.txt_message_detail);
        }

        @Override
        public void setData(NoticeInfo data) {
            super.setData(data);
            txtTitle.setText(data.getAddTime());
            txtdetail.setText(data.getContent());
//            txtTime.setText(!TextUtils.isEmpty(data.getStatus()) && data.getStatus().equals("1") ? "未读" : "已读");
        }
    }
}
