package com.sj.rentinghouse.events;

import com.sj.rentinghouse.bean.UserInfo;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Sunj on 2018/7/26.
 */

public class EventManger {
    static volatile EventManger Instance;

    public static EventManger getDefault() {
        if (Instance == null) {
            synchronized (EventManger.class) {
                if (Instance == null) {
                    Instance = new EventManger();
                }
            }
        }
        return Instance;
    }

    /**
     * 登录
     */
    public void postLoginEvent(boolean isSuccess) {
        EventBus.getDefault().postSticky(new LoginEvent(isSuccess));
    }
    /**
     * 退出
     */
    public void postLoginOutEvent(boolean isSuccess) {
        EventBus.getDefault().post(new LoginOutEvent(isSuccess));
    }
    /**
     * IM登录
     */
    public void postIMLoginEvent(boolean isSuccess) {
        EventBus.getDefault().postSticky(new IMLoginEvent(isSuccess));
    }

    /**
     * 主页切换
     */
    public void postMainPageEvent(int index) {
        EventBus.getDefault().post(new MainPageSwitchEvent(index));
    }

    /**
     * 足迹单切换
     */
    public void postTrackPageEvent(int index) {
        EventBus.getDefault().post(new TrackPageSwitchEvent(index));
    }

    /**
     * 消息切换
     */
    public void postMessagePageEvent(int index) {
        EventBus.getDefault().post(new MessagePageSwitchEvent(index));
    }

    /**
     * 我的切换
     */
    public void postMyPageEvent(int index) {
        EventBus.getDefault().post(new MyPageSwitchEvent(index));
    }

    /**
     * 我的页面刷新
     */
    public void postMyRefreshEvent() {
        EventBus.getDefault().post(new MyRefreshEvent());
    }

    /**
     * 首页的切换
     */
    public void postMainRefreshEvent() {
        EventBus.getDefault().post(new MainRefreshEvent());
    }

    /**
     * 足迹单的切换
     */
    public void postTrackRefreshEvent() {
        EventBus.getDefault().post(new TrackRefreshEvent());
    }
    /**
     * 消息的刷新
     */
    public void postMessageRefreshEvent() {
        EventBus.getDefault().post(new MessageRefreshEvent());
    }

    /**
     * 个人信息同步更新
     */
    public void postUserInfoEvent(UserInfo userInfo) {
        EventBus.getDefault().post(new UserInfoEvent(userInfo));
    }
}
