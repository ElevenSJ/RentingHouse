package com.sj.rentinghouse.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.logger.Logger;
import com.sj.im.ChatActivity;
import com.sj.im.JMUIMessage;
import com.sj.im.JMUserInfo;
import com.sj.im.SharePreferenceManager;
import com.sj.im.TimeFormat;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.HouseDetailActivity;
import com.sj.rentinghouse.activity.IMChatActivity;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Sunj on 2018/7/8.
 */

public class ConversationRyvAdapter extends RecyclerArrayAdapter<Conversation> {
    public ConversationRyvAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConversationRyvHolder(parent);
    }

    public class ConversationRyvHolder extends BaseViewHolder<Conversation> {
        ImageView headIcon;
        TextView convName;
        TextView content;
        TextView datetime;
        TextView newGroupMsgNumber;
        TextView newMsgNumber;
        ImageView groupBlocked;
        ImageView newMsgDisturb;
        ImageView newGroupMsgDisturb;

        public ConversationRyvHolder(ViewGroup parent) {
            super(parent, R.layout.item_conv_list);
            headIcon = $(R.id.msg_item_head_icon);
            convName = $(R.id.conv_item_name);
            content = $(R.id.msg_item_content);
            datetime = $(R.id.msg_item_date);
            newGroupMsgNumber = $(R.id.new_group_msg_number);
            newMsgNumber = $(R.id.new_msg_number);
            groupBlocked = $(R.id.iv_groupBlocked);
            newMsgDisturb = $(R.id.new_group_msg_disturb);
            newGroupMsgDisturb = $(R.id.new_msg_disturb);
        }

        @Override
        public void setData(final Conversation convItem) {
            super.setData(convItem);
            final Message lastMsg = convItem.getLatestMessage();
            if (lastMsg != null) {
                JMUIMessage imMessage = new JMUIMessage(lastMsg);
                datetime.setText(TimeFormat.getTime( imMessage.getCreateTime()));
                String contentStr;
                switch (imMessage.getContentType()) {
                    case image:
                        contentStr = itemView.getContext().getString(R.string.type_picture);
                        break;
                    case voice:
                        contentStr = itemView.getContext().getString(R.string.type_voice);
                        break;
                    case location:
                        contentStr = itemView.getContext().getString(R.string.type_location);
                        break;
                    case file:
                        String extra = lastMsg.getContent().getStringExtra("video");
                        if (!TextUtils.isEmpty(extra)) {
                            contentStr = itemView.getContext().getString(R.string.type_smallvideo);
                        } else {
                            contentStr = itemView.getContext().getString(R.string.type_file);
                        }
                        break;
                    case video:
                        contentStr = itemView.getContext().getString(R.string.type_video);
                        break;
                    case eventNotification:
                        contentStr = itemView.getContext().getString(R.string.group_notification);
                        break;
                    case custom:
                        CustomContent customContent = (CustomContent) lastMsg.getContent();
                        Boolean isBlackListHint = customContent.getBooleanValue("blackList");
                        if (isBlackListHint != null && isBlackListHint) {
                            contentStr = itemView.getContext().getString(R.string.jmui_server_803008);
                        } else {
                            contentStr = itemView.getContext().getString(R.string.type_custom);
                        }
                        break;
                    case prompt:
                        contentStr = ((PromptContent) lastMsg.getContent()).getPromptText();
                        break;
                    default:
                        contentStr = ((TextContent) lastMsg.getContent()).getText();
                        break;
                }
                if (lastMsg.getUnreceiptCnt() == 0) {
                    if (lastMsg.getTargetType().equals(ConversationType.single) &&
                            lastMsg.getDirect().equals(MessageDirect.send) &&
                            !lastMsg.getContentType().equals(ContentType.prompt) &&
                            //排除自己给自己发送消息
                            !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                        content.setText("[已读]" + contentStr);
                    } else {
                        content.setText(contentStr);
                    }
                } else {
                    if (lastMsg.getTargetType().equals(ConversationType.single) &&
                            lastMsg.getDirect().equals(MessageDirect.send) &&
                            !lastMsg.getContentType().equals(ContentType.prompt) &&
                            !((UserInfo) lastMsg.getTargetInfo()).getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                        contentStr = "[未读]" + contentStr;
                        SpannableStringBuilder builder = new SpannableStringBuilder(contentStr);
                        builder.setSpan(new ForegroundColorSpan(itemView.getContext().getResources().getColor(R.color.line_normal)),
                                0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        content.setText(builder);
                    } else {
                        content.setText(contentStr);
                    }
                }
            } else {
                if (convItem.getLastMsgDate() == 0) {
                    //会话列表时间展示的是最后一条会话,那么如果最后一条消息是空的话就不显示
                    datetime.setText("");
                    content.setText("");
                } else {
                    datetime.setText(TimeFormat.getTime(convItem.getLastMsgDate()));
                    content.setText("");
                }
            }
            if (convItem.getType().equals(ConversationType.single)) {
                JMUserInfo mUserInfo = new JMUserInfo((UserInfo) convItem.getTargetInfo());
                convName.setText(mUserInfo.getDisplayName());
                groupBlocked.setVisibility(View.GONE);
                if (mUserInfo != null) {
                    mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int status, String desc, Bitmap bitmap) {
                            if (status == 0) {
                                headIcon.setImageBitmap(bitmap);
                            } else {
                                headIcon.setImageResource(R.drawable.jmui_head_icon);
                            }
                        }
                    });
                } else {
                    headIcon.setImageResource(R.drawable.jmui_head_icon);
                }
                if (convItem.getUnReadMsgCnt() > 0) {
                    newGroupMsgDisturb.setVisibility(View.GONE);
                    newMsgDisturb.setVisibility(View.GONE);
                    newGroupMsgNumber.setVisibility(View.GONE);
                    newMsgNumber.setVisibility(View.GONE);

                    if (mUserInfo != null && mUserInfo.getNoDisturb() == 1) {
                        newMsgDisturb.setVisibility(View.VISIBLE);
                    } else {
                        newMsgNumber.setVisibility(View.VISIBLE);
                    }
                    if (convItem.getUnReadMsgCnt() < 100) {
                        newMsgNumber.setText(String.valueOf(convItem.getUnReadMsgCnt()));
                    } else {
                        newMsgNumber.setText("99+");
                    }

                } else {
                    newGroupMsgDisturb.setVisibility(View.GONE);
                    newMsgDisturb.setVisibility(View.GONE);
                    newGroupMsgNumber.setVisibility(View.GONE);
                    newMsgNumber.setVisibility(View.GONE);
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(Boolean) SPUtils.getInstance().getSharedPreference(NameSpace.IS_IM_LOGIN, false)) {
                        doImLogin(view,convItem.getTargetId(),(String)SPUtils.getInstance().getSharedPreference(NameSpace.IM_ACCOUNT, ""));
                    }else{
                        toImChat(view,convItem.getTargetId());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    DialogUtils.showChooseDialog(view.getContext(), new String[]{"删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (convItem.getType() == ConversationType.single) {
                                JMessageClient.deleteSingleConversation(((UserInfo) convItem.getTargetInfo()).getUserName());
                            } else {
                                JMessageClient.deleteGroupConversation(((GroupInfo) convItem.getTargetInfo()).getGroupID());
                            }
                            EventManger.getDefault().postMessageRefreshEvent();
                        }
                    });
                    return false;
                }
            });
        }
    }

    public void toImChat(View v,String id) {
        Intent imIntent = new Intent(getContext(), IMChatActivity.class);
        imIntent.putExtra("targetId", id);
        v.getContext().startActivity(imIntent);
    }

    public void doImLogin(final View v, final String id, String imAccount) {
        JMessageClient.login(imAccount, imAccount, new BasicCallback() {
            @Override
            public void gotResult(final int status, final String desc) {
                SPUtils.getInstance().apply(NameSpace.IS_IM_LOGIN,status == 0);
                EventManger.getDefault().postIMLoginEvent(status == 0);
                if (status == 0) {
                    String username = JMessageClient.getMyInfo().getUserName();
                    String appKey = JMessageClient.getMyInfo().getAppKey();
                    SharePreferenceManager.setCachedUsername(username);
                    SharePreferenceManager.setAppKey(appKey);
                    Logger.e("极光IM登录成功 status= " + status);
                    toImChat(v,id);
                } else {
                    Logger.e("极光IM登录失败 status= " + status);
                    ToastUtils.showShortToast("交谈登录失败，请重试！");
                }
            }
        });
    }
}
