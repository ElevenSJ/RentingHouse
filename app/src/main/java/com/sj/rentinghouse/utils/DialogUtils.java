package com.sj.rentinghouse.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.orhanobut.logger.Logger;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.activity.HouseDetailActivity;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunj on 2018/7/18.
 */

public class DialogUtils {
    public static AlertDialog showMultiChoiceDialog(final Context mContext, Map<String, String> items, Map<String, String> checkStrs, final OnMapMultiSelectedListener listener) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        final List<String> itemKeyList = new ArrayList<>(items.size());
        final List<String> itemValueList = new ArrayList<>(items.size());
        final boolean[] checkitems = new boolean[items.size()];
        //遍历map中的值
        int index = 0;
        for (Map.Entry<String, String> entry : items.entrySet()) {
            if (checkStrs.containsKey(entry.getKey())) {
                checkitems[index] = true;
            } else {
                checkitems[index] = false;
            }
            itemKeyList.add(entry.getKey());
            itemValueList.add(entry.getValue());
            index++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setMultiChoiceItems(itemValueList.toArray(new String[itemValueList.size()]), checkitems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkitems[i] = b;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Map<String, String> selectedMap = new HashMap<>();
                        for (int q = 0; q < checkitems.length; q++) {
                            if (checkitems[q] == true) {
                                selectedMap.put(itemKeyList.get(q), itemValueList.get(q));
                            } else if (selectedMap.containsKey(itemKeyList.get(q))) {
                                selectedMap.remove(itemKeyList.get(q));
                            }
                        }
                        if (null != listener) {
                            listener.callBack(selectedMap);
                        }
                    }
                }
        );
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }
        );
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showChooseDialog(final Context mContext, Map<String, String> items, final OnMapSelectedListener listener) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        final List<String> itemKeyList = new ArrayList<>(items.size());
        final List<String> itemValueList = new ArrayList<>(items.size());
        //遍历map中的值
        for (Map.Entry<String, String> entry : items.entrySet()) {
            itemKeyList.add(entry.getKey());
            itemValueList.add(entry.getValue());
        }
        builder.setItems(itemValueList.toArray(new String[itemValueList.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.callBack(itemKeyList.get(which), itemValueList.get(which));
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.callBack("", "");
                        }
                    }
                }
        );
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showChooseDialog(final Context mContext, String[] items, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setItems(items, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showViewDialog(final Context mContext, int resId, final ViewInterface listener, final OnSureListener onSureListener) {
        // 获取布局
        final View view = (View) View.inflate(mContext, resId, null);
        if (listener != null && resId != 0) {
            listener.getChildView(view, resId);
        }
        // 获取布局中的控件
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Logger.i("AlertDialog setPositiveButton onClick");
                if (onSureListener != null) {
                    onSureListener.callBack(dialog, which);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMShowing(dialog,true);
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().

                setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showEditDialog(final Context mContext, String hint, final OnEditListener listener) {
        // 获取布局
        final View view = (View) View.inflate(mContext, R.layout.dialog_edit, null);
        final EditText editText = view.findViewById(R.id.edt_value);
        editText.setHint(hint);
        // 获取布局中的控件
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setView(view);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.callBack(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showLoginDialog(final Context mContext, final Class loginClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setMessage("请先登录");//提示内容
        builder.setPositiveButton("去登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, loginClass);
                mContext.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showMessageDialog(final Context mContext, String msg, String positiveMsg, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setMessage(msg);//提示内容
        builder.setPositiveButton(TextUtils.isEmpty(positiveMsg) ? "确定" : positiveMsg, listener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onClick(dialog, -100);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showMessageDialog(final Context mContext, String msg, DialogInterface.OnClickListener listener) {
        return showMessageDialog(mContext, msg, null, listener);
    }

    public static AlertDialog showDeleteDialog(final Context mContext, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setMessage("是否确认删除");//提示内容
        builder.setPositiveButton("删除", listener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public static AlertDialog showCallDialog(final Context mContext, final String telNum) {
        return showCallDialog(mContext, null, telNum);
    }


    public static AlertDialog showCallDialog(final Context mContext, final String houseId, final String telNum) {
        return showCallDialog(mContext, telNum, houseId, telNum);
    }

    public static AlertDialog showCallDialog(final Context mContext, final String msg, final String houseId, final String telNum) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        builder.setMessage(msg);//提示内容
        builder.setPositiveButton("去拨号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(houseId)) {
                    EventManger.getDefault().postTrackRefreshEvent();
                    API.addDialingRecord(houseId, null);
                }
                dialog.dismiss();
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telNum));//跳转到拨号界面，同时传递电话号码
                mContext.startActivity(dialIntent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(DisplayUtils.dip2px(mContext, 300), ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public interface OnSureListener {
        void callBack(DialogInterface dialog, int which);
    }

    public interface OnEditListener {
        void callBack(String msg);
    }

    public interface ViewInterface {
        void getChildView(View view, int layoutResId);
    }

    public interface OnMapSelectedListener {
        void callBack(String key, String value);
    }

    public interface OnMapMultiSelectedListener {
        void callBack(Map<String, String> map);
    }


    public static void setMShowing(DialogInterface dialog, boolean mShowing) {
        try {
            Field field = dialog.getClass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, mShowing);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
