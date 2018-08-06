package com.sj.im;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sj.rentinghouse.R;

import java.io.File;
import java.util.Map;

import cn.jiguang.imui.commons.models.IUser;
import cn.jpush.im.android.api.callback.DownloadAvatarCallback;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by caiyaoguan on 17/4/25.
 */

public class JMUserInfo extends UserInfo implements IUser {

    private UserInfo userInfo;

    public JMUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if (userInfo!=null){
            userID = userInfo.getUserID();
        }
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    @Override
    public String getId() {
        return String.valueOf(userID);
    }

    @Override
    public String getDisplayName() {
        if (userInfo.getNotename() != null && !TextUtils.isEmpty(userInfo.getNotename())) {
            Logger.i("getNotename:"+userInfo.getNotename());
            return userInfo.getNotename();
        } else if (userInfo.getNickname() != null && !TextUtils.isEmpty(userInfo.getNickname())) {
            Logger.i("getNickname:"+userInfo.getNickname());
            return userInfo.getNickname();
        } else if (userInfo.getUserName() != null && !TextUtils.isEmpty(userInfo.getUserName())) {
            Logger.i("getUserName:"+userInfo.getUserName());
            return userInfo.getUserName();
        }else{
            Logger.i("getId:"+getId());
            return getId();
        }
    }

    @Override
    public String getAvatarFilePath() {
        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
            return userInfo.getAvatarFile().getAbsolutePath();
        } else {
            return "R.drawable.jmui_head_icon";
        }
    }
    @Override
    public String getNotename() {
        return userInfo.getNotename();
    }

    @Override
    public String getNoteText() {
        return userInfo.getNoteText();
    }

    @Override
    public long getBirthday() {
        return userInfo.getBirthday();
    }

    @Override
    public File getAvatarFile() {
        return userInfo.getAvatarFile();
    }

    @Override
    public void getAvatarFileAsync(DownloadAvatarCallback downloadAvatarCallback) {
        userInfo.getAvatarFileAsync(downloadAvatarCallback);
    }

    @Override
    public void getAvatarBitmap(GetAvatarBitmapCallback getAvatarBitmapCallback) {
        userInfo.getAvatarBitmap(getAvatarBitmapCallback);
    }

    @Override
    public File getBigAvatarFile() {
        return userInfo.getBigAvatarFile();
    }

    @Override
    public void getBigAvatarBitmap(GetAvatarBitmapCallback getAvatarBitmapCallback) {
        userInfo.getBigAvatarBitmap(getAvatarBitmapCallback);
    }

    @Override
    public int getBlacklist() {
        return userInfo.getBlacklist();
    }

    @Override
    public int getNoDisturb() {
        return userInfo.getNoDisturb();
    }

    @Override
    public boolean isFriend() {
        return userInfo.isFriend();
    }

    @Override
    public String getAppKey() {
        return userInfo.getAppKey();
    }

    @Override
    public void setUserExtras(Map<String, String> map) {
        userInfo.setUserExtras(map);
    }

    @Override
    public void setUserExtras(String s, String s1) {
        userInfo.setUserExtras(s,s1);
    }

    @Override
    public void setBirthday(long l) {
        userInfo.setBirthday(l);
    }

    @Override
    public void setNoDisturb(int i, BasicCallback basicCallback) {
        userInfo.setNoDisturb(i,basicCallback);
    }

    @Override
    public void removeFromFriendList(BasicCallback basicCallback) {
        userInfo.removeFromFriendList(basicCallback);
    }

    @Override
    public void updateNoteName(String s, BasicCallback basicCallback) {
        userInfo.updateNoteName(s,basicCallback);
    }

    @Override
    public void updateNoteText(String s, BasicCallback basicCallback) {
        userInfo.updateNoteText(s,basicCallback);
    }
}
