package com.sj.module_lib.http;

import android.widget.Toast;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.HttpCallback;
import com.sj.module_lib.utils.ToastUtils;

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
        if(error_message.contains("BEGIN_OBJECT")){
            return;
        }
        if (enableShowToast()) {
            ToastUtils.showShortToast(error_message);
        }
    }

    public abstract void onSuccess(V data);

    public void onSuccess(BaseResponse<V> data){

    }

    public boolean enableShowToast() {
        return true;
    }

    public void onFinish() {
    }
}
