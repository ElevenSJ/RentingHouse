package com.sj.rentinghouse.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.http.API;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/8/17.
 */

public class ForgetPwdActivity extends AppBaseActivity {
    @BindView(R.id.edt_fgt_phone_value)
    EditText edtFgtPhoneValue;
    @BindView(R.id.et_fgt_code_value)
    EditText etFgtCodeValue;
    @BindView(R.id.et_fgt_new_password)
    EditText etFgtNewPassword;
    @BindView(R.id.et_fgt_ensure_new_password)
    EditText etFgtEnsureNewPassword;
    @BindView(R.id.tv_fgt_getcode)
    TextView tvFgtGetcode;

    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
                tvFgtGetcode.setEnabled(false);
                tvFgtGetcode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            tvFgtGetcode.setEnabled(true);
            tvFgtGetcode.setText("获取验证码");
        }
    };

    @Override
    public int getContentView() {
        return R.layout.activity_forget_pwd;
    }

    @OnClick({R.id.tv_fgt_getcode, R.id.img_fgt_new_pwd_status, R.id.img_fgt_ensure_new_pwd_status, R.id.bt_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fgt_getcode:
                getCode(edtFgtPhoneValue.getText().toString().trim(), "3");
                break;
            case R.id.img_fgt_new_pwd_status:
                view.setSelected(!view.isSelected());
                etFgtNewPassword.setTransformationMethod(view.isSelected() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                break;
            case R.id.img_fgt_ensure_new_pwd_status:
                view.setSelected(!view.isSelected());
                etFgtEnsureNewPassword.setTransformationMethod(view.isSelected() ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
                break;
            case R.id.bt_sure:
                if (checkPhoneAllEt()) {
                    final String newPhone = edtFgtPhoneValue.getText().toString().trim();
                    final String msgCode = etFgtCodeValue.getText().toString().trim();
                    final String newPwd = etFgtNewPassword.getText().toString().trim();
                    final String ensureNewPwd = etFgtEnsureNewPassword.getText().toString().trim();
                    showProgress();
                    API.forgetPwd(newPhone,msgCode,newPwd,ensureNewPwd, new CommonCallback() {
                        @Override
                        public void onSuccess(String message) {
                            if (isDestory()){
                                return;
                            }
                            ToastUtils.showShortToast("密码重置成功");
                            finish();
                        }
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            if (isDestory()){
                                return;
                            }
                            refreshCodeTxt(false);
                            dismissProgress();
                        }
                    });
                }
                break;
        }
    }
    private boolean checkPhoneAllEt() {
        if (TextUtils.isEmpty(edtFgtPhoneValue.getText().toString())) {
            ToastUtils.showShortToast("请输入手机号码");
            return false;
        } else if (TextUtils.isEmpty(etFgtCodeValue.getText().toString())) {
            ToastUtils.showShortToast("请输入验证码");
            return false;
        } else if (TextUtils.isEmpty(etFgtNewPassword.getText().toString())) {
            ToastUtils.showShortToast("请输入新密码");
            return false;
        } else if (TextUtils.isEmpty(etFgtEnsureNewPassword.getText().toString())) {
            ToastUtils.showShortToast("请确认新密码");
            return false;
        } else if (!TextUtils.equals(etFgtNewPassword.getText().toString(), etFgtEnsureNewPassword.getText().toString())) {
            ToastUtils.showShortToast("密码不一致");
            return false;
        }
        return true;
    }
    public void getCode(String phoneNum, String type) {
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() != 11) {
            ToastUtils.showShortToast("手机号码不正确");
            return;
        }
        refreshCodeTxt(true);
        API.getSMSCode(phoneNum, type, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                ToastUtils.showShortToast(message);
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                if (isDestory()){
                    return;
                }
                refreshCodeTxt(false);
            }
        });
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

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setTopTitle(R.id.tv_top_title, "忘记密码");
    }
}
