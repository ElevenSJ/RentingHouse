package com.sj.im;


import com.sj.module_lib.utils.DateUtils;

import java.util.HashMap;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jpush.im.android.api.callback.GetReceiptDetailsCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.MediaContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by caiyaoguan on 17/4/25.
 */

public class JMUIMessage extends Message implements IMessage {

    private JMUserInfo jmUserInfo;
    private Message message;
    private String mediaFilePath;
    private MessageType type;
    private String progress;
    private ContentType contentType;

    public JMUIMessage(Message message) {
        this.message = message;
        this.jmUserInfo = new JMUserInfo(message.getFromUser());
        contentType = message.getContentType();
        if (contentType == ContentType.voice || contentType == ContentType.image
                || contentType == ContentType.video || contentType == ContentType.file) {
            mediaFilePath = ((MediaContent) message.getContent()).getLocalPath();
        }
    }

    public Message getJMessage() {
        return this.message;
    }

    public int getId() {
        return message.getId();
    }

    public long getCreateTime() {
        return message.getCreateTime();
    }
    public ContentType getContentType() {
        return contentType;
    }

//    @Override
//    public int getType() {
//        if (message.getDirect() == MessageDirect.send) {
//            switch (message.getContentType()) {
//                case text:
//                    return MessageType.SEND_TEXT;
//                case image:
//                    return MessageType.SEND_IMAGE;
//                case video:
//                    return MessageType.SEND_VIDEO;
//                case voice:
//                    return MessageType.SEND_VOICE;
//                case location:
//                    return MessageType.SEND_LOCATION;
//            }
//        } else {
//            switch (message.getContentType()) {
//                case text:
//                    return MessageType.RECEIVE_TEXT;
//                case image:
//                    return MessageType.RECEIVE_IMAGE;
//                case video:
//                    return MessageType.RECEIVE_VIDEO;
//                case voice:
//                    return MessageType.RECEIVE_VOICE;
//                case location:
//                    return MessageType.RECEIVE_LOCATION;
//            }
//        }
//        return null;
//    }

    @Override
    public MessageStatus getMessageStatus() {
        switch (message.getStatus()) {
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
        return ((TextContent) message.getContent()).getText();
    }

    public void setMediaFilePath(String path) {
        this.mediaFilePath = path;
    }

    @Override
    public String getMediaFilePath() {
        return this.mediaFilePath;
    }

    @Override
    public long getDuration() {
        if (message.getContentType() == ContentType.voice) {
            return ((VoiceContent) message.getContent()).getDuration();
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

    @Override
    public void getAtUserList(GetUserInfoListCallback getUserInfoListCallback) {
        message.getAtUserList(getUserInfoListCallback);
    }

    @Override
    public boolean isAtMe() {
        return message.isAtMe();
    }

    @Override
    public boolean isAtAll() {
        return message.isAtAll();
    }

    @Override
    public String getFromAppKey() {
        return message.getFromAppKey();
    }

    @Override
    public String getMsgId() {
        return String.valueOf(message.getId());
    }

    @Override
    public JMUserInfo getFromUser() {
        return jmUserInfo;
    }

    @Override
    public String getTimeString() {
        return DateUtils.getTime(message.getCreateTime());
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getTargetName() {
        return message.getTargetName();
    }

    @Override
    public String getTargetID() {
        return message.getTargetID();
    }

    @Override
    public String getTargetAppKey() {
        return message.getTargetAppKey();
    }

    @Override
    public void setOnContentUploadProgressCallback(ProgressUpdateCallback progressUpdateCallback) {
        message.setOnContentUploadProgressCallback(progressUpdateCallback);
    }

    @Override
    public boolean isContentUploadProgressCallbackExists() {
        return message.isContentUploadProgressCallbackExists();
    }

    @Override
    public void setOnContentDownloadProgressCallback(ProgressUpdateCallback progressUpdateCallback) {
        message.setOnContentDownloadProgressCallback(progressUpdateCallback);
    }

    @Override
    public boolean isContentDownloadProgressCallbackExists() {
        return message.isContentDownloadProgressCallbackExists();
    }

    @Override
    public void setOnSendCompleteCallback(BasicCallback basicCallback) {
        message.setOnSendCompleteCallback(basicCallback);
    }

    @Override
    public boolean isSendCompleteCallbackExists() {
        return message.isSendCompleteCallbackExists();
    }

    @Override
    public boolean haveRead() {
        return message.haveRead();
    }

    @Override
    public void setHaveRead(BasicCallback basicCallback) {
        message.setHaveRead(basicCallback);
    }

    @Override
    public int getUnreceiptCnt() {
        return message.getUnreceiptCnt();
    }

    @Override
    public long getUnreceiptMtime() {
        return message.getUnreceiptMtime();
    }

    @Override
    public void setUnreceiptCnt(int i) {
        message.setUnreceiptCnt(i);
    }

    @Override
    public void setUnreceiptMtime(long l) {
        message.setUnreceiptMtime(l);
    }

    @Override
    public void getReceiptDetails(GetReceiptDetailsCallback getReceiptDetailsCallback) {
        message.getReceiptDetails(getReceiptDetailsCallback);
    }

}
