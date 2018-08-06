package com.sj.module_lib.task;

import android.content.Context;
import android.os.AsyncTask;

import com.orhanobut.logger.Logger;
import com.sj.module_lib.utils.Utils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * 创建时间: on 2018/4/3.
 * 创建人: 孙杰
 * 功能描述:序列化客服信息到本地
 */
public class SerializeInfoSaveTask extends AsyncTask<Object, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(Object... params) {
        //序列化到本地
        try {
            FileOutputStream outStream = Utils.getContext().openFileOutput(params[1].toString(),
                    Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            out.writeObject(params[0]);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Logger.d("SerializeInfoSaveTask："+aBoolean);
    }
}
