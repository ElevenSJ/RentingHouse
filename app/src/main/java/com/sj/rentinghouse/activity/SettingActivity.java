package com.sj.rentinghouse.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.task.SerializeInfoSaveTask;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.events.LoginOutEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;
import com.zaaach.citypicker.model.City;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/20.
 */

public class SettingActivity extends AppBaseActivity {
    EditText etOldPwd;
    EditText etNewPassword;
    EditText etEnsureNewPassword;

    @Override
    public int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void init() {
        super.init();
        setTopTitle(R.id.tv_top_title, "设置");
    }

    private void onExitApp() {
        showProgress();
        API.out(new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                EventBus.getDefault().post(new LoginOutEvent(true));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()){
                    return;
                }
                dismissProgress();
                finish();
            }
        });
    }

    @OnClick({R.id.bt_exit, R.id.tv_item})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_exit:
                DialogUtils.showMessageDialog(this, "是否确定退出", "退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i!=-100){
                            onExitApp();
                        }
                    }
                });
                break;
            case R.id.tv_item:
                DialogUtils.showViewDialog(this, R.layout.dialog_modify_pwd, new DialogUtils.ViewInterface() {
                    @Override
                    public void getChildView(View view, int layoutResId) {
                        etOldPwd = view.findViewById(R.id.et_old_pwd);
                        etNewPassword = view.findViewById(R.id.et_new_password);
                        etEnsureNewPassword = view.findViewById(R.id.et_ensure_new_password);
                    }
                }, new DialogUtils.OnSureListener() {
                    @Override
                    public void callBack(final DialogInterface dialog, int which) {
                        DialogUtils.setMShowing(dialog,false);
                        if (checkAllEt()) {
                            if (etNewPassword.getText().toString().equals(etEnsureNewPassword.getText().toString())) {
                                API.changePassword(etOldPwd.getText().toString(), etNewPassword.getText().toString(), etEnsureNewPassword.getText().toString(), new CommonCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        ToastUtils.showLongToast("密码修改成功");
                                        DialogUtils.setMShowing(dialog,true);
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                ToastUtils.showShortToast("密码不一致");
                            }
                        } else {
                            ToastUtils.showShortToast("请输入所有项");
                        }
                    }
                });
                break;
        }
    }

    private boolean checkAllEt() {
        boolean isReady = true;
        if (TextUtils.isEmpty(etOldPwd.getText().toString())) {
            isReady = false;
        } else if (TextUtils.isEmpty(etNewPassword.getText().toString())) {
            isReady = false;
        } else if (TextUtils.isEmpty(etEnsureNewPassword.getText().toString())) {
            isReady = false;
        }
        return isReady;
    }

}
