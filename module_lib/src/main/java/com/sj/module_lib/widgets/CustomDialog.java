package com.sj.module_lib.widgets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import com.sj.module_lib.R;

/**
 * 创建时间: on 2018/4/1.
 * 创建人: 孙杰
 * 功能描述:progressDialog
 */
public class CustomDialog extends ProgressDialog
{

    private boolean isCancelable = false;
    private boolean isCanceledOnTouchOutside = false;

    public CustomDialog(Context context)
    {
        this(context,R.style.CustomDialog);
    }

    public CustomDialog(Context context, int theme)
    {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext());
    }

    private void init(Context context)
    {
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        this.setCancelable(isCancelable);
        this.setCanceledOnTouchOutside(isCanceledOnTouchOutside);

        setContentView(R.layout.load_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
    }

    @Override
    public void show()
    {
        super.show();
    }

    @Override
    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
    }
}