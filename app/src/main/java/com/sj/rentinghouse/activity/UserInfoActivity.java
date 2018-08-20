package com.sj.rentinghouse.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.MyRentRyvAdapter;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.MyItem;
import com.sj.rentinghouse.bean.UserInfo;
import com.sj.rentinghouse.events.UserInfoEvent;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.http.UrlConfig;
import com.sj.rentinghouse.utils.DialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/19.
 */

public class UserInfoActivity extends AppBaseActivity {
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    MyRentRyvAdapter mAdapter;
    List<MyItem> myItemList = new ArrayList<>();
    UserInfo userInfo;
    @BindView(R.id.img_top_right)
    TextView imgTopRight;

    EditText etCardName;
    EditText etCardId;

    EditText etNewPhone;
    EditText etCode;
    TextView getCode;

    private List<LocalMedia> selectList = new ArrayList<>();


    /**
     * 倒计时60秒，一次1秒
     */
    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            getCode.setEnabled(false);
            getCode.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            getCode.setEnabled(true);
            getCode.setText("获取验证码");
        }
    };
    public void getCode(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum) || phoneNum.length() != 11) {
            ToastUtils.showShortToast("手机号码不正确");
            return;
        }
        refreshCodeTxt(true);
        API.getSMSCode(phoneNum,"4", new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                ToastUtils.showShortToast(message);
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                if (isDestory()){
                    return;
                }
                refreshCodeTxt(false);
            }
        });
    }
    public void refreshCodeTxt(boolean refresh) {
        if (refresh) {
            timer.start();
        } else {
            timer.cancel();
            timer.onFinish();
        }
    }


    @Override
    public int getContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        userInfo = getIntent().getParcelableExtra("data");
