package com.sj.rentinghouse.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sj.module_lib.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 创建时间: on 2018/6/19.
 * 创建人: 孙杰
 * 功能描述:
 */
public abstract class AppBaseActivity extends BaseActivity {
    Unbinder unbinder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unbinder = ButterKnife.bind(this);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
