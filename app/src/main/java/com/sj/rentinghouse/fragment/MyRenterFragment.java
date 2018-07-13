package com.sj.rentinghouse.fragment;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.adapter.MyRentRyvAdapter;
import com.sj.rentinghouse.base.AppBaseFragment;
import com.sj.rentinghouse.bean.MyItem;
import com.sj.rentinghouse.events.MyPageSwitchEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.img_top_right)
    TextView tvTopTitleRight;

    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;
    MyRentRyvAdapter mAdapter;
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
        tvTopTitleRight.setVisibility(View.VISIBLE);
        tvTopTitleRight.setText("切换为房东");
        Drawable drawable = getResources().getDrawable(R.drawable.img_top_title_switch);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        tvTopTitleRight.setCompoundDrawables(drawable,null,null,null);
        tvTopTitle.setText("个人中心");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getHoldingActivity(), LinearLayoutManager.VERTICAL, false);
        DividerDecoration dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray_AD), 1, 16, 16);
        dividerDecoration.setDrawLastItem(false);
        rylView.addItemDecoration(dividerDecoration);
        mAdapter = new MyRentRyvAdapter(getHoldingActivity());
        rylView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ToastUtils.showShortToast(mAdapter.getItem(position).getItemName());
            }
        });
        rylView.setLayoutManager(layoutManager);

        initData();
    }

    private void initData() {
        mAdapter.add(new MyItem(R.drawable.img_my_custom,"客户咨询"));
        mAdapter.add(new MyItem(R.drawable.img_my_suggest,"投诉建议"));
        mAdapter.add(new MyItem(R.drawable.img_my_contact_us,"联系我们","400-1234-6789"));
        mAdapter.add(new MyItem(R.drawable.img_my_suggest,"账号设置"));
    }

    @OnClick(R.id.img_top_right)
    public void onClickView(){
        EventBus.getDefault().post(new MyPageSwitchEvent(1));
    }
}
