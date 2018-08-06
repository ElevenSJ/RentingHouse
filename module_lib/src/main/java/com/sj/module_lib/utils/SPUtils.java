package com.sj.module_lib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间: on 2018/4/1.
 * 创建人: 孙杰
 * 功能描述:sharepreferences 工具类
 */
public class SPUtils {

    private static volatile SPUtils spUtils;

    private final String FILE_NAME = "ticket_data";
    /*
     * 保存手机里面的名字
     */
    private SharedPreferences mSharedPreferences;

    private SPUtils() {
        mSharedPreferences = Utils.getContext().getApplicationContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance() {
        if (spUtils == null) {
            synchronized (SPUtils.class) {
                if (spUtils == null) {
                    spUtils = new SPUtils();
                }
            }
        }
        return spUtils;
    }

    /**
     * 存储同步commit
     */
    public void commit(String key, Object object) {
        if (mSharedPreferences == null){
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 存储异步commit
     */
    public void apply(String key, Object object) {
        if (mSharedPreferences == null){
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.apply();
    }

    /**
     * 存储同步commit
     */
    public void commit(String[] keys, Object[] objects) {
        if (keys.length != objects.length) {
            Logger.e("key - value not map  each other");
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < keys.length; i++) {
            if (objects[i] instanceof String) {
                editor.putString(keys[i], (String) objects[i]);
            } else if (objects[i] instanceof Integer) {
                editor.putInt(keys[i], (Integer) objects[i]);
            } else if (objects[i] instanceof Boolean) {
                editor.putBoolean(keys[i], (Boolean) objects[i]);
            } else if (objects[i] instanceof Float) {
                editor.putFloat(keys[i], (Float) objects[i]);
            } else if (objects[i] instanceof Long) {
                editor.putLong(keys[i], (Long) objects[i]);
            } else {
                editor.putString(keys[i], objects[i].toString());
            }
        }
        editor.commit();
    }

    /**
     * 存储异步apply
     */
    public void apply(String[] keys, Object[] objects) {
        if (keys.length != objects.length) {
            Logger.e("key - value not map  each other");
            return;
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        for (int i = 0; i < keys.length; i++) {
            if (objects[i] instanceof String) {
                editor.putString(keys[i], (String) objects[i]);
            } else if (objects[i] instanceof Integer) {
                editor.putInt(keys[i], (Integer) objects[i]);
            } else if (objects[i] instanceof Boolean) {
                editor.putBoolean(keys[i], (Boolean) objects[i]);
            } else if (objects[i] instanceof Float) {
                editor.putFloat(keys[i], (Float) objects[i]);
            } else if (objects[i] instanceof Long) {
                editor.putLong(keys[i], (Long) objects[i]);
            } else {
                editor.putString(keys[i], objects[i].toString());
            }
        }
        editor.apply();
    }

    /**
     * 获取保存的数据
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (mSharedPreferences == null){
            return null;
        }
        if (defaultObject instanceof String) {
            return mSharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return mSharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return mSharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return mSharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return mSharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return mSharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove( String key) {
        mSharedPreferences.edit().remove(key);
        mSharedPreferences.edit().commit();
    }
    /**
     * 移除某个key值已经对应的值
     */
    public void remove( String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            mSharedPreferences.edit().remove(keys[i]);
        }
        mSharedPreferences.edit().commit();
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void clear() {
        mSharedPreferences.edit().clear();
        mSharedPreferences.edit().commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String FILE_NAME, String key) {
        return mSharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll(String FILE_NAME) {
        return mSharedPreferences.getAll();
    }
}
