package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.roundedimageview.RoundedImageView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.utils.DateUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.AddOrUpdateOrderActivity;
import com.sj.rentinghouse.activity.HouseDetailActivity;
import com.sj.rentinghouse.activity.HouseManagerActivity;
import com.sj.rentinghouse.activity.OrderDetailActivity;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.bean.CallInfo;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.bean.MyHouseInfo;
import com.sj.rentinghouse.bean.MyLetHouseInfo;
import com.sj.rentinghouse.bean.MyOrderInfo;
import com.sj.rentinghouse.bean.OrderInfo;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 */

public class HouseRyvAdapter extends RecyclerArrayAdapter<HouseInfo> {

    public HouseRyvAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new HourseRyvHolder(parent);
    }

    public static class HourseRyvHolder extends BaseViewHolder<HouseInfo> {
        RoundedImageView imgHouseIcon;
        TextView tvOtherTime;
        TextView tvOrderStatus;
        TextView tvHouseName;
        TextView tvType;
        TextView tvHouseRoom;
        TextView tvRoomArea;
        TextView tvRoomDirection;
        TextView tvPrice;
        TextView tvStatus;
        TextView tvImgCount;

        public HourseRyvHolder(ViewGroup parent) {
            super(parent, R.layout.hourse_card);
            imgHouseIcon = $(R.id.img_house_icon);
            tvOtherTime = $(R.id.tv_other_time);
            tvOrderStatus = $(R.id.tv_order_status);
            tvHouseName = $(R.id.tv_hourse_name);
            tvType = $(R.id.tv_type);
            tvHouseRoom = $(R.id.tv_hourse_room);
            tvRoomArea = $(R.id.tv_room_area);
            tvRoomDirection = $(R.id.tv_room_direction);
            tvPrice = $(R.id.tv_price);
            tvStatus = $(R.id.tv_status);
            tvImgCount = $(R.id.tv_img_count);
        }

        @Override
        public void setData(final HouseInfo data) {
            super.setData(data);
            String[] housePictures = TextUtils.isEmpty(data.getHousePicture())||data.getHousePicture().equalsIgnoreCase("null") ? null : data.getHousePicture().split(",");
            if (housePictures!=null&&housePictures.length > 0) {
                ImageUtils.loadImageView(housePictures[0], imgHouseIcon);
                tvImgCount.setText(housePictures.length + "图");
                tvImgCount.setVisibility(View.VISIBLE);
            } else {
                imgHouseIcon.setImageResource(R.mipmap.ic_launcher);
                tvImgCount.setVisibility(View.GONE);
            }
            tvHouseName.setText(data.getVillage() + (TextUtils.isEmpty(data.getBedroom()) ? "" : " · " + data.getBedroom() + "居室"));
            if (!TextUtils.isEmpty(data.getType())) {
                Drawable drawable = null;
                try {
                    tvType.setText(App.typeArray[Integer.valueOf(data.getType()) - 1]);
                    if (data.getType().equals("1")) {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_whole_rent);
                    } else {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_joint_rent);
                    }
                    drawable.setBounds(0, 0, 50, 50);// 设置边界
                    tvType.setCompoundDrawables(drawable, null, null, null);
                } catch (NumberFormatException e) {
                    Logger.e("房源类型数转型异常");
                }
            }
            if (!TextUtils.isEmpty(data.getType()) && !data.getType().equals("1")) {
                tvHouseRoom.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(data.getHouseType())) {
                    try {
                        tvHouseRoom.setText(App.roomTypeArray[Integer.valueOf(data.getHouseType()) - 1]);
                    } catch (NumberFormatException e) {
                        tvHouseRoom.setText(data.getHouseType());
                        Logger.e("房源合租类型数转型异常");
                    }
                }
            } else {
                tvHouseRoom.setVisibility(View.GONE);
            }
            tvRoomArea.setText(data.getArea() + "㎡");
            try {
                tvRoomDirection.setText(App.directionArray[Integer.valueOf(data.getDirection()) - 1]);
            } catch (NumberFormatException e) {
                tvRoomDirection.setText(data.getDirection());
                Logger.e("房源朝向数转型异常");
            }
            tvPrice.setText("¥ " + data.getRent() + " 元/月");
            if (data instanceof OrderInfo) {
                tvOtherTime.setVisibility(View.VISIBLE);
                tvOrderStatus.setVisibility(View.VISIBLE);
                tvOtherTime.setText("预约看房时间：" + ((OrderInfo) data).getShowTime());
                if (!TextUtils.isEmpty(data.getStatus())) {
                    if (data.getStatus().equals("1") || data.getStatus().equals("2")) {
                        tvStatus.setText("取消约看");
                        tvStatus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (DateUtils.StandardFormatStrMin(((OrderInfo) data).getShowTime()) < 4 * 60) {
                                    ToastUtils.showShortToast("距约看时间不足4小时，不可取消约看");
                                    return;
                                }
                                DialogUtils.showMessageDialog(view.getContext(), "是否确定取消约看", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialogInterface, int i) {
                                        if (i != -100) {
                                            API.updateOrderStatus(((OrderInfo) data).getId(), "4", new CommonCallback() {
                                                @Override
                                                public void onSuccess(String message) {
                                                    dialogInterface.dismiss();
                                                    EventManger.getDefault().postTrackPageEvent(1);
                                                    EventManger.getDefault().postTrackRefreshEvent();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                    try {
                        tvOrderStatus.setText(App.statusArray[Integer.valueOf(data.getStatus()) - 1]);
                    } catch (NumberFormatException e) {
                        tvOrderStatus.setText(data.getStatus());
                        Logger.e("房源状态转型异常");
                    }
                }
            } else if (data instanceof MyOrderInfo) {
                tvOtherTime.setVisibility(View.VISIBLE);
                tvOrderStatus.setVisibility(View.VISIBLE);
                tvOtherTime.setText("申请时间：" + ((MyOrderInfo) data).getShowTime());
                if (!TextUtils.isEmpty(data.getStatus())) {
                    try {
                        tvOrderStatus.setText(App.statusArray[Integer.valueOf(data.getStatus()) - 1]);
                    } catch (NumberFormatException e) {
                        tvOrderStatus.setText(data.getStatus());
                        Logger.e("房源状态转型异常");
                    }
                }
            } else if (data instanceof CallInfo) {
                tvOtherTime.setVisibility(View.VISIBLE);
                tvOtherTime.setText("拨号时间：" + ((CallInfo) data).getAddTime());
            } else if (data instanceof MyHouseInfo||data instanceof MyLetHouseInfo) {
                if (data instanceof MyLetHouseInfo){
                    if (TextUtils.isEmpty(data.getToDate())) {
                        tvOtherTime.setVisibility(View.GONE);
                        tvOtherTime.setText("");
                    } else {
                        tvOtherTime.setVisibility(View.VISIBLE);
                        tvOtherTime.setText(data.getToDate().equals("0")?"今天即将到期":"还有" + data.getToDate() + "天即将到期");
                    }
                }
                try {
                    tvStatus.setText(App.rentStatusArray[Integer.valueOf(data.getStatus())]);
                } catch (NumberFormatException e) {
                    tvRoomDirection.setText(data.getStatus());
                    Logger.e("房源出租转型异常");
                } catch (IndexOutOfBoundsException e) {
                    tvRoomDirection.setText(data.getStatus());
                    Logger.e("房源出租转型异常");
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    if (data instanceof MyHouseInfo) {
                        intent.putExtra("id", data.getId());
                        intent.setClass(view.getContext(), HouseManagerActivity.class);
                    } else if (data instanceof OrderInfo) {
                        intent.putExtra("id", ((OrderInfo) data).getHouseId());
                        intent.setClass(view.getContext(), HouseDetailActivity.class);
                    } else if (data instanceof MyOrderInfo) {
                        intent.putExtra("id", data.getId());
                        intent.putExtra("status", data.getStatus());
                        intent.setClass(view.getContext(), OrderDetailActivity.class);
                    } else if (data instanceof CallInfo) {
                        intent.putExtra("id", ((CallInfo) data).getHouseId());
                        intent.setClass(view.getContext(), HouseDetailActivity.class);
                    } else {
                        intent.setClass(view.getContext(), HouseDetailActivity.class);
                        intent.putExtra("id", data.getId());
                    }
                    view.getContext().startActivity(intent);
                }
            });
        }
    }
}
