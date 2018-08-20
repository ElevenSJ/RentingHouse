package com.sj.rentinghouse.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.SPUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.HouseRyvAdapter;
import com.sj.rentinghouse.adapter.SearchWordAdapter;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.DataList;
import com.sj.rentinghouse.bean.HotWord;
import com.sj.rentinghouse.bean.HouseInfo;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.utils.NameSpace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/16.
 */

public class HotWordActivity extends AppBaseActivity implements  RecyclerArrayAdapter.OnMoreListener {

    @BindView(R.id.tv_top_title)
    EditText tvTopTitle;
    @BindView(R.id.img_delete)
    ImageView imgDeleteEt;
    @BindView(R.id.img_top_right)
    TextView imgTopRight;
    @BindView(R.id.layout_flex)
    FlexboxLayout layoutFlex;
    @BindView(R.id.ryl_view)
    EasyRecyclerView rylView;

    SearchWordAdapter mAdapter;
    HouseRyvAdapter mHouseAdapter;

    String word;
    @BindView(R.id.tv_clear_history)
    TextView tvClearHistory;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;

    private List<HotWord> hotWords;

    private List<String> searchWordList = new ArrayList<>();

    private final String SEPARATOR = "@#";

    DividerDecoration dividerDecoration;

    int pageNum = 1;
    String nextFirstIndex = "-1";

    Map<String, Object> map = new ArrayMap<>();

    Handler hander = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!TextUtils.isEmpty(word)) {
                if (!searchWordList.contains(word)) {
                    searchWordList.add(word);
                }
            }
            SPUtils.getInstance().commit(NameSpace.SEARCH_WORD, TextUtils.join(SEPARATOR, searchWordList.toArray()));
            map.put("village", word);
            onRefresh();
        }
    };

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        map.put("city", SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, ""));
    }

    @Override
    public int getContentView() {
        return R.layout.activity_hot_word;
    }

    @Override
    public void initEvent() {
        super.initEvent();
        imgTopRight.setText("取消");
        imgTopRight.setVisibility(View.VISIBLE);
        tvTopTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hander.removeCallbacks(runnable);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                word = editable.toString();
                if (!TextUtils.isEmpty(word)) {
                    imgDeleteEt.setVisibility(View.VISIBLE);
                    hander.postDelayed(runnable, 500);
                } else {
                    imgDeleteEt.setVisibility(View.INVISIBLE);
                    showDefault();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rylView.setLayoutManager(layoutManager);
        rylView.showEmpty();
        dividerDecoration = new DividerDecoration(getResources().getColor(R.color.gray_AD), 1, 0, 0);
        dividerDecoration.setDrawLastItem(true);
        mAdapter = new SearchWordAdapter(this);
        mHouseAdapter = new HouseRyvAdapter(this);
        mHouseAdapter.setMore(R.layout.layout_load_more, this);
        mHouseAdapter.setNoMore(R.layout.layout_load_no_more);

        mAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                tvTopTitle.setText(mAdapter.getItem(position));
            }
        });
        showDefault();
    }

    private void showDefault() {
        tvClearHistory.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.VISIBLE);
        layoutFlex.setVisibility(View.VISIBLE);

        rylView.addItemDecoration(dividerDecoration);
        rylView.setAdapter(mAdapter);
        mAdapter.clear();
        searchWordList.clear();
        layoutFlex.removeAllViews();
        getHotWord();
        getSearchWord();
    }

    private void showResult() {
        tvClearHistory.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        layoutFlex.setVisibility(View.GONE);
        rylView.removeItemDecoration(dividerDecoration);
        rylView.setAdapter(mHouseAdapter);
    }


    private void getSearchWord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String searchWordStr = (String) SPUtils.getInstance().getSharedPreference(NameSpace.SEARCH_WORD, "");
                if (!TextUtils.isEmpty(searchWordStr)) {
                    String[] searchWordStrs = searchWordStr.split(SEPARATOR);
                    for (String str : searchWordStrs) {
                        searchWordList.add(str);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addAll(searchWordList);
                    }
                });
            }
        }).start();
    }

    private void getHotWord() {
        API.hotWord(new ServerResultBack<BaseResponse<DataList<HotWord>>, DataList<HotWord>>() {
            @Override
            public void onSuccess(DataList<HotWord> data) {
                hotWords = data.getData();
                initFlexBox();
            }
        });
    }

    private void initFlexBox() {
        for (HotWord hotWord : hotWords) {
            layoutFlex.addView(createNewFlexItemTextView(hotWord));
        }
    }

    /**
     * 动态创建TextView
     *
     * @return
     */
    private TextView createNewFlexItemTextView(final HotWord hotWord) {
        TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.item_hot_word, null);
        textView.setText(hotWord.getVillage());
        textView.setTag(hotWord.getId());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTopTitle.setText(((TextView) view).getText().toString());
            }
        });
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginTopBottom = DisplayUtils.dip2px(this, 4);
        int marginRight = DisplayUtils.dip2px(this, 16);
        layoutParams.setMargins(0, marginTopBottom, marginRight, marginTopBottom);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    @Override
    protected void onDestroy() {
        String searchWords = TextUtils.join(SEPARATOR, searchWordList.toArray());
        SPUtils.getInstance().apply(NameSpace.SEARCH_WORD, searchWords);
        super.onDestroy();
    }
    @OnClick(R.id.img_top_right)
    public void onTitleRightClick(View view) {
        finish();
    }
    @OnClick(R.id.img_delete)
    public void onDeleteEtClick(View view) {
        tvTopTitle.setText(null);
    }

    @OnClick(R.id.tv_clear_history)
    public void onViewClicked() {
        searchWordList.clear();
        SPUtils.getInstance().commit(NameSpace.SEARCH_WORD, "");
        mAdapter.clear();
    }

    public void onRefresh() {
        pageNum = 1;
        nextFirstIndex="-1";
        getData(map);
    }

    @Override
    public void onMoreShow() {
        if (TextUtils.isEmpty(nextFirstIndex)){
            mHouseAdapter.add(null);
            return;
        }
        getData(map);
}

    private void getData(Map<String, Object> map) {
        showResult();
        API.allHouseList(map, nextFirstIndex, new ServerResultBack<BaseResponse<DataList<HouseInfo>>, DataList<HouseInfo>>() {
            @Override
            public void onSuccess(DataList<HouseInfo> data) {
                if (isDestory()){
                    return;
                }
                nextFirstIndex = data.getNextFirstIndex();
                if (pageNum == 1 && mHouseAdapter.getCount() > 0) {
                    mHouseAdapter.clear();
                }
                mHouseAdapter.addAll(data.getData());
                pageNum++;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (rylView==null||isDestory()){
                    return;
                }
                rylView.setRefreshing(false);
            }
        });
    }
    @Override
    public void onMoreClick() {

    }
}

