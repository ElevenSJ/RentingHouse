package com.zaaach.citypicker;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.sj.module_lib.task.SerializeInfoGetTask;
import com.sj.module_lib.utils.FileToolUtils;
import com.sj.module_lib.utils.PinyinUtils;
import com.zaaach.citypicker.adapter.CityListAdapter;
import com.zaaach.citypicker.adapter.InnerListener;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.adapter.decoration.DividerItemDecoration;
import com.zaaach.citypicker.adapter.decoration.SectionItemDecoration;
import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;
import com.zaaach.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */
public class CityPickerDialogJsonFragment extends AppCompatDialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private EditText mSearchBox;
    private TextView mCancelBtn;
    private ImageView mClearAllBtn;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<City> mResults;
    private String assetFile;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnPickListener mOnPickListener;

    Handler hander = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mClearAllBtn.setVisibility(View.VISIBLE);
            //开始数据库查找
            mResults = searchCity(mSearchBox.getText().toString());
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
    };


    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogJsonFragment newInstance(boolean enable) {
        final CityPickerDialogJsonFragment fragment = new CityPickerDialogJsonFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.CityPickerStyle);

        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }
//        initLocatedCity();
    }

    private void initLocatedCity() {
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.FAILURE;
        } else {
            locateState = LocateState.SUCCESS;
        }
    }

    private void initDefaultHotCities() {
        if (mHotCities == null || mHotCities.isEmpty()) {
            mHotCities = new ArrayList<>();
            mHotCities.add(new HotCity("北京", "北京", "110100"));
            mHotCities.add(new HotCity("上海", "上海", "310100"));
            mHotCities.add(new HotCity("广州", "广东", "440100"));
            mHotCities.add(new HotCity("深圳", "广东", "440300"));
            mHotCities.add(new HotCity("天津", "天津", "120100"));
        }
    }

    public void setLocatedCity(LocatedCity location) {
        this.mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
    }

    public void setDefaultHotCities() {
        initDefaultHotCities();
    }

    public void setAnimationStyle(@StyleRes int style) {
        this.mAnimStyle = style <= 0 ? R.style.DefaultCityPickerAnimation : style;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);

        mRecyclerView = mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()), 1);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);

        mIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);

        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);

        mCancelBtn = mContentView.findViewById(R.id.cp_cancel);
        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
        mCancelBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);
        if (mAllCities == null || mAllCities.isEmpty()) {
            mContentView.post(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(assetFile)) {
                        new CityAsyncTask() {
                            @Override
                            protected void onPostExecute(Boolean result) {
                                super.onPostExecute(result);
                                initCityData();
                            }
                        }.execute(assetFile);
                    } else {
                        new CityAsyncTask() {
                            @Override
                            protected void onPostExecute(Boolean result) {
                                super.onPostExecute(result);
                                initCityData();
                            }
                        }.execute("city.json");
                    }
                }
            });
        } else {
            if (mAllCities.get(0) instanceof LocatedCity) {
                mAllCities.remove(0);
                mAllCities.add(0, mLocatedCity);
            }
            if (mAllCities.size() > 1 && mAllCities.get(1) instanceof HotCity) {
                if (mHotCities == null || mHotCities.isEmpty()) {
                    mAllCities.remove(1);
                }
            }
            Collections.sort(mAllCities, new DBManager.CityComparator());
        }

        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return mContentView;
    }

    private void initCityData() {
        if (mAllCities == null) {
            return;
        }
        if (mAllCities.size() > 0 && mAllCities.get(0) instanceof LocatedCity) {
            mAllCities.remove(0);
            mAllCities.add(0, mLocatedCity);
        }
        if (mAllCities.size() > 1 && mAllCities.get(1) instanceof HotCity) {
            if (mHotCities == null || mHotCities.isEmpty()) {
                mAllCities.remove(1);
            }
        }
        mClearAllBtn.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        Collections.sort(mAllCities, new DBManager.CityComparator());
        mResults = mAllCities;
        ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
        mAdapter.updateData(mResults);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
        return dialog;
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        hander.removeCallbacks(runnable);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            if (s.toString().length() > 0) {
                hander.postDelayed(runnable, 1000);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss(-1, null);
        } else if (id == R.id.cp_clear_all) {
            mSearchBox.setText("");
        }
    }

    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    public void locationChanged(LocatedCity location, int state) {
        mAdapter.updateLocateState(location, state);
    }

    @Override
    public void dismiss(int position, City data) {
        dismiss();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }

    public void setAllCities(List<City> allCities) {
        this.mAllCities = allCities;
    }

    public void setAssetFile(String assetFileName) {
        this.assetFile = assetFileName;
    }

    public class CityAsyncTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("CityAsyncTask", "开始读取城市数据");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Map<String, Map<String, String>> allCityMap = (Map<String, Map<String, String>>) JSON.parse(FileToolUtils.ReadAssetsString(CityPickerDialogJsonFragment.this.getActivity().getApplicationContext(), "city.json"));
            Map<String, String> provinceMap = allCityMap.get("1");
            if (mAllCities == null) {
                mAllCities = new ArrayList<>();
            }
            //遍历map中的值
            for (Map.Entry<String, String> province : provinceMap.entrySet()) {
                Map<String, String> cities = allCityMap.get(province.getKey());
                for (Map.Entry<String, String> city : cities.entrySet()) {
                    mAllCities.add(new City(city.getValue(), province.getValue(), PinyinUtils.getPingYin(city.getValue()), city.getKey()));
                }
            }
            Collections.sort(mAllCities, new DBManager.CityComparator());
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Log.i("CityAsyncTask", "读取城市数据结束");
                for (City city : mAllCities) {
                    Log.i("CityAsyncTask", "城市：" + JSON.toJSONString(city));
                }
            }
        }
    }

    public synchronized List<City> searchCity(String keyWord) {
        List<City> searchCity = new ArrayList<>();
        for (City city : mAllCities) {
            if (city.getName().contains(keyWord) || city.getPinyin().contains(keyWord)) {
                searchCity.add(city);
            }
        }
        Log.i("CityAsyncTask", "搜索城市：" + searchCity == null ? "未搜索到" : "搜索到" + JSON.toJSONString(searchCity));
        return searchCity;
    }
}
