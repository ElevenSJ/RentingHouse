package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UploadManager;
import com.sj.module_lib.http.BaseResponse;
import com.sj.module_lib.http.CommonCallback;
import com.sj.module_lib.http.ServerResultBack;
import com.sj.module_lib.manager.ThreadPoolManager;
import com.sj.module_lib.utils.DateUtils;
import com.sj.module_lib.utils.DisplayUtils;
import com.sj.module_lib.utils.FileToolUtils;
import com.sj.module_lib.utils.SPUtils;
import com.sj.module_lib.utils.TimePickerDialog;
import com.sj.module_lib.utils.ToastUtils;
import com.sj.module_lib.widgets.FullyGridLayoutManager;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.adapter.GridImageAdapter;
import com.sj.rentinghouse.app.App;
import com.sj.rentinghouse.base.AppBaseActivity;
import com.sj.rentinghouse.bean.CityInfo;
import com.sj.rentinghouse.bean.HouseDetail;
import com.sj.rentinghouse.events.EventManger;
import com.sj.rentinghouse.http.API;
import com.sj.rentinghouse.http.UrlConfig;
import com.sj.rentinghouse.utils.CityPopWindow;
import com.sj.rentinghouse.utils.DialogUtils;
import com.sj.rentinghouse.utils.NameSpace;
import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/7/21.
 */

public class AddOrUpdateHouseActivity extends AppBaseActivity implements TimePickerDialog.TimePickerDialogInterface {
    @BindView(R.id.layout_head)
    View headView;
    @BindView(R.id.tv_house_name)
    TextView tvHouseName;
    @BindView(R.id.tv_rent_time)
    TextView tvRentTime;
    @BindView(R.id.tv_unused_time)
    TextView tvUnusedTime;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.edt_building_no)
    EditText edtBuildingNo;
    @BindView(R.id.edt_room_no)
    EditText edtRoomNo;
    @BindView(R.id.edt_room_num)
    EditText edtRoomNum;
    @BindView(R.id.edt_toilet_num)
    EditText edtToiletNum;
    @BindView(R.id.edt_hall_num)
    EditText edtHallNum;
    @BindView(R.id.sp_direction)
    Spinner spDirection;
    @BindView(R.id.edt_rent_price)
    EditText edtRentPrice;
    @BindView(R.id.sp_pay_method)
    Spinner spPayMethod;
    @BindView(R.id.edt_floor)
    EditText edtFloor;
    @BindView(R.id.edt_all_floor)
    EditText edtAllFloor;
    @BindView(R.id.sp_renovation)
    Spinner spRenovation;
    @BindView(R.id.edt_area)
    EditText edtArea;
    @BindView(R.id.edt_note)
    EditText edtNote;
    @BindView(R.id.edt_requirement)
    EditText edtRequirement;
    @BindView(R.id.layout_flex)
    GridLayout layoutFlex;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.radioGroup1)
    RadioGroup bedRommTypeGroup;
    @BindView(R.id.rd_whole_rent)
    RadioButton rdWholeRent;
    @BindView(R.id.rd_join_rent)
    RadioButton rdJoinRent;
    @BindView(R.id.rd_master_bedroom)
    RadioButton rdMasterBedroom;
    @BindView(R.id.rd_sub_bedroom)
    RadioButton rdSubBedroom;

    private List<LocalMedia> selectList = new ArrayList<>();
    private List<LocalMedia> needCommitImageList = new ArrayList<>();
    private GridImageAdapter adapter;
    private int maxSelectNum = 9;
    Map<String, Object> facilityMap = new ArrayMap<>(16);

    HouseDetail houseDetail;
    int type = 1;//默认合租
    int houseType = 1;//默认主卧
