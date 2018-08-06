package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.bean.MyItem;

/**
 * Created by Sunj on 2018/7/8.
 */

public class MyRentRyvAdapter extends RecyclerArrayAdapter<MyItem> {
    public MyRentRyvAdapter(Context context) {
        super(context);
    }
    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return  new HourseRyvHolder(parent);
    }

    public static class HourseRyvHolder extends BaseViewHolder<MyItem> {
        TextView itemName;
        TextView itemRemark;
        ImageView itemImgRight;
        public HourseRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_my_rent);
            this.itemName = $(R.id.tv_item);
            this.itemRemark = $(R.id.tv_item_remark);
            this.itemImgRight = $(R.id.img_item_right_image);
        }

        @Override
        public void setData(MyItem data) {
            super.setData(data);
            itemName.setText(data.getItemName());
            if(!TextUtils.isEmpty(data.getItemRemark())){
                itemRemark.setText(data.getItemRemark());
            }
            if(data.getDrawableLeftId()>0){
                Drawable drawable = itemName.getContext().getResources().getDrawable(data.getDrawableLeftId());
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
                // param 左上右下
                itemName.setCompoundDrawables(drawable,null,null,null);
            }
            if (!TextUtils.isEmpty(data.getDrawableRight())){
                itemImgRight.setVisibility(View.VISIBLE);
                String drawablePath = data.getDrawableRight();
                if (drawablePath.startsWith("http")){
                    ImageUtils.loadImageView(drawablePath,itemImgRight);
                }else if (drawablePath.startsWith("R.drawable.")){
                    int id=itemImgRight.getContext().getResources().getIdentifier(drawablePath.replace("R.drawable.",""),"drawable",itemImgRight.getContext().getPackageName());
                    itemImgRight.setImageResource(id);
                }else if (drawablePath.startsWith("R.mipmap.")){
                    int id=itemImgRight.getContext().getResources().getIdentifier(drawablePath.replace("R.mipmap.",""),"mipmap",itemImgRight.getContext().getPackageName());
                    itemImgRight.setImageResource(id);
                }
            }
        }
    }
}
