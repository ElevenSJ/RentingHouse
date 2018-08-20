package com.sj.rentinghouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.sj.module_lib.utils.DateUtils;
import com.sj.rentinghouse.R;
import com.sj.rentinghouse.base.AppBaseActivity;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Sunj on 2018/8/19.
 */

public class SetRentActivity extends AppBaseActivity {
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.img_top_right)
    TextView imgTopRight;
    private TimePickerView timePickerView;
    int rentTimeType = 1;
    @Override
    public int getContentView() {
        return R.layout.activity_set_rent;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        setTopTitle(R.id.tv_top_title, "设置租期");
    }

    @OnClick({R.id.img_top_right, R.id.tv_start_time, R.id.tv_end_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_top_right:
                Intent intent = new Intent();
                intent.putExtra("startTime",tvStartTime.getText().toString().trim());
                intent.putExtra("endTime",tvEndTime.getText().toString().trim());
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.tv_start_time:
                rentTimeType = 1;
                showTimePickerView();
                break;
            case R.id.tv_end_time:
                rentTimeType = 2;
                showTimePickerView();
                break;
        }
    }
    private void showTimePickerView() {
        if (timePickerView == null){
            timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    if (rentTimeType == 1) {
                        tvStartTime.setText(DateUtils.getDayTime(date));
                    } else {
                        tvEndTime.setText(DateUtils.getDayTime(date));
                        imgTopRight.setVisibility(View.VISIBLE);
                        imgTopRight.setText("保存");
                    }
                }
            });
        }
        timePickerView.setTime(new Date());
        timePickerView.show();
    }
}
