package com.sj.rentinghouse.activity;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.utils.StatusBarUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.http.API;

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


    @OnClick({R.id.tv_getcode, R.id.tv_to_login, R.id.bt_register})
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
                        ToastUtils.showShortToast("密码不一致");
                    }
                } else {
                    ToastUtils.showShortToast("请输入所有项");
                }
                break;
            case R.id.tv_to_login:
                finish();
                break;
        }
    }

    private void doRegister(String s, String s1, String s2) {
        showProgress();
        API.doRegister(s,s1,s2, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                ToastUtils.showShortToast(message);
                finish();
            }

            @Override
            public void onFinish() {
                super.onFinish();
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
        API.getSMSCode(num, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                ToastUtils.showShortToast(message);
            }

            @Override
            public void onFailure(String error_code, String error_message) {
                super.onFailure(error_code, error_message);
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
        super.onDestroy();
        timer.cancel();
    }

}
