package com.sj.rentinghouse.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.sj.im.SharePreferenceManager;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.DeviceUtils;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * 创建时间: on 2018/3/29.
 * 创建人: 孙杰
 * 功能描述:登录页
 */
public class LoginActivity extends AppBaseActivity {
    @BindView(R.id.edt_phone_value)
    EditText edtPhoneValue;
    @BindView(R.id.edt_code_value)
    EditText edtCodeValue;
    @BindView(R.id.tv_getcode)
    TextView tvGetcode;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_register_detail)
    TextView tvRegisterDetail;

    int type = 1;//1账号密码登录，2 验证码登录

    int showPwdStatus = 1;//1隐藏，2 显示
    @BindView(R.id.img_pwd_status)
    ImageView imgPwdStatus;


    EditText edtFgtPhoneValue;
    EditText edtFgtCodeValue;
    EditText edtFgtNewPwdValue;
    EditText edtFgtNewPwdEnsureValue;
    TextView tvGetFgtCode;
    String getCodeType = "";

    @Override
    public int getContentView() {
        return R.layout.activity_login;
    }


    @Override
    public void initEvent() {
        super.initEvent();
        final View myLayout = getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int statusBarHeight;
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                // 使用最外层布局填充，进行测算计算
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = myLayout.getRootView().getHeight();
                int heightDiff = screenHeight - (r.bottom - r.top);
                if (heightDiff > 100) {
                    // 如果超过100个像素，它可能是一个键盘。获取状态栏的高度
                    statusBarHeight = 0;
                }
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object obj = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    int x = Integer.parseInt(field.get(obj).toString());
                    statusBarHeight = LoginActivity.this.getApplicationContext().getResources().getDimensionPixelSize(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int realKeyboardHeight = heightDiff - statusBarHeight;
                Logger.i("软键盘高度(单位像素) ="+realKeyboardHeight);
                if (realKeyboardHeight>0){
                    SharePreferenceManager.setCachedKeyboardHeight(realKeyboardHeight);
                }
            }
        });
    }

    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(getCodeType.equals("2")) {
                tvGetcode.setEnabled(false);
                tvGetcode.setText(millisUntilFinished / 1000 + "秒");
            }else{
                tvGetFgtCode.setEnabled(false);
                tvGetFgtCode.setText(millisUntilFinished / 1000 + "秒");
            }
        }

        @Override
        public void onFinish() {
            if(getCodeType.equals("2")) {
                tvGetcode.setEnabled(true);
                tvGetcode.setText("获取验证码");
            }else{
                tvGetFgtCode.setEnabled(true);
                tvGetFgtCode.setText("获取验证码");
            }
        }
    };

    @OnClick({R.id.tv_forget_pwd, R.id.tv_getcode, R.id.bt_login, R.id.tv_register_detail, R.id.tv_change_login_type, R.id.img_pwd_status})
    public void onClick(View view) {
        Logger.i("onClick(View view):" + view.getId());
        switch (view.getId()) {
            case R.id.tv_forget_pwd:
                DialogUtils.showViewDialog(LoginActivity.this, R.layout.dialog_forget_pwd, new DialogUtils.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        edtFgtPhoneValue = view.findViewById(R.id.edt_fgt_phone_value);
                        edtFgtCodeValue = view.findViewById(R.id.et_fgt_code_value);
                        edtFgtNewPwdValue = view.findViewById(R.id.et_fgt_new_password);
                        edtFgtNewPwdEnsureValue = view.findViewById(R.id.et_fgt_ensure_new_password);
                        tvGetFgtCode = view.findViewById(R.id.tv_fgt_getcode);
                        tvGetFgtCode.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCode(edtFgtPhoneValue.getText().toString().trim(), "3");
                            }
                        });
                    }
                }, new DialogUtils.OnSureListener() {
                    @Override
                    public void callBack(final DialogInterface dialog, int which) {
                        DialogUtils.setMShowing(dialog,false);
                        if (checkPhoneAllEt()) {
                            final String newPhone = edtFgtPhoneValue.getText().toString().trim();
                            final String msgCode = edtFgtCodeValue.getText().toString().trim();
                            final String newPwd = edtFgtNewPwdValue.getText().toString().trim();
                            final String ensureNewPwd = edtFgtNewPwdEnsureValue.getText().toString().trim();
                            showProgress();
                            API.forgetPwd(newPhone,msgCode,newPwd,ensureNewPwd, new CommonCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    if (isDestory()){
                                        return;
                                    }
                                    DialogUtils.setMShowing(dialog,true);
                                    dialog.dismiss();
                                    ToastUtils.showShortToast("密码重置成功");
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
                    }
                });
                break;
            case R.id.tv_getcode:
                getCode(edtPhoneValue.getText().toString().trim(), "2");
                break;
            case R.id.bt_login:
                doLogin(edtPhoneValue.getText().toString().trim(), edtCodeValue.getText().toString().trim(), DeviceUtils.getUniqueId(this));
                break;
            case R.id.tv_register_detail:
                toRegister();
                break;
            case R.id.tv_change_login_type:
                toChangeLoginType();
                break;
            case R.id.img_pwd_status:
                if (showPwdStatus == 1) {
                    view.setSelected(true);
                    showPwdStatus = 2;
                    //如果选中，显示密码
                    edtCodeValue.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    showPwdStatus = 1;
                    view.setSelected(false);
                    //否则隐藏密码
                    edtCodeValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
            default:
        }
    }

    private boolean checkPhoneAllEt() {
        if (TextUtils.isEmpty(edtFgtPhoneValue.getText().toString())) {
            ToastUtils.showShortToast("请输入手机号码");
            return false;
        } else if (TextUtils.isEmpty(edtFgtCodeValue.getText().toString())) {
            ToastUtils.showShortToast("请输入验证码");
            return false;
        } else if (TextUtils.isEmpty(edtFgtNewPwdValue.getText().toString())) {
            ToastUtils.showShortToast("请输入新密码");
            return false;
        } else if (TextUtils.isEmpty(edtFgtNewPwdEnsureValue.getText().toString())) {
            ToastUtils.showShortToast("请确认新密码");
            return false;
        } else if (!TextUtils.equals(edtFgtNewPwdValue.getText().toString(), edtFgtNewPwdEnsureValue.getText().toString())) {
            ToastUtils.showShortToast("密码不一致");
            return false;
        }
        return true;
    }

    private void toChangeLoginType() {
        if (type == 1) {
            type = 2;
            edtPhoneValue.setHint("请输入手机号码");
            edtCodeValue.setHint("请输入验证码");
            edtCodeValue.setText(null);
            tvGetcode.setVisibility(View.VISIBLE);
//            imgPwdStatus.setVisibility(View.GONE);
        } else {
            type = 1;
            edtPhoneValue.setHint("账号");
            edtCodeValue.setHint("密码");
            edtCodeValue.setText(null);
            tvGetcode.setVisibility(View.GONE);
//            imgPwdStatus.setVisibility(View.VISIBLE);
        }
    }

    private void toRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getBooleanExtra("isLogined", false)) {
            toMainActivity();
        }
    }

    public void toMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
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

    public void getCode(String phoneNum, String type) {
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() != 11) {
            ToastUtils.showShortToast("手机号码不正确");
            return;
        }
        getCodeType = type;
        refreshCodeTxt(true);
        API.getSMSCode(phoneNum, getCodeType, new CommonCallback() {
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

    public void doLogin(final String phoneNum, String codeNum, String deviceId) {
        showProgress();
        Logger.d("phoneNum:" + phoneNum + ",codeNum:" + codeNum + ",deviceId:" + deviceId);
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() != 11) {
            ToastUtils.showShortToast("手机号码不正确");
            dismissProgress();
            return;
        }
        if (TextUtils.isEmpty(codeNum)) {
            ToastUtils.showShortToast("请输入验证码");
            dismissProgress();
            return;
        }
        if (type == 1) {
            API.doLoginPwd(phoneNum, codeNum, deviceId, new ServerResultBack<BaseResponse<String>, String>() {
                @Override
                public void onSuccess(BaseResponse<String> data) {
                    super.onSuccess(data);
                    if (isDestory()){
                        return;
                    }
                    doLoginSuccess(phoneNum, data.getData(),data.getMessage());
                }
                @Override
                public void onSuccess(String data) {

                }

                @Override
                public void onFailed(String error_code, String error_message) {
                    super.onFailed(error_code, error_message);
                    if (isDestory()){
                        return;
                    }
                    dismissProgress();
                }
            });
        } else {
            API.doLoginCode(phoneNum, codeNum, deviceId, new ServerResultBack<BaseResponse<String>, String>() {
                @Override
                public void onSuccess(BaseResponse<String> data) {
                    super.onSuccess(data);
                    if (isDestory()){
                        return;
                    }
                    doLoginSuccess(phoneNum, data.getData(),data.getMessage());
                }

                @Override
                public void onFailed(String error_code, String error_message) {
                    super.onFailed(error_code, error_message);
                    if (isDestory()){
                        return;
                    }
                    dismissProgress();
                }
                @Override
                public void onSuccess(String data) {

                }
            });
        }
    }

    private void doLoginSuccess(String phoneNum, String data,String im) {
        SPUtils.getInstance().apply(new String[]{NameSpace.IS_LOGIN, NameSpace.USER_ACCOUNT, NameSpace.TOKEN_ID}, new Object[]{true, phoneNum, data});
        EventManger.getDefault().postLoginEvent(true);
        JMessageClient.login(im, im, new BasicCallback() {
            @Override
            public void gotResult(final int status, final String desc) {
                if (status == 0) {
                    String username = JMessageClient.getMyInfo().getUserName();
                    String appKey = JMessageClient.getMyInfo().getAppKey();
                    SharePreferenceManager.setCachedUsername(username);
                    SharePreferenceManager.setAppKey(appKey);
                    Logger.e("极光IM登录成功 status= " + status);
                } else {
                    Logger.e("极光IM登录失败 status= " + status);
                }
                EventManger.getDefault().postIMLoginEvent(status == 0);
                dismissProgress();
                toMainActivity();
            }
        });
    }

}
