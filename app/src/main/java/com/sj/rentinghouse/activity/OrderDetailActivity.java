package com.sj.rentinghouse.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.OrderDetail;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.events.OrderStatusEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/22.
 */

public class OrderDetailActivity extends AppBaseActivity {
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_phone)
    TextView tvUserPhone;
    @BindView(R.id.tv_user_time)
    TextView tvUserTime;
    @BindView(R.id.tv_note)
    EditText tvNote;
    @BindView(R.id.bt_agree)
    Button btAgree;
    @BindView(R.id.bt_refuse)
    Button btRefuse;

    String id;
    String status;
    OrderDetail orderDetail;
    @Override
    public int getContentView() {
        return R.layout.activity_order_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        id = getIntent().getStringExtra("id");
        status= getIntent().getStringExtra("status");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "约看详情");
        getOrderDetail();
    }

    private void getOrderDetail() {
        showProgress();
        API.queryOrderInfo(id, new ServerResultBack<BaseResponse<OrderDetail>,OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail data) {
                if (isDestory()){
                    return;
                }
                orderDetail = data;
                initViewData();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()){
                    return;
                }
                dismissProgress();
            }
        });
    }

    private void initViewData() {
        try {
            tvStatus.setText(App.statusArray[Integer.valueOf(status) - 1]);
        } catch (NumberFormatException e) {
            tvStatus.setText(status);
            Logger.e("房源状态转型异常");
        }
        tvUserName.setText(orderDetail.getName());
        tvUserPhone.setText(orderDetail.getPhone());
        tvUserTime.setText(orderDetail.getShowTime());
        tvNote.setText(orderDetail.getNote());
        if (!status.equals("1")){
            btAgree.setVisibility(View.GONE);
            btRefuse.setVisibility(View.GONE);
        }else{
            btAgree.setVisibility(View.VISIBLE);
            btRefuse.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.bt_agree, R.id.bt_refuse})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_agree:
                showProgress();
                API.updateOrderStatus(id, "2", new CommonCallback() {
                    @Override
                    public void onSuccess(String message) {
                        if (isDestory()){
                            return;
                        }
                        status = "2";
                        ToastUtils.showShortToast("已同意约看");
                        backFinish(true);
                    }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (isDestory()){
                            return;
                        }
                        dismissProgress();
                    }
                });
                break;
            case R.id.bt_refuse:
                showProgress();
                API.updateOrderStatus(id, "3", new CommonCallback() {
                    @Override
                    public void onSuccess(String message) {
                        status = "3";
                        ToastUtils.showShortToast("已拒绝约看");
                        backFinish(true);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissProgress();
                    }
                });
                break;
            case R.id.tv_contact:
                getOrderPhone();
                break;
        }
    }

    private void getOrderPhone() {
        showProgress();
        API.queryOrderPhone(id, new ServerResultBack<BaseResponse<String>,String>() {
            @Override
            public void onSuccess(String data) {
                if (isDestory()){
                    return;
                }
                DialogUtils.showCallDialog(OrderDetailActivity.this, data);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()){
                    return;
                }
                dismissProgress();
            }
        });
    }
    public void backFinish(boolean isRefresh){
       if (isRefresh){
           EventBus.getDefault().post(new OrderStatusEvent(id,status));
           EventManger.getDefault().postMyRefreshEvent();
       }
       finish();
    }
}
