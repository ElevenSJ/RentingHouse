package com.sj.rentinghouse.base;

import com.sj.module_lib.base.BaseFragment;
import com.sj.rentinghouse.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Sunj on 2018/7/8.
 */

public abstract class AppBaseFragment extends BaseFragment {
    Unbinder unbinder;
    @Override
    public void bindView() {
        super.bindView();
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
