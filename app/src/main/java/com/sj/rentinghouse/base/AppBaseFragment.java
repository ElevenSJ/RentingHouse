package com.sj.rentinghouse.base;

import android.support.v4.app.Fragment;

import com.sj.module_lib.base.BaseFragment;
import com.sj.rentinghouse.R;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

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
    public void initEvent() {
        super.initEvent();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        try
        {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
