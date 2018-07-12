package com.sj.rentinghouse.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseFragment;

import butterknife.BindView;

/**
 * Created by Sunj on 2018/7/8.
 * 我的（租客页）
 */

public class MyRenterFragment extends AppBaseFragment {

    @BindView(R.id.layout_title)
    View view;
    @BindView(R.id.img_top_left)
    ImageView imgTopLeft;
    @BindView(R.id.tv_top_title)
    TextView tvTopTitle;

    public static MyRenterFragment newInstance() {
        MyRenterFragment f = new MyRenterFragment();
        return f;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_my_renter;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        view.setBackgroundResource(R.color.transparent);
        imgTopLeft.setVisibility(View.INVISIBLE);
        tvTopTitle.setText("个人中心");
    }
}