//    String housePics = "";
    String cityCode = "";
    String cityName = "";
    String districtCode = "";
    String districtName = "";

    List<HotCity> hotCities = new ArrayList<>();
    List<CityInfo> cityInfos = null;
    private List<UploadImage> uploadList = new ArrayList<>();
    private List<String> uploadImagePath = new ArrayList<>();
    private TimePickerDialog mTimePickerDialog;
    StringBuffer rentTimeBuffer = new StringBuffer();
    private int chooseNum = 0;

    @Override
    public void positiveListener() {
        int year = mTimePickerDialog.getYear();
        int month = mTimePickerDialog.getMonth();
        int day = mTimePickerDialog.getDay();
        Logger.i("=======year======" + mTimePickerDialog.getYear());
        Logger.i("=======getMonth======" + mTimePickerDialog.getMonth());
        Logger.i("=======getDay======" + mTimePickerDialog.getDay());
        if (chooseNum == 0) {
            rentTimeBuffer.setLength(0);
            rentTimeBuffer.append(year + "-" + month + "-" + day + "-");
            ToastUtils.showLongToast("选择租期结束时间");
            mTimePickerDialog.showDatePickerDialog();
        } else if (chooseNum == 1) {
            rentTimeBuffer.append(year + "-" + month + "-" + day);
            chooseNum = 0;
            tvRentTime.setText(rentTimeBuffer.toString());
        }
        chooseNum++;
    }

    @Override
    public void negativeListener() {
        if (chooseNum >= 1) {
            chooseNum = 0;
            rentTimeBuffer.setLength(0);
        }
    }

    public class UploadImage {
        String path;
        boolean isSuccess;

        public UploadImage(boolean isSuccess, String imagePath) {
            this.isSuccess = isSuccess;
            this.path = imagePath;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean success) {
            isSuccess = success;
        }
    }

    UploadImageListener uploadImageListener = new UploadImageListener() {
        @Override
        public void complete(boolean isSuccess, String imagePath) {
            uploadList.add(new UploadImage(isSuccess, imagePath));
            if (isSuccess){
                uploadImagePath.add(imagePath);
            }
            if (uploadList.size() == needCommitImageList.size()) {
                Logger.i("图片全部上传完成");
                boolean isAllOk = true;
                for (UploadImage uploadImage : uploadList) {
                    Logger.i("结果:" + uploadImage.isSuccess() + "," + uploadImage.getPath());
                    if (!uploadImage.isSuccess()) {
                        isAllOk = false;
                        break;
                    }
                }
                if (isAllOk) {
                    Logger.i("图片全部上传完成:任务全部成功");
                } else {
                    Logger.i("图片全部上传完成:任务有失败");
                }
                uploadList.clear();
                needCommitImageList.clear();
                saveHouse();
            }
        }
    };

    @Override
    public int getContentView() {
        return R.layout.activity_add_or_update_house;
    }

    @Override
    public void init() {
        super.init();
        cityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, "");
        cityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, "");
        houseDetail = getIntent().getParcelableExtra("data") == null ? new HouseDetail() : (HouseDetail) getIntent().getParcelableExtra("data");
        setTopTitle(R.id.tv_top_title, TextUtils.isEmpty(houseDetail.getId())?"添加房产":"修改房产");
        mTimePickerDialog = new TimePickerDialog(AddOrUpdateHouseActivity.this);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        if (!TextUtils.isEmpty(houseDetail.getId())) {
            initViewData();
        }
        for (int i = 0; i < App.facilityArray.length; i++) {
            createNewFlexItemView(i);
        }
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(AddOrUpdateHouseActivity.this).themeStyle(R.style.picture_main_style).openExternalPreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(AddOrUpdateHouseActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(AddOrUpdateHouseActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });

    }

    @OnClick({R.id.rd_whole_rent, R.id.rd_join_rent, R.id.tv_area, R.id.bt_save, R.id.rd_master_bedroom, R.id.rd_sub_bedroom, R.id.layout_head})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_head:
//                ToastUtils.showLongToast("选择租期开始时间");
//                mTimePickerDialog.showDatePickerDialog();
                break;
            case R.id.rd_whole_rent:
                type = 1;
                bedRommTypeGroup.setVisibility(View.GONE);
                break;
            case R.id.rd_join_rent:
                type = 2;
                bedRommTypeGroup.setVisibility(View.VISIBLE);
                break;
            case R.id.rd_master_bedroom:
                houseType = 1;
                break;
            case R.id.rd_sub_bedroom:
                houseType = 2;
                break;
            case R.id.tv_area:
