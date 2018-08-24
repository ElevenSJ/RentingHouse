package com.sj.rentinghouse.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.sj.im.BitmapLoader;
import com.sj.im.SharePreferenceManager;
import com.sj.im.models.DefaultUser;
import com.sj.im.models.MyMessage;
import com.sj.im.view.ChatView;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.utils.NameSpace;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.chatinput.photo.SelectPhotoView;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.messages.MsgListAdapter;
import cn.jiguang.imui.messages.ViewHolderController;
import cn.jiguang.imui.messages.ptr.PtrHandler;
import cn.jiguang.imui.messages.ptr.PullToRefreshLayout;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class IMChatActivity extends AppBaseActivity implements View.OnTouchListener, SensorEventListener {

    private final static String TAG = "IMChatActivity";

    private ChatView mChatView;
    private MsgListAdapter<MyMessage> mAdapter;
    private List<MyMessage> mData = new ArrayList<>();
    private List<Message> mMsgList = new ArrayList<>();
    private Conversation mConv;
    private DefaultUser mTargetInfo;
    private DefaultUser myUserInfo;
    private String mTargetId;
    private String mTargetAppKey;

    public static final int PAGE_MESSAGE_COUNT = 18;
    private int mOffset = PAGE_MESSAGE_COUNT;
    private int mStart = 0;

    private InputMethodManager mImm;
    private Window mWindow;
    private HeadsetDetectReceiver mReceiver;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private ArrayList<String> mPathList = new ArrayList<>();
    private ArrayList<String> mMsgIdList = new ArrayList<>();

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mTargetId = getIntent().getStringExtra("targetId");
        mTargetAppKey = SharePreferenceManager.getAppKey();
        myUserInfo = new DefaultUser(JMessageClient.getMyInfo());
    }

    @Override
    public void setStatusView() {
    }

    @Override
    public void initEvent() {
        JMessageClient.registerEventReceiver(this);
        this.mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mWindow = getWindow();

        registerProximitySensorListener();
        mReceiver = new HeadsetDetectReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mReceiver, intentFilter);

        mChatView = findViewById(R.id.chat_view);
        mChatView.initModule();
        try {
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            if (mConv == null) {
                mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
            }
            this.mMsgList = mConv.getMessagesFromNewest(mStart, mOffset);
            mStart = mOffset;
            if (mMsgList.size() > 0) {
                for (Message message : mMsgList) {
                    message.setHaveRead(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {

                        }
                    });
                    MyMessage MyMessage = new MyMessage(message);
                    mData.add(MyMessage);
                }
            }
//            UserInfo userInfo = (UserInfo) mConv.getTargetInfo();
//            if (userInfo == null) {
            JMessageClient.getUserInfo(mTargetId, new GetUserInfoCallback() {
                @Override
                public void gotResult(int status, String s, UserInfo userInfo) {
                    if (status == 0) {
                        mTargetInfo = new DefaultUser(userInfo);
                        setTopTitle(R.id.tv_top_title, mTargetInfo.getDisplayName());
                    }
                }
            });
