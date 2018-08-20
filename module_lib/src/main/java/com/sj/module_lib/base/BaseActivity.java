package com.sj.module_lib.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sj.module_lib.R;
import com.sj.module_lib.utils.StatusBarUtils;
import com.sj.module_lib.utils.ViewUtils;
import com.sj.module_lib.widgets.CustomDialog;


/**
 * 创建时间: on 2018/3/29.
 * 创建人: 孙杰
 * 功能描述:activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    private CustomDialog progressDialog;
    public View rootView;
    boolean isDestory = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(getContentView(), null);
        setContentView(rootView);
        setStatusView();
        init(savedInstanceState);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                initEvent();
            }
        });
//        setStatusView();
//        setTopTitlePadding(R.id.layout_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isDestory = false;
    }

    public void setTopTitle(int resId, String title) {
        View topTitleView = findViewById(resId);
        if (topTitleView != null && topTitleView instanceof TextView) {
            ((TextView) topTitleView).setText(title);
        }
    }

    private void setTopTitlePadding(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup topTitleView = findViewById(resId);
            if (topTitleView != null) {
                ViewUtils.setMargins(topTitleView, 0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (isProgressShowing()) {
            dismissProgress();
        }
        progressDialog = null;
        isDestory = true;
        super.onDestroy();
    }
    public boolean isDestory() {
        return isDestory;
    }
    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new CustomDialog(this);
        }
        if (!isProgressShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgress() {
        if (isProgressShowing()) {
            progressDialog.dismiss();
        }
    }

    public boolean isProgressShowing() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else {
            return false;
        }
    }

    public abstract int getContentView();

    public void init(Bundle savedInstanceState) {
    }

    public void initEvent() {
    }

    public void setStatusView() {
        View emptyView = findViewById(R.id.empty_view);
        if (emptyView != null) {
            ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            lp.height = StatusBarUtils.getStatusBarHeight(this);
            emptyView.setLayoutParams(lp);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
