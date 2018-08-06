package com.sj.rentinghouse.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sj.module_lib.glide.ImageUtils;
import com.sj.rentinghouse.bean.BannerInfo;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Sunj on 2018/7/15.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide 加载图片简单用法
        ImageUtils.loadImageView(((BannerInfo) path).getPictureUrl(), imageView);
    }

    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}