//            } else {
//                mTargetInfo = new DefaultUser(userInfo);
//                setTopTitle(R.id.tv_top_title, mTargetInfo.getDisplayName());
//            }
            initMsgAdapter();
            mChatView.setOnTouchListener(this);
            mChatView.setMenuClickListener(new OnMenuClickListener() {
                @Override
                public boolean onSendTextMessage(CharSequence input) {
                    if (input.length() == 0) {
                        return false;
                    }
                    TextContent textContent = new TextContent(input.toString());
                    Message message = mConv.createSendMessage(textContent);
                    JMessageClient.sendMessage(message);
                    final MyMessage msg = new MyMessage(message);
                    IMChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addToStart(msg, true);
                        }
                    });
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            if (status == 0) {
                                Log.i(TAG, "send message succeed!");
                            } else {
                                Log.i(TAG, "send message failed " + desc);
                            }
                            mAdapter.updateMessage(msg);
                        }
                    });
                    return true;
                }

                @Override
                public void onSendFiles(List<FileItem> list) {
                    if (list == null || list.isEmpty()) {
                        return;
                    }

                    for (final FileItem item : list) {
                        if (item.getType() == FileItem.Type.Image) {
                            Bitmap bitmap = BitmapLoader.getBitmapFromFile(item.getFilePath(), 720, 1280);
                            ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                                @Override
                                public void gotResult(int status, String desc, ImageContent imageContent) {
                                    if (status == 0) {
                                        Message msg = mConv.createSendMessage(imageContent);
                                        JMessageClient.sendMessage(msg);
                                        final MyMessage MyMessage = new MyMessage(msg);
                                        MyMessage.setMediaFilePath(item.getFilePath());
                                        MyMessage.setOriginImagePath(item.getFilePath());
                                        IMChatActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.addToStart(MyMessage, true);
                                            }
                                        });
                                        msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                                            @Override
                                            public void onProgressUpdate(double v) {
                                                MyMessage.setProgress(Math.ceil(v * 100) + "%");
                                                Log.w(TAG, "Uploading image progress" + Math.ceil(v * 100) + "%");
                                                mAdapter.updateMessage(MyMessage);
                                            }
                                        });
                                        msg.setOnSendCompleteCallback(new BasicCallback() {
                                            @Override
                                            public void gotResult(int status, String desc) {
                                                mAdapter.updateMessage(MyMessage);
                                                if (status == 0) {
                                                    Log.i(TAG, "Send image succeed");
                                                } else {
                                                    Log.i(TAG, "Send image failed, " + desc);
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        } else if (item.getType() == FileItem.Type.Video) {
//                        message = new MyMessage(null, IMessage.MessageType.SEND_VIDEO);
                        } else {
                            throw new RuntimeException("Invalid FileItem type. Must be Type.Image or Type.Video.");
                        }
                    }
                }

                @Override
                public boolean switchToMicrophoneMode() {
                    scrollToBottom();
                    AndPermission.with(IMChatActivity.this)
                            .permission(Permission.RECORD_AUDIO,
                                    Permission.WRITE_EXTERNAL_STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                }
                            }).onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            // TODO what to do
                        }
                    }).start();
                    return true;
                }

                @Override
                public boolean switchToGalleryMode() {
                    scrollToBottom();
                    AndPermission.with(IMChatActivity.this)
                            .permission(Permission.RECORD_AUDIO,
                                    Permission.READ_EXTERNAL_STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
                                }
                            }).onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            // TODO what to do
                        }
                    }).start();
                    ((SelectPhotoView) mChatView.getChatInputView().getSelectPictureContainer()).initData();
                    return true;
                }

                @Override
                public boolean switchToCameraMode() {
                    scrollToBottom();
                    AndPermission.with(IMChatActivity.this)
                            .permission(Permission.RECORD_AUDIO,
                                    Permission.CAMERA,
                                    Permission.WRITE_EXTERNAL_STORAGE)
                            .onGranted(new Action() {
                                @Override
                                public void onAction(List<String> permissions) {
//                                    File rootDir = getFilesDir();
//                                    String fileDir = rootDir.getAbsolutePath() + "/photo";
//                                    mChatView.setCameraCaptureFile(fileDir, new SimpleDateFormat("yyyy-MM-dd-hhmmss",
//                                            Locale.getDefault()).format(new Date()));
                                    openImageSelect();
                                }
                            }).onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            // TODO what to do
                        }
                    }).start();
                    return false;
                }

                @Override
                public boolean switchToEmojiMode() {
                    scrollToBottom();
                    return true;
                }
            });

            mChatView.setRecordVoiceListener(new RecordVoiceListener() {
                @Override
                public void onStartRecord() {
                    // set voice file path, after recording, audio file will save here
                    String path = Environment.getExternalStorageDirectory().getPath() + "/voice";
                    File destDir = new File(path);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }
                    mChatView.setRecordVoiceFile(destDir.getPath(), DateFormat.format("yyyy-MM-dd-hhmmss",
                            Calendar.getInstance(Locale.CHINA)) + "");
                }

                @Override
                public void onFinishRecord(File voiceFile, int duration) {
                    try {
                        VoiceContent content = new VoiceContent(voiceFile, duration);
                        Message msg = mConv.createSendMessage(content);
                        JMessageClient.sendMessage(msg);
                        final MyMessage MyMessage = new MyMessage(msg);
                        mAdapter.addToStart(MyMessage, true);
                        msg.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int status, String s) {
                                mAdapter.updateMessage(MyMessage);
                                if (status == 0) {
                                    Log.i(TAG, "send voice message succeed!");
                                } else {
                                    Log.i(TAG, "send voice message failed");
                                }
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelRecord() {

                }

                /**
                 * In preview record voice layout, fires when click cancel button
                 * Add since chatinput v0.7.3
                 */
                @Override
                public void onPreviewCancel() {

                }

                /**
                 * In preview record voice layout, fires when click send button
                 * Add since chatinput v0.7.3
                 */
                @Override
                public void onPreviewSend() {

                }
            });

//            mChatView.setOnCameraCallbackListener(new OnCameraCallbackListener() {
//                @Override
//                public void onTakePictureCompleted(final String photoPath) {
//                    Bitmap bitmap = BitmapLoader.getBitmapFromFile(photoPath, 720, 1280);
//                    ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
//                        @Override
//                        public void gotResult(int status, String desc, ImageContent imageContent) {
//                            if (status == 0) {
//                                Message msg = mConv.createSendMessage(imageContent);
//                                JMessageClient.sendMessage(msg);
//                                final MyMessage myMessage = new MyMessage(msg);
//                                myMessage.setMediaFilePath(photoPath);
//                                IMChatActivity.this.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mAdapter.addToStart(myMessage, true);
//                                    }
//                                });
//                                msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
//                                    @Override
//                                    public void onProgressUpdate(double v) {
//                                        myMessage.setProgress(Math.ceil(v * 100) + "%");
//                                        Log.w(TAG, "Uploading image progress" + Math.ceil(v * 100) + "%");
//                                        mAdapter.updateMessage(myMessage);
//                                    }
//                                });
//                                msg.setOnSendCompleteCallback(new BasicCallback() {
//                                    @Override
//                                    public void gotResult(int status, String desc) {
//                                        mAdapter.updateMessage(myMessage);
//                                        if (status == 0) {
//                                            Log.i(TAG, "Send image succeed");
//                                        } else {
//                                            Log.i(TAG, "Send image failed, " + desc);
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onStartVideoRecord() {
//                    ToastUtils.showShortToast("不支持视频文件");
//
//                }
//
//                @Override
//                public void onFinishVideoRecord(String videoPath) {
//
//                }
//
//                @Override
//                public void onCancelVideoRecord() {
//
//                }
//            });

            mChatView.getChatInputView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    scrollToBottom();
                    return false;
                }
            });

            mChatView.getSelectAlbumBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImageSelect();
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            ToastUtils.showShortToast("交谈异常,请重试!");
            SPUtils.getInstance().apply(NameSpace.IS_IM_LOGIN,false);
            finish();
        }
    }

    private void openImageSelect() {
        PictureSelector.create(IMChatActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_main_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(false)// 是否裁剪
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .selectionMedia(null)// 是否传入已选图片
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void registerProximitySensorListener() {
        try {
            mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        try {
            if (audioManager.isBluetoothA2dpOn() || audioManager.isWiredHeadsetOn()) {
                return;
            }
            if (mAdapter.getMediaPlayer().isPlaying()) {
                float distance = event.values[0];
                if (distance >= mSensor.getMaximumRange()) {
                    mAdapter.setAudioPlayByEarPhone(0);
                    setScreenOn();
                } else {
                    mAdapter.setAudioPlayByEarPhone(2);
                    ViewHolderController.getInstance().replayVoice();
                    setScreenOff();
                }
            } else {
                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setScreenOn() {
        if (mWakeLock != null) {
            mWakeLock.setReferenceCounted(false);
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    private void setScreenOff() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        mWakeLock.acquire();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class HeadsetDetectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                if (intent.hasExtra("state")) {
                    int state = intent.getIntExtra("state", 0);
                    mAdapter.setAudioPlayByEarPhone(state);
                }
            }
        }
    }


    private void initMsgAdapter() {
        final float density = getResources().getDisplayMetrics().density;
        final float MIN_WIDTH = 60 * density;
        final float MAX_WIDTH = 200 * density;
        final float MIN_HEIGHT = 60 * density;
        final float MAX_HEIGHT = 200 * density;
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                // You can use other image load libraries.
                if (string.contains("R.drawable")) {
                    Integer resId = getResources().getIdentifier(string.replace("R.drawable.", ""),
                            "drawable", getPackageName());

                    avatarImageView.setImageResource(resId);
                } else {
                    Glide.with(IMChatActivity.this)
                            .load(string)
                            .placeholder(R.drawable.jmui_head_icon)
                            .error(R.drawable.jmui_head_icon)
                            .into(avatarImageView);
                }
            }

            /**
             * Load image message
             * @param imageView Image message's ImageView.
             * @param string A file path, or a uri or url.
             */
            @Override
            public void loadImage(final ImageView imageView, String string) {
                // You can use other image load libraries.
                Glide.with(getApplicationContext())
                        .load(string)
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.aurora_picture_not_found)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                int imageWidth = resource.getWidth();
                                int imageHeight = resource.getHeight();
                                Log.d(TAG, "Image width " + imageWidth + " height: " + imageHeight);

                                // 裁剪 bitmap
                                float width, height;
                                if (imageWidth > imageHeight) {
                                    if (imageWidth > MAX_WIDTH) {
                                        float temp = MAX_WIDTH / imageWidth * imageHeight;
                                        height = temp > MIN_HEIGHT ? temp : MIN_HEIGHT;
                                        width = MAX_WIDTH;
                                    } else if (imageWidth < MIN_WIDTH) {
                                        float temp = MIN_WIDTH / imageWidth * imageHeight;
                                        height = temp < MAX_HEIGHT ? temp : MAX_HEIGHT;
                                        width = MIN_WIDTH;
                                    } else {
                                        float ratio = imageWidth / imageHeight;
                                        if (ratio > 3) {
                                            ratio = 3;
                                        }
                                        height = imageHeight * ratio;
                                        width = imageWidth;
                                    }
                                } else {
                                    if (imageHeight > MAX_HEIGHT) {
                                        float temp = MAX_HEIGHT / imageHeight * imageWidth;
                                        width = temp > MIN_WIDTH ? temp : MIN_WIDTH;
                                        height = MAX_HEIGHT;
                                    } else if (imageHeight < MIN_HEIGHT) {
                                        float temp = MIN_HEIGHT / imageHeight * imageWidth;
                                        width = temp < MAX_WIDTH ? temp : MAX_WIDTH;
                                        height = MIN_HEIGHT;
                                    } else {
                                        float ratio = imageHeight / imageWidth;
                                        if (ratio > 3) {
                                            ratio = 3;
                                        }
                                        width = imageWidth * ratio;
                                        height = imageHeight;
                                    }
                                }
                                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                                params.width = (int) width;
                                params.height = (int) height;
                                imageView.setLayoutParams(params);
                                Matrix matrix = new Matrix();
                                float scaleWidth = width / imageWidth;
                                float scaleHeight = height / imageHeight;
                                matrix.postScale(scaleWidth, scaleHeight);
                                imageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, imageWidth, imageHeight, matrix, true));
                            }
                        });
            }

            /**
             * Load video message
             * @param imageCover Video message's image cover
             * @param uri Local path or url.
             */
            @Override
            public void loadVideo(ImageView imageCover, String uri) {
                long interval = 5000 * 1000;
                Glide.with(IMChatActivity.this)
                        .load(uri)
                        .asBitmap()
                        .override(200, 400)
                        .into(imageCover);
            }
        };

        // Use default layout
        MsgListAdapter.HoldersConfig holdersConfig = new MsgListAdapter.HoldersConfig();
        mAdapter = new MsgListAdapter<>(myUserInfo.getId(), holdersConfig, imageLoader);
        mAdapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(final MyMessage message) {
                if (message.getMessage().getContentType() == ContentType.image) {
                    final ImageContent imgContent = (ImageContent) message.getMessage().getContent();
                    if (TextUtils.isEmpty(message.getOriginImagePath())) {
                        imgContent.downloadOriginImage(message.getMessage(), new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {
                                String path = "";
                                if (status == 0) {
                                    path = file.getPath();
                                } else {
                                    path = message.getMediaFilePath();
                                }
                                if (!TextUtils.isEmpty(path)) {
                                    List<LocalMedia> selectList = new ArrayList<>(1);
                                    selectList.add(new LocalMedia(path, 0, PictureMimeType.ofImage(), null));
                                    PictureSelector.create(IMChatActivity.this).themeStyle(R.style.picture_main_style).openExternalPreview(0, selectList);
                                } else {
                                    ToastUtils.showShortToast("未获取到图片");
                                }
                            }
                        });
                    } else {
                        String path = message.getOriginImagePath();
                        if (!TextUtils.isEmpty(path)) {
                            List<LocalMedia> selectList = new ArrayList<>(1);
                            selectList.add(new LocalMedia(path, 0, PictureMimeType.ofImage(), null));
                            PictureSelector.create(IMChatActivity.this).themeStyle(R.style.picture_main_style).openExternalPreview(0, selectList);
                        } else {
                            ToastUtils.showShortToast("未获取到图片");
                        }
                    }
                }
                // do something
