package com.sj.module_lib.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sj.module_lib.R;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.StatusBarUtils;
import com.sj.module_lib.utils.ViewManager;
import com.sj.module_lib.utils.ViewUtils;


/**
 * <p>Fragment的基类</p>
 *
 * @author 张华洋
 * @name FragmentBase
 */
@Keep
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mActivity;
    public View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createInit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getContentView(), container, false);
        bindView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        view.post(new Runnable() {
            @Override
            public void run() {
                initEvent();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }
    /**
     * 获取宿主Activity
     *
     * @return Activity
     */
    public BaseActivity getHoldingActivity() {
        return mActivity;
    }

    public abstract int getContentView();
    public void createInit(){}
    public void bindView(){
//        setTopTitlePadding(view,R.id.layout_title);
        setStatusView(view);
    }
    public void init(){}
    public void initEvent(){}

    public void setTopTitlePadding(View view,int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup topTitleView = view.findViewById(resId);
            if (topTitleView != null) {
                ViewUtils.setMargins(topTitleView, 0, StatusBarUtils.getStatusBarHeight(getHoldingActivity()), 0, 0);
            }
        }
    }

    public void setStatusView(View view){
        View emptyView = view.findViewById(R.id.empty_view);
        if (emptyView!=null){
            ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            lp.height = StatusBarUtils.getStatusBarHeight(getHoldingActivity());
            emptyView.setLayoutParams(lp);
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