//                toOpenCityList();
                CityPopWindow.getDefault(this).init().initCityData(cityName).initDistrictData(districtName).setOnAdapterItemClickListener(new CityPopWindow.OnAdapterItemClickListener() {
                    @Override
                    public void onItemSelected(CityInfo cityInfo, CityInfo cityInfo1) {
                        if (cityInfo != null && cityInfo1 != null) {
                            cityName = cityInfo.getName();
                            cityCode = cityInfo.getCode();
                            districtName = cityInfo1.getName();
                            districtCode = cityInfo1.getCode();
                        } else {
                            cityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_CODE, "");
                            cityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.CITY_NAME, "");
                            districtName = "";
                            districtCode = "";
                        }
                        tvArea.setText(cityName+ " " + districtName);
                    }
                }).show(findViewById(R.id.layout_title));
                break;
            case R.id.bt_save:
                updateImages();
                break;
        }
    }

    private void toOpenCityList() {
        String locatedCityName = (String) SPUtils.getInstance().getSharedPreference(NameSpace.LOCAL_CITY_NAME, "");
        String locatedCityCode = (String) SPUtils.getInstance().getSharedPreference(NameSpace.LOCAL_CITY_CODE, "");
        final LocatedCity locatedCity = new LocatedCity(locatedCityName, locatedCityName, locatedCityCode);
        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())
                .enableAnimation(true)
                .setAnimationStyle(R.style.DefaultCityPickerAnimation).setLocatedCity(locatedCity).setAllCities(App.allCities).setOnPickListener(new OnPickListener() {
            @Override
            public void onPick(int position, final City data) {
                if (data != null) {
                    cityName = data.getName();
                    cityCode = data.getCode();
                    tvArea.setText(data.getName());
                    Logger.i("选择城市：" + data.getName() + "," + data.getCode());
                    DialogUtils.showChooseDialog(AddOrUpdateHouseActivity.this, App.allCityMap.get(data.getCode()), new DialogUtils.OnMapSelectedListener() {
                        @Override
                        public void callBack(String key, String value) {
                            Logger.i("选择区县：" + value + "," + key);
                            districtCode = key;
                            districtName = value;
                            tvArea.setText(data.getName() + " " + districtName);
                        }
                    });
                }
            }

            @Override
            public void onLocate() {
            }
        }).jsonShow();
    }

    /**
     * 动态创建TextView
     *
     * @return
     */
    private void createNewFlexItemView(final int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_house_facility, null);
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(facilityMap.get(App.facilityKeyArray[index]) != null && facilityMap.get(App.facilityKeyArray[index]).equals("1"));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                facilityMap.put(App.facilityKeyArray[index], b ? "1" : "0");
            }
        });
        TextView textView = view.findViewById(R.id.tv_item);
        textView.setText(App.facilityArray[index]);
        Drawable drawable = getResources().getDrawable(App.facilitiesResIds[index]);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
        textView.setCompoundDrawables(null, drawable, null, null);
        GridLayout.Spec rowSpec = GridLayout.spec(index / 6, 1f);
        GridLayout.Spec columnSpec = GridLayout.spec(index % 6, 1f);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
        layoutParams.setGravity(Gravity.CENTER);
        int margin = DisplayUtils.dip2px(this, 2);
        if (index / 6 == 1) {
            layoutParams.topMargin = margin;
            layoutParams.bottomMargin = margin;
        }
        layoutFlex.addView(view, layoutParams);
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(AddOrUpdateHouseActivity.this)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_main_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(maxSelectNum)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .enableCrop(false)// 是否裁剪
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .isGif(false)// 是否显示gif图片
                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .selectionMedia(selectList)// 是否传入已选图片
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /**
     * 上传图片
     */
    private void updateImages() {
        if (checkParamters()) {
            uploadImagePath.clear();
            needCommitImageList.clear();
            uploadList.clear();
            for (LocalMedia media : selectList) {
                if (!media.getPath().startsWith("http")) {
                    needCommitImageList.add(media);
                } else {
                    uploadImagePath.add(media.getPath());
                }
            }
            showProgress();
            if (!needCommitImageList.isEmpty()) {
                API.getQINIUToken(new ServerResultBack<BaseResponse<String>, String>() {
                    @Override
                    public void onSuccess(String data) {
                        for (LocalMedia media : needCommitImageList) {
                            ThreadPoolManager.getInstance().execute(new UploadTask(data, media, uploadImageListener));
                        }
                    }

                    @Override
                    public void onFailed(String error_code, String error_message) {
                        super.onFailed(error_code, error_message);
                        dismissProgress();
                    }
                });
            } else {
                saveHouse();
            }
        }
    }

    private void initViewData() {
        headView.setVisibility(View.VISIBLE);
        tvHouseName.setText(houseDetail.getVillage()+(TextUtils.isEmpty(houseDetail.getBedroom())?"":" · "+houseDetail.getBedroom()+"居室"));
        tvRentTime.setText(houseDetail.getStartEndTime());
//        try {
//            tvRentTime.setText(TextUtils.isEmpty(houseDetail.getStartTime())||TextUtils.isEmpty(houseDetail.getEndTime()) ? "" : DateUtils.formatDate(Long.valueOf(houseDetail.getStartTime())) + "至" + DateUtils.formatDate(Long.valueOf(houseDetail.getEndTime())));
//        }catch (Exception e){
//            tvRentTime.setText("");
//        }
        tvUnusedTime.setText(houseDetail.getUnusedTime());
        if (!TextUtils.isEmpty(houseDetail.getType()) && houseDetail.getType().equals("2")) {
            type = 2;
            rdJoinRent.setChecked(true);
            bedRommTypeGroup.setVisibility(View.VISIBLE);
        }
        if (type == 2) {
            if (!TextUtils.isEmpty(houseDetail.getHouseType()) && houseDetail.getHouseType().equals("2")) {
                houseType = 2;
                rdSubBedroom.setChecked(true);
            }
        }
        cityName = houseDetail.getCityName();
        cityCode = houseDetail.getCity();
        districtCode = TextUtils.isEmpty(houseDetail.getDistrictCode()) ? "" : houseDetail.getDistrictCode();
        districtName = TextUtils.isEmpty(houseDetail.getDistrictName()) ? "" : houseDetail.getDistrictName();
        tvArea.setText(cityName + " " + districtName);
        edtName.setText(houseDetail.getVillage());
        edtBuildingNo.setText(houseDetail.getBuildingNo());
        edtRoomNo.setText(houseDetail.getRoomNo());
        edtRoomNum.setText(houseDetail.getBedroom());
        edtHallNum.setText(houseDetail.getParlour());
        edtToiletNum.setText(houseDetail.getToilet());
        try {
            spDirection.setSelection(Integer.valueOf(houseDetail.getDirection()) - 1, true);
        } catch (NumberFormatException e) {
            Logger.e("房源朝向转型异常");
        }
        edtRentPrice.setText(houseDetail.getRent());
        try {
            spPayMethod.setSelection(Integer.valueOf(houseDetail.getPayMethod()) - 1, true);
        } catch (NumberFormatException e) {
            Logger.e("房源付款方式转型异常");
        }
        edtFloor.setText(houseDetail.getFloor());
        edtAllFloor.setText(houseDetail.getTotalFloor());
        try {
            spRenovation.setSelection(Integer.valueOf(houseDetail.getRenovation()) - 1, true);
        } catch (NumberFormatException e) {
            Logger.e("房源装修方式转型异常");
        }
        edtArea.setText(houseDetail.getArea());
        edtNote.setText(houseDetail.getHouseDescription());
        edtRequirement.setText(houseDetail.getRentingRequirements());

        if (!TextUtils.isEmpty(houseDetail.getBed()) && !houseDetail.getBed().equals("0")) {
            facilityMap.put(App.facilityKeyArray[0], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getWardrobe()) && !houseDetail.getWardrobe().equals("0")) {
            facilityMap.put(App.facilityKeyArray[1], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getDesk()) && !houseDetail.getDesk().equals("0")) {
            facilityMap.put(App.facilityKeyArray[2], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getWifi()) && !houseDetail.getWifi().equals("0")) {
            facilityMap.put(App.facilityKeyArray[3], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getAirConditioner()) && !houseDetail.getAirConditioner().equals("0")) {
            facilityMap.put(App.facilityKeyArray[4], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getWashingMachine()) && !houseDetail.getWashingMachine().equals("0")) {
            facilityMap.put(App.facilityKeyArray[5], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getRefrigerator()) && !houseDetail.getRefrigerator().equals("0")) {
            facilityMap.put(App.facilityKeyArray[6], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getHeater()) && !houseDetail.getHeater().equals("0")) {
            facilityMap.put(App.facilityKeyArray[7], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getTelevision()) && !houseDetail.getTelevision().equals("0")) {
            facilityMap.put(App.facilityKeyArray[8], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getMicrowaveOven()) && !houseDetail.getMicrowaveOven().equals("0")) {
            facilityMap.put(App.facilityKeyArray[9], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getGasStove()) && !houseDetail.getGasStove().equals("0")) {
            facilityMap.put(App.facilityKeyArray[10], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getLampblackMachine()) && !houseDetail.getLampblackMachine().equals("0")) {
            facilityMap.put(App.facilityKeyArray[11], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getSofa()) && !houseDetail.getSofa().equals("0")) {
            facilityMap.put(App.facilityKeyArray[12], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getTeaTable()) && !houseDetail.getTeaTable().equals("0")) {
            facilityMap.put(App.facilityKeyArray[13], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getTolletRoom()) && !houseDetail.getTolletRoom().equals("0")) {
            facilityMap.put(App.facilityKeyArray[14], "1");
        }
        if (!TextUtils.isEmpty(houseDetail.getBalcony()) && !houseDetail.getBalcony().equals("0")) {
            facilityMap.put(App.facilityKeyArray[15], "1");
        }
        for (int i = 0; i < App.facilityArray.length; i++) {
            createNewFlexItemView(i);
        }
        String[] housePictures = TextUtils.isEmpty(houseDetail.getHousePicture())||houseDetail.getHousePicture().equalsIgnoreCase("null") ? null : houseDetail.getHousePicture().split(",");
        if (housePictures!=null&&housePictures.length > 0) {
            for (String path : housePictures) {
                selectList.add(new LocalMedia(path, 0, PictureMimeType.ofImage(), null));
            }
        }

    }


    /**
     * 保存房屋信息
     */
    private void saveHouse() {
        houseDetail.setCity(cityCode);
        houseDetail.setCityName(cityName);
        houseDetail.setDistrictCode(districtCode);
        houseDetail.setDistrictName(districtName);
        houseDetail.setVillage(edtName.getText().toString());
        houseDetail.setType(type + "");
        houseDetail.setBuildingNo(edtBuildingNo.getText().toString());
        houseDetail.setRoomNo(edtRoomNo.getText().toString());
        houseDetail.setBedroom(edtRoomNum.getText().toString());
        houseDetail.setParlour(edtHallNum.getText().toString());
        houseDetail.setToilet(edtToiletNum.getText().toString());
        houseDetail.setDirection((spDirection.getSelectedItemPosition() + 1) + "");
        houseDetail.setRent(edtRentPrice.getText().toString());
        houseDetail.setPayMethod((spPayMethod.getSelectedItemPosition() + 1) + "");
        houseDetail.setFloor(edtFloor.getText().toString());
        houseDetail.setTotalFloor(edtAllFloor.getText().toString());
        houseDetail.setRenovation((spRenovation.getSelectedItemPosition() + 1) + "");
        houseDetail.setArea(edtArea.getText().toString());
        houseDetail.setHouseDescription(edtNote.getText().toString());
        houseDetail.setRentingRequirements(edtRequirement.getText().toString());
        houseDetail.setHousePicture(TextUtils.join(",",uploadImagePath.toArray()));
        if (type == 2) {
            houseDetail.setHouseType(houseType + "");
        } else {
            houseDetail.setHouseType("");
        }
        houseDetail.setHousingAllocation(JSON.toJSONString(facilityMap));

        API.addHouse(houseDetail, new CommonCallback() {
            @Override
            public void onSuccess(String message) {
                if (isDestory()){
                    return;
                }
                ToastUtils.showShortToast("保存成功");
                EventManger.getDefault().postMyRefreshEvent();
                finish();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (isDestory()){
                    return;
                }
                dismissProgress();
            }
        });
    }

    private boolean checkParamters() {
        if (TextUtils.isEmpty(cityName)) {
            ToastUtils.showShortToast("请选择城市");
            return false;
        }
        if (TextUtils.isEmpty(districtName)) {
            ToastUtils.showShortToast("请选择区域");
            return false;
        }
        if (TextUtils.isEmpty(edtName.getText().toString())) {
            ToastUtils.showShortToast("请输入小区名称");
            return false;
        }
        if (TextUtils.isEmpty(edtBuildingNo.getText().toString())) {
            ToastUtils.showShortToast("请输入楼号");
            return false;
        }
        if (TextUtils.isEmpty(edtRoomNo.getText().toString())) {
            ToastUtils.showShortToast("请输入房号");
            return false;
        }
        if (TextUtils.isEmpty(edtRoomNum.getText().toString()) || TextUtils.isEmpty(edtHallNum.getText().toString()) || TextUtils.isEmpty(edtToiletNum.getText().toString())) {
            ToastUtils.showShortToast("请完善房型");
            return false;
        }
        if (TextUtils.isEmpty(edtRentPrice.getText().toString())) {
            ToastUtils.showShortToast("请输入租金");
            return false;
        }
        if (TextUtils.isEmpty(edtFloor.getText().toString()) || TextUtils.isEmpty(edtAllFloor.getText().toString())) {
            ToastUtils.showShortToast("请完善楼层");
            return false;
        }
        if (TextUtils.isEmpty(edtArea.getText().toString())) {
            ToastUtils.showShortToast("请输入面积");
            return false;
        }
        if (selectList.isEmpty()) {
            ToastUtils.showShortToast("请选择房屋图片");
            return false;
        }
        return true;
    }

    static class UploadTask implements Runnable {
        private String token;
        private String path;
        private String key;
        private UploadImageListener listener;

        public UploadTask(String token, LocalMedia media, UploadImageListener listener) {
            super();
            this.token = token;
            if (media.isCompressed()) {
                Logger.i("原图已压缩");
                path = media.getCompressPath();
            } else if (media.isCut()) {
                Logger.i("原图已裁剪");
                path = media.getCutPath();
            } else {
                Logger.i("原图");
                path = media.getPath();
            }
            this.listener = listener;
            Logger.i("创建上传任务：" + path);
            key = SPUtils.getInstance().getSharedPreference(NameSpace.USER_ACCOUNT, "") + "_" + System.currentTimeMillis() + "." + FileToolUtils.getExtensionName(path);
        }

        @Override
        public void run() {
            Logger.i("上传任务：" + path + "------start---------");
            UploadManager uploadManager = new UploadManager();
            ResponseInfo info = uploadManager.syncPut(path, key, token, null);
            Logger.i("上传任务：" + path + "-------end----------");
            if (info.isOK()) {
                String filePath = UrlConfig.QINIU_DOMAIN_URL + key;
                if (listener != null) {
                    listener.complete(true, filePath);
                }
            } else {
                if (listener != null) {
                    listener.complete(false, path);
                }
            }
        }
    }

    interface UploadImageListener {
        void complete(boolean isSuccess, String imagePath);
    }

    @Override
    protected void onDestroy() {
        CityPopWindow.getDefault(this).dissmiss();
        super.onDestroy();
    }
}