//                if (message.getType() == IMessage.MessageType.RECEIVE_VIDEO.ordinal()
//                        || message.getType() == IMessage.MessageType.SEND_VIDEO.ordinal()) {
//                    if (!TextUtils.isEmpty(message.getMediaFilePath())) {
//                        Intent intent = new Intent(IMChatActivity.this, VideoActivity.class);
//                        intent.putExtra(VideoActivity.VIDEO_PATH, message.getMediaFilePath());
//                        startActivity(intent);
//                    }
//                } else
//                    if (message.getType() == IMessage.MessageType.RECEIVE_IMAGE.ordinal()
//                            || message.getType() == IMessage.MessageType.SEND_IMAGE.ordinal()) {
//                        Intent intent = new Intent(IMChatActivity.this, BrowserImageActivity.class);
//                        intent.putExtra("msgId", message.getMsgId());
//                        intent.putStringArrayListExtra("pathList", mPathList);
//                        intent.putStringArrayListExtra("idList", mMsgIdList);
//                        startActivity(intent);
//                    }
//                else {
//                    Toast.makeText(getApplicationContext(),
//                            getApplicationContext().getString(R.string.message_click_hint),
//                            Toast.LENGTH_SHORT).show();
//                }
            }
        });

        mAdapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>()

        {
            @Override
            public void onMessageLongClick(View view, MyMessage message) {
//                Toast.makeText(getApplicationContext(),
//                        getApplicationContext().getString(R.string.message_long_click_hint),
//                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>()

        {
            @Override
            public void onAvatarClick(MyMessage message) {
//                Toast.makeText(getApplicationContext(),
//                        getApplicationContext().getString(R.string.avatar_click_hint),
//                        Toast.LENGTH_SHORT).show();
                // do something
            }
        });

        mAdapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener<MyMessage>()

        {
            @Override
            public void onStatusViewClick(final MyMessage message) {
                if (message.getMessageStatus() == IMessage.MessageStatus.SEND_FAILED) {
                    Message msg = message.getMessage();
                    JMessageClient.sendMessage(msg);
                    msg.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            mAdapter.updateMessage(message);
                        }
                    });
                }
            }
        });

        PullToRefreshLayout layout = mChatView.getPtrLayout();
        layout.setPtrHandler(new

                                     PtrHandler() {
                                         @Override
                                         public void onRefreshBegin(PullToRefreshLayout layout) {
                                             Log.i("MessageListActivity", "Loading next page");
                                             loadNextPage();
                                             layout.refreshComplete();
                                         }
                                     });

        mChatView.setAdapter(mAdapter);
        mAdapter.addToEnd(mData);

        handleImageMessage(mData);

        scrollToBottom();
    }

    private void handleImageMessage(List<MyMessage> mData) {
        for (MyMessage myMessage : mData) {
            handleImageMessage(myMessage);
        }
    }

    private void handleImageMessage(final MyMessage myMessage) {
        mMsgIdList.add(myMessage.getMsgId());
        if (myMessage.getMessage().getContentType() == ContentType.image) {
            final ImageContent imgContent = (ImageContent) myMessage.getMessage().getContent();
            // 先拿本地缩略图
            final String path = myMessage.getMediaFilePath();
            if (path == null) {
                //从服务器上拿缩略图
                imgContent.downloadThumbnailImage(myMessage.getMessage(), new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int status, String desc, File file) {
                        if (status == 0) {
                            myMessage.setMediaFilePath(file.getPath());
                            mAdapter.updateMessage(myMessage);
                        }
                        //从服务器上拿原图
                        imgContent.downloadOriginImage(myMessage.getMessage(), new DownloadCompletionCallback() {
                            @Override
                            public void onComplete(int status, String desc, File file) {
                                if (status == 0) {
                                    mPathList.add(file.getPath());
                                    myMessage.setMediaFilePath(file.getPath());
                                    myMessage.setOriginImagePath(file.getPath());
                                    mAdapter.updateMessage(myMessage);
                                }
                            }
                        });
                    }
                });
            } else {
                mPathList.add(path);
            }
        }
    }

    private void loadNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mConv != null) {
                    List<Message> msgList = mConv.getMessagesFromNewest(mStart, PAGE_MESSAGE_COUNT);
                    if (msgList != null) {
                        for (Message msg : msgList) {
                            MyMessage MyMessage = new MyMessage(msg);
                            msg.setHaveRead(new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {

                                }
                            });
                            mData.add(0, MyMessage);
                        }
                        if (msgList.size() > 0) {
//                            checkSendingImgMsg();
                            mOffset = msgList.size();
                        } else {
                            mOffset = 0;
                        }
                        mStart += mOffset;
                        mAdapter.addToEnd(mData.subList(0, msgList.size()));
                        handleImageMessage(mData.subList(0, msgList.size()));
                    }
                }
            }
        }, 1000);
    }

    private void scrollToBottom() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mChatView.getMessageListView().smoothScrollToPosition(0);
            }
        }, 200);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ChatInputView chatInputView = mChatView.getChatInputView();
                if (chatInputView.getMenuState() == View.VISIBLE) {
                    chatInputView.dismissMenuLayout();
                }
                try {
                    View v = getCurrentFocus();
                    if (mImm != null && v != null) {
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        view.clearFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
        }
        return false;
    }

    /**
     * 接收消息类事件
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        final Message msg = event.getMessage();
        //若为群聊相关事件，如添加、删除群成员
        Log.i(TAG, event.getMessage().toString());
        //刷新消息
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //收到消息的类型为单聊
                if (msg.getTargetType() == ConversationType.single) {
                    UserInfo userInfo = (UserInfo) msg.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    String appKey = userInfo.getAppKey();
                    //判断消息是否在当前会话中
                    if (targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                        msg.setHaveRead(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {

                            }
                        });
                        final MyMessage myMessage = new MyMessage(msg);
                        mMsgList.add(msg);
                        mData.add(myMessage);
                        mAdapter.addToStart(myMessage, true);
                        Log.i(TAG, "Receiving msg! " + msg);
                        handleImageMessage(myMessage);
                    }
                }
            }
        });
    }

    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        mAdapter.delete(new MyMessage(retractedMessage));
    }

    /**
     * 当在聊天界面断网再次连接时收离线事件刷新
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (conv.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            String targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                List<Message> singleOfflineMsgList = event.getOfflineMessageList();
                if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                    for (Message msg : singleOfflineMsgList) {
                        MyMessage myMessage = new MyMessage(msg);
                        msg.setHaveRead(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {

                            }
                        });
                        mMsgList.add(msg);
                        mData.add(myMessage);
                    }
                    mAdapter.addToEnd(mData.subList(0, singleOfflineMsgList.size()));
                    handleImageMessage(mData.subList(0, singleOfflineMsgList.size()));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (mConv != null) {
                mConv.resetUnreadCount();
            }
            JMessageClient.exitConversation();
            unregisterReceiver(mReceiver);
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(this);
            }
            JMessageClient.unRegisterEventReceiver(this);
            EventManger.getDefault().postMessageRefreshEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_im_chat;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    String path = "";
                    if (selectList.size() != 0) {
                        LocalMedia media = selectList.get(0);
                        if (media.isCompressed()) {
                            Logger.i("原图已压缩");
                            path = media.getCompressPath();
                        } else if (media.isCut()) {
                            Logger.i("原图已裁剪");
                            path = media.getCutPath();
                        } else {
                            Logger.i("原图");
                            path = media.getPath();
                        }
                        Logger.i(path);
                        Bitmap bitmap = BitmapLoader.getBitmapFromFile(path, 720, 1280);
                        final String finalPath = path;
                        ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                            @Override
                            public void gotResult(int status, String desc, ImageContent imageContent) {
                                if (status == 0) {
                                    Message msg = mConv.createSendMessage(imageContent);
                                    JMessageClient.sendMessage(msg);
                                    final MyMessage MyMessage = new MyMessage(msg);
                                    MyMessage.setMediaFilePath(finalPath);
                                    MyMessage.setOriginImagePath(finalPath);
                                    IMChatActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.addToStart(MyMessage, true);
                                        }
                                    });
                                    msg.setOnContentUploadProgressCallback(new ProgressUpdateCallback() {
                                        @Override
                                        public void onProgressUpdate(double v) {
                                            MyMessage.setProgress(Math.ceil(v * 100) + "%");
                                            Log.w(TAG, "Uploading image progress" + Math.ceil(v * 100) + "%");
                                            mAdapter.updateMessage(MyMessage);
                                        }
                                    });
                                    msg.setOnSendCompleteCallback(new BasicCallback() {
                                        @Override
                                        public void gotResult(int status, String desc) {
                                            mAdapter.updateMessage(MyMessage);
                                            if (status == 0) {
                                                Log.i(TAG, "Send image succeed");
                                            } else {
                                                Log.i(TAG, "Send image failed, " + desc);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                    break;
            }
        }
    }
}
