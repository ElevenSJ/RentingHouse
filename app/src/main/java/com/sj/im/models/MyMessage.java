package com.sj.im.models;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.sj.im.TimeFormat;
import com.sj.module_lib.utils.DateUtils;

import java.util.HashMap;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jpush.im.android.api.callback.GetReceiptDetailsCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MediaContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;


public class MyMessage extends Message implements IMessage {
    Message jMessage;
    DefaultUser jmUserInfo;
    private String mediaFilePath;
    private String  originImagePath;
    private String progress;
    private ContentType contentType;
    public MyMessage(Message message) {
        this.jMessage = message;
        this.jmUserInfo = new DefaultUser(message.getFromUser());
        contentType = message.getContentType();
        if (contentType == ContentType.voice || contentType == ContentType.image
                || contentType == ContentType.video || contentType == ContentType.file) {
            mediaFilePath = ((MediaContent) message.getContent()).getLocalPath();
        }
    }

    public Message getMessage() {
        return jMessage;
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    public String getOriginImagePath() {
        return originImagePath;
    }

    public void setOriginImagePath(String originImagePath) {
        this.originImagePath = originImagePath;
    }

    @Override
    public String getMsgId() {
        return String.valueOf(jMessage.getId());
    }

    @Override
    public void getAtUserList(GetUserInfoListCallback getUserInfoListCallback) {
        jMessage.getAtUserList(getUserInfoListCallback);
    }

    @Override
    public boolean isAtMe() {
        return jMessage.isAtMe();
    }

    @Override
    public boolean isAtAll() {
        return jMessage.isAtAll();
    }

    @Override
    public String getFromAppKey() {
        return jMessage.getFromAppKey();
    }

    @Override
    public DefaultUser getFromUser() {
        return jmUserInfo;
    }

    @Override
    public String getTargetName() {
        return jMessage.getTargetName();
    }

    @Override
    public String getTargetID() {
        return jMessage.getTargetID();
    }

    @Override
    public String getTargetAppKey() {
        return jMessage.getTargetAppKey();
    }

    @Override
    public void setOnContentUploadProgressCallback(ProgressUpdateCallback progressUpdateCallback) {
        jMessage.setOnContentUploadProgressCallback(progressUpdateCallback);
    }

    @Override
    public boolean isContentUploadProgressCallbackExists() {
        return jMessage.isContentUploadProgressCallbackExists();
    }

    @Override
    public void setOnContentDownloadProgressCallback(ProgressUpdateCallback progressUpdateCallback) {
        jMessage.setOnContentDownloadProgressCallback(progressUpdateCallback);
    }

    @Override
    public boolean isContentDownloadProgressCallbackExists() {
        return jMessage.isContentDownloadProgressCallbackExists();
    }

    @Override
    public void setOnSendCompleteCallback(BasicCallback basicCallback) {
        jMessage.setOnSendCompleteCallback(basicCallback);
    }

    @Override
    public boolean isSendCompleteCallbackExists() {
        return jMessage.isSendCompleteCallbackExists();
    }

    @Override
    public boolean haveRead() {
        return jMessage.haveRead();
    }

    @Override
    public void setHaveRead(BasicCallback basicCallback) {
        jMessage.setHaveRead(basicCallback);
    }

    @Override
    public int getUnreceiptCnt() {
        return jMessage.getUnreceiptCnt();
    }

    @Override
    public long getUnreceiptMtime() {
        return jMessage.getUnreceiptMtime();
    }

    @Override
    public void setUnreceiptCnt(int i) {
        jMessage.setUnreceiptCnt(i);
    }

    @Override
    public void setUnreceiptMtime(long l) {
        jMessage.setUnreceiptMtime(l);
    }

    @Override
    public void getReceiptDetails(GetReceiptDetailsCallback getReceiptDetailsCallback) {
        jMessage.getReceiptDetails(getReceiptDetailsCallback);
    }

    @Override
    public String getTimeString() {
        return TimeFormat.getDetailTime(jMessage.getCreateTime());
    }

    @Override
    public int getType() {
        if (jMessage.getDirect() == MessageDirect.send) {
            switch (jMessage.getContentType()) {
                case text:
                    return MessageType.SEND_TEXT.ordinal();
                case image:
                    return MessageType.SEND_IMAGE.ordinal();
                case video:
                    return MessageType.SEND_VIDEO.ordinal();
                case voice:
                    return MessageType.SEND_VOICE.ordinal();
                case location:
                    return MessageType.SEND_LOCATION.ordinal();
            }
        } else {
            switch (jMessage.getContentType()) {
                case text:
                    return MessageType.RECEIVE_TEXT.ordinal();
                case image:
                    return MessageType.RECEIVE_IMAGE.ordinal();
                case video:
                    return MessageType.RECEIVE_VIDEO.ordinal();
                case voice:
                    return MessageType.RECEIVE_VOICE.ordinal();
                case location:
                    return MessageType.RECEIVE_LOCATION.ordinal();
            }
        }
        return 0;
    }

    @Override
    public MessageStatus getMessageStatus() {
        switch (jMessage.getStatus()) {
            case created:
                return MessageStatus.CREATED;
            case send_going:
                return MessageStatus.SEND_GOING;
            case send_fail:
                return MessageStatus.SEND_FAILED;
            case send_success:
                return MessageStatus.SEND_SUCCEED;
            case receive_going:
                return MessageStatus.RECEIVE_GOING;
            case receive_fail:
                return MessageStatus.RECEIVE_FAILED;
            default:
                return MessageStatus.RECEIVE_SUCCEED;
        }
    }

    @Override
    public String getText() {
        return ((TextContent) jMessage.getContent()).getText();
    }

    @Override
    public String getMediaFilePath() {
       return TextUtils.isEmpty(originImagePath)?mediaFilePath:originImagePath;
    }

    @Override
    public long getDuration() {
        if (jMessage.getContentType() == ContentType.voice) {
            return ((VoiceContent) jMessage.getContent()).getDuration();
        }
        return 0;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    @Override
    public String getProgress() {
        return this.progress;
    }

    @Override
    public HashMap<String, String> getExtras() {
        return null;
    }
}