//        imgTopRight.setText("保存");
    }

    @Override
    public void initEvent() {
        super.initEvent();
        setTopTitle(R.id.tv_top_title, "个人中心");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray_AD), 1, 0, 0);
        dividerDecoration.setDrawLastItem(true);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new MyRentRyvAdapter(this);
        rylView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (userInfo == null) {
                    return;
                }
                switch (position) {
                    case 0:
                        toSelectedImage();
                        break;
                    case 1:
                        showEditDialog(position, userInfo.getNickname());
                        break;
                    case 2:
                        DialogUtils.showViewDialog(UserInfoActivity.this, R.layout.dialog_update_user_phone, new DialogUtils.ViewInterface() {
                            @Override
                            public void getChildView(View view, int layoutResId) {
                                etNewPhone = view.findViewById(R.id.edt_phone_value);
                                etCode = view.findViewById(R.id.edt_code_value);
                                getCode= view.findViewById(R.id.tv_getcode);
                                getCode.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getCode(etNewPhone.getText().toString().trim());
                                    }
                                });
                            }
                        }, new DialogUtils.OnSureListener() {
                            @Override
                            public void callBack(final DialogInterface dialog, int which) {
                                DialogUtils.setMShowing(dialog,false);
                                if (checkPhoneAllEt()) {
                                    final String newPhone = etNewPhone.getText().toString().trim();
                                    final String msgCode = etCode.getText().toString().trim();
                                    Map<String, Object> map = new ArrayMap<>();
                                    map.put("newPhone", newPhone);
                                    map.put("msgCode", msgCode);
                                    showProgress();
                                    API.updateUserInfo(map, new CommonCallback() {
                                        @Override
                                        public void onSuccess(String message) {
                                            userInfo.setUserId(newPhone);
                                            EventBus.getDefault().post(new UserInfoEvent(userInfo));
                                            if (isDestory()){
                                                return;
                                            }
                                            DialogUtils.setMShowing(dialog,true);
                                            dialog.dismiss();
                                            myItemList.get(2).setItemRemark(newPhone);
                                            mAdapter.update(myItemList.get(2), 2);

                                        }

                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            if (isDestory()){
                                                return;
                                            }
                                            refreshCodeTxt(false);
                                            dismissProgress();
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case 3:
                        showChooseDialog(position, new String[]{"男", "女"});
                        break;
                    case 4:
                        showEditDialog(position, userInfo.getEmail());
                        break;
                    case 5:
//                    case 6:
                        DialogUtils.showViewDialog(UserInfoActivity.this, R.layout.dialog_update_user_card, new DialogUtils.ViewInterface() {
                            @Override
                            public void getChildView(View view, int layoutResId) {
                                etCardName = view.findViewById(R.id.et_card_name);
                                etCardId = view.findViewById(R.id.et_card_id);
                                etCardName.setText(userInfo.getName());
                                etCardId.setText(userInfo.getIdCard());
                            }
                        }, new DialogUtils.OnSureListener() {
                            @Override
                            public void callBack(final DialogInterface dialog, int which) {
                                DialogUtils.setMShowing(dialog,false);
                                if (checkAllEt()) {
                                    final String cardName = etCardName.getText().toString().trim();
                                    final String cardId = etCardId.getText().toString().trim();
                                    Map<String, Object> map = new ArrayMap<>();
                                    map.put("name", cardName);
                                    map.put("idCard", cardId);
                                    showProgress();
                                    API.updateUserInfo(map, new CommonCallback() {
                                        @Override
                                        public void onSuccess(String message) {
                                            userInfo.setIdCard(cardId);
                                            userInfo.setName(cardName);
                                            EventBus.getDefault().post(new UserInfoEvent(userInfo));
                                            if (isDestory()){
                                                return;
                                            }
                                            DialogUtils.setMShowing(dialog,true);
                                            dialog.dismiss();
//                                            myItemList.get(5).setItemRemark(cardName);
                                            myItemList.get(5).setItemRemark(cardId);
                                            mAdapter.update(myItemList.get(5), 5);
//                                            mAdapter.update(myItemList.get(6), 6);

                                        }

                                        @Override
                                        public void onFinish() {
                                            super.onFinish();
                                            if (isDestory()){
                                                return;
                                            }
                                            dismissProgress();
                                        }
                                    });
                                }
                            }
                        });
                        break;
                }
            }

        });
        rylView.setLayoutManager(layoutManager);
        initData();
        mAdapter.addAll(myItemList);
    }

    private boolean checkPhoneAllEt() {
        if (TextUtils.isEmpty(etNewPhone.getText().toString())) {
            ToastUtils.showShortToast("请输入手机号码");
            return false;
        } else if (TextUtils.isEmpty(etCode.getText().toString())) {
            ToastUtils.showShortToast("请输入验证码");
            return false;
        }
        return true;
    }
    private boolean checkAllEt() {
        if (TextUtils.isEmpty(etCardName.getText().toString())) {
            ToastUtils.showShortToast("请输入证件姓名");
            return false;
        } else if (TextUtils.isEmpty(etCardId.getText().toString())) {
            ToastUtils.showShortToast("请输入证件号码");
            return false;
        }
        return true;
    }

    private void toSelectedImage() {
        PictureSelector.create(UserInfoActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .theme(R.style.picture_main_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .circleDimmedLayer(true)// 是否圆形裁剪
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .openClickSound(false)// 是否开启点击声音
                .selectionMedia(selectList)// 是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
//                        .videoMaxSecond(15)
//                        .videoMinSecond(10)
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(true) // 裁剪是否可旋转图片
                //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.videoSecond()//显示多少秒以内的视频or音频也可适用
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    private void showChooseDialog(final int index, String[] items) {
        DialogUtils.showChooseDialog(this, items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                userInfo.setSex((i + 1) + "");
                myItemList.get(index).setItemRemark((i + 1) == 1 ? "男" : "女");
                updateUserInfo(index, (i + 1) + "");

            }
        });
    }

    private void showEditDialog(final int index, String hint) {
        DialogUtils.showEditDialog(this, hint, new DialogUtils.OnEditListener() {
            @Override
            public void callBack(String msg) {
                myItemList.get(index).setItemRemark(msg);
                updateUserInfo(index, msg);
            }
        });
    }

    private void updateUserInfo(final int index, final String value) {
        Map<String, Object> map = new ArrayMap<>();
        switch (index) {
            case 0:
                map.put("imgUrl", value);
                break;
            case 1:
                map.put("nickname", value);
                break;
            case 2:
                map.put("newPhone", value);
                break;
            case 3:
                map.put("sex", value);
                break;
            case 4:
                map.put("email", value);
                break;
        }
        showProgress();
        API.updateUserInfo(map, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                switch (index) {
                    case 0:
                        userInfo.setImgUrl(value);
                        break;
                    case 1:
                        userInfo.setNickname(value);
                        break;
                    case 2:
                        break;
                    case 3:
                        userInfo.setSex(value);
                        break;
                    case 4:
                        userInfo.setEmail(value);
                        break;
                }
                EventBus.getDefault().post(new UserInfoEvent(userInfo));
                if (isDestory()){
                    return;
                }
                mAdapter.update(myItemList.get(index), index);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()){
                    return;
                }
                dismissProgress();
            }
        });
    }

    private void updateUserImage(final String path) {
        showProgress();
        API.getQINIUToken(new ServerResultBack<BaseResponse<String>, String>() {
            @Override
            public void onSuccess(String data) {
                UploadManager uploadManager = new UploadManager();
//                String key = userInfo.getUserId()+"_"+System.currentTimeMillis() + ".png";
                uploadManager.put(path, null, data,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject res) {
                                if (info.isOK()) {
                                    try {
                                        String filePath = UrlConfig.QINIU_DOMAIN_URL + res.getString("key");
                                        Logger.i("图片上传成功：" + filePath);
                                        myItemList.get(0).setDrawableRight(filePath);
                                        updateUserInfo(0, filePath);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtils.showShortToast("解析数据失败");
                                        dismissProgress();
                                        Logger.i("获取上传文件名：json解析异常");
                                    }
                                } else {
                                    ToastUtils.showShortToast("图片上传失败");
                                    dismissProgress();
                                }
                                Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);

                            }
                        }, null);
            }

            @Override
            public void onFailed(String error_code, String error_message) {
                super.onFailed(error_code, error_message);
                dismissProgress();
            }
        });

    }

    private void initData() {
        myItemList.add(new MyItem(0, "头像", null, userInfo == null || TextUtils.isEmpty(userInfo.getImgUrl()) ? "R.drawable.img_user_icon" : userInfo.getImgUrl()));
        myItemList.add(new MyItem(0, "昵称", userInfo == null ? "昵称" : userInfo.getNickname()));
        myItemList.add(new MyItem(0, "手机号", userInfo == null ? "手机号" : userInfo.getUserId()));
        myItemList.add(new MyItem(0, "性别", userInfo == null ? "未设置" : userInfo.getSex().equals("0") ? "未设置" : userInfo.getSex().equals("1") ? "男" : "女"));
        myItemList.add(new MyItem(0, "绑定邮箱", userInfo == null ? "邮箱地址" : userInfo.getEmail()));
//        myItemList.add(new MyItem(0, "证件姓名", userInfo == null ? "证件姓名" : userInfo.getName()));
        myItemList.add(new MyItem(0, "证件信息", userInfo == null ? "证件号码" : userInfo.getIdCard()));
    }


    private void updateUserData(UserInfo user) {
        if (user == null) {
            return;
        }
        myItemList.get(0).setDrawableRight(userInfo.getImgUrl());
        myItemList.get(1).setItemRemark(userInfo.getNickname());
        myItemList.get(2).setItemRemark(userInfo.getUserId());
        myItemList.get(3).setItemRemark(userInfo.getSex().equals("0") ? "" : userInfo.getSex().equals("1") ? "男" : "女");
        myItemList.get(4).setItemRemark(userInfo.getEmail());
        myItemList.get(5).setItemRemark(userInfo.getIdCard());
        mAdapter.clear();
        mAdapter.addAll(myItemList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
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
                    }
                    updateUserImage(path);
                    break;
            }
        }
    }

}
