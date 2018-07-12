package com.sj.module_lib.http;

import android.widget.Toast;

import com.jady.retrofitclient.HttpManager;
import com.jady.retrofitclient.callback.HttpCallback;

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
                this.onSuccess(result);
                onFinish();
            } else {
                onFailed(callbackData.getCode(), callbackData.getMessage());
            }
        } else {
            onSuccess((V) t);
            onFinish();
        }
    }

    @Override
    public void onFailed(String error_code, String error_message) {
        onFailure(error_code,error_message);
    }

    public abstract void onSuccess(V data);

    public boolean enableShowToast() {
        return true;
    }

    public void onFailure(String error_code, String error_message){
        if (enableShowToast()) {
            Toast.makeText(HttpManager.mContext, error_message, Toast.LENGTH_SHORT).show();
        }
        onFinish();
    }

    public void onFinish() {
    }
}
