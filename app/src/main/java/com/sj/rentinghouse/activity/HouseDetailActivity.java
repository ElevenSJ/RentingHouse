package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.BannerInfo;
import com.sj.rentinghouse.bean.HouseDetail;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.GlideImageLoader;
import com.sj.rentinghouse.utils.NameSpace;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/17.
 */

public class HouseDetailActivity extends AppBaseActivity {
    String id;
    HouseDetail houseDetail;

    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.tv_hourse_name)
    TextView tvHourseName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_hourse_room)
    TextView tvHourseRoom;
    @BindView(R.id.tv_room_area)
    TextView tvRoomArea;
    @BindView(R.id.tv_room_direction)
    TextView tvRoomDirection;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_house_info2)
    TextView tvHouseInfo2;
    @BindView(R.id.tv_house_info1)
    TextView tvHouseInfo1;
    @BindView(R.id.layout_flex)
    GridLayout layoutFlex;
    @BindView(R.id.tv_house_desc)
    TextView tvHouseDesc;
    @BindView(R.id.tv_house_drequirement)
    TextView tvHouseDrequirement;
    int gridLayoutIndex = 0;

    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    public int getContentView() {
        return R.layout.activity_house_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        id = getIntent().getStringExtra("id");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "房屋详情");
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (selectList.size() > 0) {
                    PictureSelector.create(HouseDetailActivity.this).themeStyle(R.style.picture_main_style).openExternalPreview(position, selectList);
                }
            }
        });
        getHouseDetail();
    }

    private void getHouseDetail() {
        API.queryHouseInfo(id, new ServerResultBack<BaseResponse<HouseDetail>, HouseDetail>() {
            @Override
            public void onSuccess(HouseDetail data) {
                houseDetail = data;
                updateView();
            }
        });
    }

    private void updateView() {
        if (houseDetail == null) {
            return;
        }
        if (!TextUtils.isEmpty(houseDetail.getHousePicture())) {
            String[] bannerStrs = houseDetail.getHousePicture().split(",");
            List<BannerInfo> bannerInfoList = new ArrayList<>(bannerStrs.length);
            for (String bannerPic : bannerStrs) {
                selectList.add(new LocalMedia(bannerPic, 0, PictureMimeType.ofImage(), null));
                BannerInfo bannerInfo = new BannerInfo();
                bannerInfo.setPictureUrl(bannerPic);
                bannerInfoList.add(bannerInfo);
            }
            banner.setImages(bannerInfoList);
            banner.start();
        }

        tvHourseName.setText(houseDetail.getVillage() + (TextUtils.isEmpty(houseDetail.getBedroom()) ? "" : " · " + houseDetail.getBedroom() + "居室"));
        try {
            Drawable drawable = null;
            tvType.setText(App.typeArray[Integer.valueOf(houseDetail.getType()) - 1]);
            if (houseDetail.getType().equals("1")) {
                drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_whole_rent);
            } else {
                drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_joint_rent);
            }
            drawable.setBounds(0, 0, 50, 50);// 设置边界
            tvType.setCompoundDrawables(drawable, null, null, null);
        } catch (NumberFormatException e) {
            Logger.e("房源类型数转型异常");
        }
        if (!houseDetail.getType().equals("1")) {
            tvHourseRoom.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(houseDetail.getHouseType())) {
                try {
                    tvHourseRoom.setText(App.roomTypeArray[Integer.valueOf(houseDetail.getHouseType()) - 1]);
                } catch (NumberFormatException e) {
                    tvHourseRoom.setText(houseDetail.getHouseType());
                    Logger.e("房源合租类型数转型异常");
                }
            }
        } else {
            tvHourseRoom.setVisibility(View.GONE);
        }
        tvRoomArea.setText(houseDetail.getArea() + "㎡");
        try {
            tvRoomDirection.setText(App.directionArray[Integer.valueOf(houseDetail.getDirection()) - 1]);
        } catch (NumberFormatException e) {
            tvRoomDirection.setText(houseDetail.getDirection());
            Logger.e("房源朝向转型异常");
        }
        tvPrice.setText("¥" + houseDetail.getRent() + "元/月");

        StringBuffer houseInfo1Buffer = new StringBuffer();
        houseInfo1Buffer.append("面积：");
        houseInfo1Buffer.append(houseDetail.getArea() + "㎡");
        houseInfo1Buffer.append("\n");
        houseInfo1Buffer.append("楼层：");
        houseInfo1Buffer.append(houseDetail.getFloor() + "/" + houseDetail.getTotalFloor() + "层");
        houseInfo1Buffer.append("\n");
        houseInfo1Buffer.append("付款方式：");
        try {
            houseInfo1Buffer.append(App.payMethodArray[Integer.valueOf(houseDetail.getPayMethod()) - 1]);
        } catch (NumberFormatException e) {
            houseInfo1Buffer.append(houseDetail.getPayMethod());
            Logger.e("房源付款方式转型异常");
        }
        houseInfo1Buffer.append("\n");
        houseInfo1Buffer.append("地理位置：");
        houseInfo1Buffer.append(houseDetail.getCityName() + (TextUtils.isEmpty(houseDetail.getDistrictName())||houseDetail.getDistrictName().equalsIgnoreCase("null")?"":houseDetail.getDistrictName()) + "-" + tvHourseName.getText().toString());
        houseInfo1Buffer.append("\n");
        houseInfo1Buffer.append("最后更新时间：");
        houseInfo1Buffer.append(houseDetail.getUpdateTime());
        tvHouseInfo1.setText(houseInfo1Buffer.toString());

        StringBuffer houseInfo2Buffer = new StringBuffer();
        houseInfo2Buffer.append("户型：");
        houseInfo2Buffer.append(houseDetail.getBedroom() + "室" + houseDetail.getParlour() + "厅" + houseDetail.getToilet() + "卫");
        houseInfo2Buffer.append("\n");
        houseInfo2Buffer.append("装修：");
        try {
            houseInfo2Buffer.append(App.renovationArray[Integer.valueOf(houseDetail.getRenovation()) - 1]);
        } catch (NumberFormatException e) {
            houseInfo2Buffer.append(houseDetail.getRenovation());
            Logger.e("房源装修转型异常");
        }
        tvHouseInfo2.setText(houseInfo2Buffer.toString());

        if (!TextUtils.isEmpty(houseDetail.getBed()) && !houseDetail.getBed().equals("0")) {
            createNewFlexItemView(0);
        }
        if (!TextUtils.isEmpty(houseDetail.getWardrobe()) && !houseDetail.getWardrobe().equals("0")) {
            createNewFlexItemView(1);
        }
        if (!TextUtils.isEmpty(houseDetail.getDesk()) && !houseDetail.getDesk().equals("0")) {
            createNewFlexItemView(2);
        }
        if (!TextUtils.isEmpty(houseDetail.getWifi()) && !houseDetail.getWifi().equals("0")) {
            createNewFlexItemView(3);
        }
        if (!TextUtils.isEmpty(houseDetail.getAirConditioner()) && !houseDetail.getAirConditioner().equals("0")) {
            createNewFlexItemView(4);
        }
        if (!TextUtils.isEmpty(houseDetail.getWashingMachine()) && !houseDetail.getWashingMachine().equals("0")) {
            createNewFlexItemView(5);
        }
        if (!TextUtils.isEmpty(houseDetail.getRefrigerator()) && !houseDetail.getRefrigerator().equals("0")) {
            createNewFlexItemView(6);
        }
        if (!TextUtils.isEmpty(houseDetail.getHeater()) && !houseDetail.getHeater().equals("0")) {
            createNewFlexItemView(7);
        }
        if (!TextUtils.isEmpty(houseDetail.getTelevision()) && !houseDetail.getTelevision().equals("0")) {
            createNewFlexItemView(8);
        }
        if (!TextUtils.isEmpty(houseDetail.getMicrowaveOven()) && !houseDetail.getMicrowaveOven().equals("0")) {
            createNewFlexItemView(9);
        }
        if (!TextUtils.isEmpty(houseDetail.getGasStove()) && !houseDetail.getGasStove().equals("0")) {
            createNewFlexItemView(10);
        }
        if (!TextUtils.isEmpty(houseDetail.getLampblackMachine()) && !houseDetail.getLampblackMachine().equals("0")) {
            createNewFlexItemView(11);
        }
        if (!TextUtils.isEmpty(houseDetail.getSofa()) && !houseDetail.getSofa().equals("0")) {
            createNewFlexItemView(12);
        }
        if (!TextUtils.isEmpty(houseDetail.getTeaTable()) && !houseDetail.getTeaTable().equals("0")) {
            createNewFlexItemView(13);
        }
        if (!TextUtils.isEmpty(houseDetail.getTolletRoom()) && !houseDetail.getTolletRoom().equals("0")) {
            createNewFlexItemView(14);
        }
        if (!TextUtils.isEmpty(houseDetail.getBalcony()) && !houseDetail.getBalcony().equals("0")) {
            createNewFlexItemView(15);
        }
        if (gridLayoutIndex < 5) {
            for (int i = gridLayoutIndex; i < 5; i++) {
                createNewFlexItemView(-1);
            }
        }
        tvHouseDesc.setText(houseDetail.getHouseDescription());
        tvHouseDrequirement.setText(houseDetail.getRentingRequirements());
    }


    /**
     * 动态创建TextView
     *
     * @return
     */

    int drawableMinimumWidth = 0;
    int drawableMinimumHeight = 0;

    private void createNewFlexItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_house_facility, null);
        if (index!=-1) {
            if (drawableMinimumWidth == 0) {
                Drawable drawable = getResources().getDrawable(App.facilitiesResIds[0]);
                drawableMinimumWidth = drawable.getMinimumWidth();
                drawableMinimumHeight = drawable.getMinimumHeight();
            }
            TextView textView = view.findViewById(R.id.tv_item);
            textView.setText(App.facilityArray[index]);
            Drawable drawable = getResources().getDrawable(App.facilitiesResIds[index]);
            drawable.setBounds(0, 0, drawableMinimumWidth, drawableMinimumHeight);// 设置边界
            textView.setCompoundDrawables(null, drawable, null, null);
        }
        GridLayout.Spec rowSpec = GridLayout.spec(gridLayoutIndex / 5, 1f);
        GridLayout.Spec columnSpec = GridLayout.spec(gridLayoutIndex % 5, 1f);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        int margin = DisplayUtils.dip2px(this, 2);
        if (index / 5 == 1 || index / 5 == 2) {
            layoutParams.topMargin = margin;
            layoutParams.bottomMargin = margin;
        }
        layoutFlex.addView(view, layoutParams);
        gridLayoutIndex++;
    }

    @OnClick({R.id.tv_add_order, R.id.tv_contact, R.id.tv_call})
    public void onClickView(View view) {
        if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_LOGIN, false)) {
            DialogUtils.showLoginDialog(this, LoginActivity.class);
            return;
        }
        if (houseDetail == null) {
            ToastUtils.showLongToast("未查询到房屋详情");
            return;
        }
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tv_add_order:
                Intent intent = new Intent(HouseDetailActivity.this, AddOrUpdateOrderActivity.class);
                intent.putExtra("data", houseDetail);
                startActivity(intent);
                break;
            case R.id.tv_contact:
                Intent imIntent = new Intent(HouseDetailActivity.this, IMChatActivity.class);
                imIntent.putExtra("targetId", houseDetail.getUserIm());
                startActivity(imIntent);
                EventManger.getDefault().postMessageRefreshEvent();
                break;
            case R.id.tv_call:
//                if (!TextUtils.isEmpty(houseDetail.getUserPhone())) {
//                    DialogUtils.showCallDialog(HouseDetailActivity.this, houseDetail.getUserIm(),houseDetail.getId(), houseDetail.getUserPhone());
                getHousePhone();
//                } else {
//                    ToastUtils.showLongToast("未查询到联系号码");
//                }
                break;
        }

    }

    private void getHousePhone() {
        showProgress();
        API.queryHousePhone(id, new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                if (isDestory()) {
                    return;
                }
                DialogUtils.showCallDialog(HouseDetailActivity.this, houseDetail.getId(), data);
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
