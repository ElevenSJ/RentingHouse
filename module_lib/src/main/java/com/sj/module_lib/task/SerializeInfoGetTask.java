package com.sj.module_lib.task;

import android.os.AsyncTask;

import com.orhanobut.logger.Logger;
import com.sj.module_lib.utils.Utils;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

/**
 * 创建时间: on 2018/4/3.
 * 创建人: 孙杰
 * 功能描述:序列化用户信息到本地
 */
public class SerializeInfoGetTask extends AsyncTask<Object, Integer, Object> {

    @Override
    protected Object doInBackground(Object... params) {
        //读取本地序列化
        try {
            FileInputStream inputStream = Utils.getContext().openFileInput(params[1].toString());
            ObjectInputStream in = new ObjectInputStream(inputStream);
            params[0] = in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
            return null;
        }
        return params[0];
    }
    @Override
    protected void onPostExecute(Object object) {
        super.onPostExecute(object);
        Logger.d("SerializeInfoSaveTask："+object!=null);
    }
}
