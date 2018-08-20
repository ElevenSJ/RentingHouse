package com.sj.rentinghouse.activity;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.events.LocationEvent;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.StatusBarUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.LocatedCity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/7.
 */

public class RegisterActivity extends AppBaseActivity {
    @BindView(R.id.et_ensure_password)
    EditText etEnsurePassword;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_getcode)
    TextView tvGetcode;


    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvGetcode.setEnabled(false);
            tvGetcode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tvGetcode.setEnabled(true);
            tvGetcode.setText("获取验证码");
        }

    };

    @Override
    public int getContentView() {
        return R.layout.activity_register;
    }

    @Override
    public void initEvent() {
        super.initEvent();
//        App.getApp().getLocationService().startDefault();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void receiveLocation(LocationEvent locationEvent) {
//        if (locationEvent.getLocation() != null) {
//            final String localCityName = locationEvent.getLocation().getCity();
//            final String localCityCode = locationEvent.getLocation().getCityCode();
//            SPUtils.getInstance().apply(new String[]{NameSpace.LOCAL_CITY_NAME}, new Object[]{localCityName});
//            Logger.d("定位城市：" + localCityName + "城市编码：" + localCityCode);
//            Logger.d("城市编码开始转换");
//            if (App.allCities != null) {
//                for (City city : App.allCities) {
//                    if (localCityName.contains(city.getName())) {
//                        Logger.d("检索到对应城市");
//                        SPUtils.getInstance().apply(new String[]{NameSpace.CITY_NAME, NameSpace.CITY_CODE, NameSpace.LOCAL_CITY_NAME, NameSpace.LOCAL_CITY_CODE}, new Object[]{city.getName(),city.getCode(), city.getName(), city.getCode()});
//                        break;
//                    }
//                }
//            }
//            Logger.d("城市编码转换完毕");
//        }
//    }
    @OnClick({R.id.tv_getcode, R.id.tv_to_login, R.id.bt_register, R.id.img_new_pwd_status, R.id.img_ensure_new_pwd_status})
    public void onClickView(View view) {
        switch (view.getId()) {
            case R.id.tv_getcode:
                if (checkPhone()) {
                    refreshCodeTxt(true);
                    doGetSMSCode(etPhone.getText().toString());
                } else {
                    ToastUtils.showShortToast("请输入手机号码");
                }
                break;
            case R.id.bt_register:
                if (checkAllEt()) {
                    if (etPassword.getText().toString().equals(etEnsurePassword.getText().toString())) {
                        doRegister(etPhone.getText().toString(), etCode.getText().toString(), etPassword.getText().toString());
                    }else{
                        ToastUtils.showShortToast("密码不一致，重新输入");
                    }
                } else {
                    ToastUtils.showShortToast("请输入所有项");
                }
                break;
            case R.id.tv_to_login:
                finish();
                break;
            case R.id.img_new_pwd_status:
                view.setSelected(!view.isSelected());
                etPassword.setTransformationMethod(view.isSelected() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                break;
            case R.id.img_ensure_new_pwd_status:
                view.setSelected(!view.isSelected());
                etEnsurePassword.setTransformationMethod(view.isSelected() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                break;
        }
    }

    private void doRegister(String s, String s1, String s2) {
        showProgress();
        API.doRegister(s,s1,s2, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                if (isDestory()){
                    return;
                }
                ToastUtils.showShortToast(message);
                finish();
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

    private boolean checkAllEt() {
        boolean isReady = true;
        int[] resIds = {R.id.et_phone,R.id.et_code,R.id.et_password,R.id.et_ensure_password};
        for (int id :resIds){
            View view  = findViewById(id);
            if (view instanceof EditText){
                if(TextUtils.isEmpty(((EditText) view).getText().toString())){
                    isReady = false;
                    break;
                }
            }
        }
        return isReady;
    }

    private void doGetSMSCode(String num) {
        API.getSMSCode(num,"1", new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                ToastUtils.showShortToast(message);
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                refreshCodeTxt(false);
            }
        });
    }

    private boolean checkPhone() {
        return !TextUtils.isEmpty(etPhone.getText().toString());
    }

    public void refreshCodeTxt(boolean refresh) {
        if (refresh) {
            timer.start();
        } else {
            timer.cancel();
            timer.onFinish();
        }
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

}
