package com.sj.module_lib.http;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.HttpCallback;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.utils.Utils;

/**
 * Created by Sunj on 2018/7/7.
 */
public abstract class ServerResultBack<T, V> extends HttpCallback<T> {
    @Override
    public void onResolve(T t) {
        if (t instanceof BaseResponse) {
            BaseResponse<V> callbackData = (BaseResponse) t;
            V result = callbackData.getData();
            if (callbackData.getCode().equals("1")) {
                onFinish();
                this.onSuccess(result);
                this.onSuccess(callbackData);
            } else {
                onFailed(callbackData.getCode(), callbackData.getMessage());
            }
        } else {
            onFinish();
            onSuccess((V) t);
        }
    }

    @Override
    public void onFailed(String error_code, String error_message) {
        onFinish();
        if (!TextUtils.isEmpty(error_message) && error_message.contains("BEGIN_OBJECT")) {
            if (enableShowToast()) {
                ToastUtils.showShortToast("数据为空");
            }
            return;
        }
        if (enableShowToast()) {
            ToastUtils.showShortToast(error_message);
        }
        if (TextUtils.isEmpty(error_code)||TextUtils.isEmpty(error_message)){
            return;
        }
        //其他设备登录统一处理
        if (error_code.equals("2")&&error_message.contains("token失效")) {
            if (Utils.getMainActivity() != null) {
                Intent intent = new Intent(Utils.getContext(), Utils.getMainActivity());
                intent.putExtra("LoginOut", true);
                Utils.getContext().startActivity(intent);
            }
        }
    }

    public abstract void onSuccess(V data);

    public void onSuccess(BaseResponse<V> data) {

    }

    public boolean enableShowToast() {
        return true;
    }

    public void onFinish() {
    }
}
