package com.sj.rentinghouse.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.itheima.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.DateUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.HouseDetail;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/22.
 */

public class HouseManagerActivity extends AppBaseActivity {
    @BindView(R.id.img_house_icon)
    RoundedImageView imgHouseIcon;
    @BindView(R.id.tv_other_time)
    TextView tvOtherTime;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
    @BindView(R.id.tv_hourse_name)
    TextView tvHouseName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_hourse_room)
    TextView tvHouseRoom;
    @BindView(R.id.tv_room_area)
    TextView tvRoomArea;
    @BindView(R.id.tv_room_direction)
    TextView tvRoomDirection;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_img_count)
    TextView tvImgCount;
    @BindView(R.id.tv_house_brief_info)
    TextView tvHouseBriefInfo;
    HouseDetail data;
    String id;

    @BindView(R.id.img_top_right)
    TextView imgTopRight;
    @BindView(R.id.tv_rent_house)
    TextView tvRentHouse;

    @Override
    public int getContentView() {
        return R.layout.activity_house_manager;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        id = getIntent().getStringExtra("id");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "房产管理");
        imgTopRight.setText("添加房产");
        if (TextUtils.isEmpty(id)) {
            return;
        }
        getData();
    }

    private void getData() {
        showProgress();
        API.queryHouseInfo(id, new ServerResultBack<BaseResponse<HouseDetail>, HouseDetail>() {
            @Override
            public void onSuccess(HouseDetail data) {
                if (isDestory()) {
                    return;
                }
                HouseManagerActivity.this.data = data;
                initViewData();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()) {
                    return;
                }
                dismissProgress();
            }
        });
    }

    private void initViewData() {
        if (data == null) {
            return;
        }
        String[] housePictures = TextUtils.isEmpty(data.getHousePicture()) || data.getHousePicture().equalsIgnoreCase("null") ? null : data.getHousePicture().split(",");
        if (housePictures != null && housePictures.length > 0) {
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
        if (!data.getType().equals("1")) {
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
        tvPrice.setText("¥" + data.getRent() + "元/月");
        try {
            tvStatus.setText(App.rentStatusArray[Integer.valueOf(data.getStatus())]);
        } catch (Exception e) {
            tvStatus.setText(data.getStatus());
            Logger.e("房源出租转型异常");
        }
        StringBuffer briefBuffer = new StringBuffer();
        briefBuffer.append("租期：");
        briefBuffer.append(data.getStartEndTime());
//        briefBuffer.append(TextUtils.isEmpty(data.getStartTime())?"":DateUtils.formatDate(Long.valueOf(data.getStartTime()))+"至"+DateUtils.formatDate(Long.valueOf(data.getEndTime())));
        briefBuffer.append("\n");
        briefBuffer.append("付款方式：");
        String payMethod = "";
        try {
            payMethod = App.payMethodArray[Integer.valueOf(data.getPayMethod()) - 1];
        } catch (NumberFormatException e) {
            Logger.e("房源付款方式转型异常");
            payMethod = data.getPayMethod();
        } catch (IndexOutOfBoundsException e) {
            Logger.e("房源付款方式转型异常");
            payMethod = data.getPayMethod();
        }
        briefBuffer.append(payMethod);
        briefBuffer.append("\n");

        briefBuffer.append("最近闲置期：");
        briefBuffer.append(data.getUnusedTime());
        tvHouseBriefInfo.setText(briefBuffer.toString());

    }

    @OnClick({R.id.tv_modify_house, R.id.tv_delete_house, R.id.img_top_right, R.id.tv_rent_house})
    public void onViewClicked(View view) {
        if (data == null) {
            ToastUtils.showShortToast("未获取到房产详情");
            return;
        }
        switch (view.getId()) {
            case R.id.tv_modify_house:
                Intent intent = new Intent(this, AddOrUpdateHouseActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_delete_house:
                DialogUtils.showDeleteDialog(this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        API.delHouse(data.getId(), new CommonCallback() {
                            @Override
                            public void onSuccess(String message) {
                                ToastUtils.showShortToast("已删除该房产");
                                EventManger.getDefault().postMyRefreshEvent();
                                EventManger.getDefault().postMainRefreshEvent();
                                finish();
                            }
                        });
                    }
                });
                break;
            case R.id.tv_rent_house:
                Intent intent2 = new Intent(this,SetRentActivity.class);
                startActivityForResult(intent2,100);
                break;
            case R.id.img_top_right:
                Intent intent1 = new Intent();
                intent1.setClass(view.getContext(), AddOrUpdateHouseActivity.class);
                startActivity(intent1);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        super.onActivityResult(requestCode, resultCode, intentData);
        if (requestCode == 100 && resultCode == RESULT_OK){
            showProgress();
            final String startTime = intentData.getStringExtra("startTime");
            final String endTime =  intentData.getStringExtra("endTime");
            API.upHouseStatus(data.getId(), DateUtils.getString2Milli(startTime) + "", DateUtils.getString2Milli(endTime) + "", new CommonCallback() {
                @Override
                public void onSuccess(String message) {
                    EventManger.getDefault().postMyRefreshEvent();
                    if (isDestory()) {
                        return;
                    }
                    data.setStartEndTime(startTime + " 至 " + endTime);
                    data.setStatus("1");
                    initViewData();
                }
                @Override
                public void onFinish() {
                    super.onFinish();
                    if (isDestory()) {
                        return;
                    }
                    dismissProgress();
                }
            });
        }
    }
}
