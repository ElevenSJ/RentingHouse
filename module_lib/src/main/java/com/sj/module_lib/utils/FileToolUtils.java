package com.sj.module_lib.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * 创建时间: on 2018/4/4.
 * 创建人: 孙杰
 * 功能描述:
 */

public class FileToolUtils {

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath() {
        File path = null;
        if (sdCardIsAvailable()) {
            path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            path = Environment.getDataDirectory();
        }
        return path;
    }

    /**
     * 获取的目录默认没有最后的”/”,需要自己加上
     * 获取本应用图片缓存目录
     *
     * @return
     */
    public static File getCecheFolder(Context context) {
        File folder = new File(context.getCacheDir(), "IMAGECACHE");
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else {
            return false;
        }
    }

    /**
     * 文件或者文件夹是否存在.
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
