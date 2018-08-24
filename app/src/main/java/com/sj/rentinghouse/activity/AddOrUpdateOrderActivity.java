package com.sj.rentinghouse.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.itheima.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.glide.ImageUtils;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.utils.DateUtils;
import com.sj.module_lib.utils.SoftKeyboardUtil;
import com.sj.module_lib.utils.TimePickerDialog;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.HouseDetail;
import com.sj.rentinghouse.bean.OrderInfo;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/18.
 */

public class AddOrUpdateOrderActivity extends AppBaseActivity
//        implements TimePickerDialog.TimePickerDialogInterface
{
    @BindView(R.id.img_house_icon)
    RoundedImageView imgHouseIcon;
    @BindView(R.id.tv_other_time)
    TextView tvOtherTime;
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus;
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
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_img_count)
    TextView tvImgCount;
    @BindView(R.id.edt_name_value)
    EditText edtNameValue;
    @BindView(R.id.edt_time_value)
    EditText edtTimeValue;
    @BindView(R.id.edt_remark_value)
    EditText edtRemarkValue;
    HouseDetail houseDetail;
    OrderInfo orderInfo;

    @BindArray(R.array.user_sex_type)
    String[] sexType;
    @BindView(R.id.spinner)
    Spinner spinner;
//    private TimePickerDialog mTimePickerDialog;
    private TimePickerView timePickerView;

    @Override
    public int getContentView() {
        return R.layout.activity_add_order;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Object dataObject = getIntent().getParcelableExtra("data");
        if (dataObject instanceof HouseDetail) {
            houseDetail = (HouseDetail) dataObject;
        } else if (dataObject instanceof OrderInfo) {
            orderInfo = (OrderInfo) dataObject;
        }
//        mTimePickerDialog = new TimePickerDialog(AddOrUpdateOrderActivity.this);
    }

    private void showTimePickerView() {
        if (timePickerView == null){
            ArrayList<Integer> minList= new ArrayList<>();
            minList.add(0);
            minList.add(30);
            timePickerView = new TimePickerView(this, TimePickerView.Type.MONTH_DAY_HOUR_MIN,minList);
            timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
//                    ToastUtils.showShortToast(DateUtils.getMinTime(date));
                    long betweenTime = DateUtils.nowCurrentTime(date.getTime());
                    if (betweenTime<0) {
                        ToastUtils.showShortToast("请选择未来5天内的预约时间");
                    }else if (betweenTime>5*24*60*60){
                        ToastUtils.showShortToast("预约时间请勿超过5天");
                    }else{
                        edtTimeValue.setText(DateUtils.getMinTime(date));
                    }
                }
            });
            timePickerView.setRange(DateUtils.getCurrentYear(), DateUtils.getCurrentYear());
        }
        timePickerView.setCustomTime(new Date(),1);
        timePickerView.show();
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "约看");

        if (houseDetail != null) {
            String[] housePictures = TextUtils.isEmpty(houseDetail.getHousePicture()) || houseDetail.getHousePicture().equalsIgnoreCase("null") ? null : houseDetail.getHousePicture().split(",");
            if (houseDetail != null && housePictures.length > 0) {
                ImageUtils.loadImageView(housePictures[0], imgHouseIcon);
                tvImgCount.setText(housePictures.length + "图");
                tvImgCount.setVisibility(View.VISIBLE);
            } else {
                imgHouseIcon.setImageResource(R.mipmap.ic_launcher);
                tvImgCount.setVisibility(View.GONE);
            }
            tvHourseName.setText(houseDetail.getVillage() + (TextUtils.isEmpty(houseDetail.getBedroom()) ? "" : " · " + houseDetail.getBedroom() + "居室"));
            if (!TextUtils.isEmpty(houseDetail.getType())) {
                try {
                    tvType.setText(App.typeArray[Integer.valueOf(houseDetail.getType()) - 1]);
                    Drawable drawable = null;
                    if (houseDetail.getType().equals("1")) {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_whole_rent);
                    } else {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_joint_rent);
                    }
                    drawable.setBounds(0, 0, 50, 50);// 设置边界
                    tvType.setCompoundDrawables(drawable, null, null, null);
                } catch (NumberFormatException e) {
                    Logger.e("房源类型转型异常");
                }
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
            tvPrice.setText("¥ " + houseDetail.getRent() + " 元/月");
        } else if (orderInfo != null) {
            String[] housePictures = TextUtils.isEmpty(orderInfo.getHousePicture()) || orderInfo.getHousePicture().equalsIgnoreCase("null") ? null : orderInfo.getHousePicture().split(",");
            if (housePictures != null && housePictures.length > 0) {
                ImageUtils.loadImageView(housePictures[0], imgHouseIcon);
                tvImgCount.setVisibility(View.VISIBLE);
                tvImgCount.setText(housePictures.length + "图");
            } else {
                imgHouseIcon.setImageResource(R.mipmap.ic_launcher);
                tvImgCount.setVisibility(View.GONE);
            }
            tvHourseName.setText(orderInfo.getVillage());
            if (!TextUtils.isEmpty(orderInfo.getType())) {
                try {
                    tvType.setText(App.typeArray[Integer.valueOf(orderInfo.getType()) - 1]);
                    Drawable drawable = null;
                    if (orderInfo.getType().equals("1")) {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_whole_rent);
                    } else {
                        drawable = tvType.getContext().getResources().getDrawable(R.drawable.img_joint_rent);
                    }
                    drawable.setBounds(0, 0, 50, 50);// 设置边界
                    tvType.setCompoundDrawables(drawable, null, null, null);
                } catch (NumberFormatException e) {
                    Logger.e("房源类型转型异常");
                }
            }
            if (!orderInfo.getType().equals("1")) {
                tvHourseRoom.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(orderInfo.getHouseType())) {
                    try {
                        tvHourseRoom.setText(App.roomTypeArray[Integer.valueOf(orderInfo.getHouseType()) - 1]);
                    } catch (NumberFormatException e) {
                        tvHourseRoom.setText(orderInfo.getHouseType());
                        Logger.e("房源合租类型数转型异常");
                    }
                }
            } else {
                tvHourseRoom.setVisibility(View.GONE);
            }
            tvRoomArea.setText(orderInfo.getArea() + "㎡");
            try {
                tvRoomDirection.setText(App.directionArray[Integer.valueOf(orderInfo.getDirection()) - 1]);
            } catch (NumberFormatException e) {
                tvRoomDirection.setText(orderInfo.getDirection());
                Logger.e("房源朝向转型异常");
            }
            tvPrice.setText("¥ " + orderInfo.getRent()+" 元/月");

        }
    }

    @OnClick({R.id.bt_sure, R.id.edt_time_value})
    public void onClickView(View view) {
        if (houseDetail == null) {
            ToastUtils.showLongToast("未查询到房屋详情");
            return;
        }
        int viewId = view.getId();
        switch (viewId) {
            case R.id.edt_time_value:
//                mTimePickerDialog.showDateAndTimePickerDialog();
                SoftKeyboardUtil.hideSoftKeyboard(this);
                showTimePickerView();
                break;
            case R.id.bt_sure:
                SoftKeyboardUtil.hideSoftKeyboard(this);
                if (TextUtils.isEmpty(edtNameValue.getText().toString())) {
                    ToastUtils.showShortToast("请输入您的姓名");
                    return;
                }
                if (TextUtils.isEmpty(edtTimeValue.getText().toString())) {
                    ToastUtils.showShortToast("请选择预约时间");
                    return;
                }
                showProgress();
                API.addOrder(houseDetail.getId(), edtNameValue.getText().toString() + sexType[spinner.getSelectedItemPosition()], edtTimeValue.getText().toString(), houseDetail.getUserId(), edtRemarkValue.getText().toString(), new CommonCallback() {
                    @Override
                    public void onSuccess(String message) {
                        if (isDestory()) {
                            return;
                        }
                        ToastUtils.showShortToast("预约完成请等待房东确认");
                        EventManger.getDefault().postTrackRefreshEvent();
                        EventManger.getDefault().postMessageRefreshEvent();
                        finish();
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
                break;
        }

    }

//    @Override
//    public void positiveListener() {
//        int year = mTimePickerDialog.getYear();
//        int month = mTimePickerDialog.getMonth();
//        int day = mTimePickerDialog.getDay();
//        int hour = mTimePickerDialog.getHour();
//        int minute = mTimePickerDialog.getMinute();
//        long betweenTime = DateUtils.nowCurrentTime(DateUtils.getStringToDate(year + "-" + month + "-" + day+" "+hour+":"+minute));
//        if (betweenTime<0) {
//            ToastUtils.showShortToast("请选择未来5天内的预约时间");
//        }else if (betweenTime>5*24*60*60){
//            ToastUtils.showShortToast("预约时间请勿超过5天");
//        }else{
//            edtTimeValue.setText(DateUtils.timeStampToStr1(DateUtils.getStringToDate(year + "-" + month + "-" + day + " " + hour + ":" + minute)));
//        }
//    }

//    @Override
//    public void negativeListener() {
//
//    }
}
