package com.sj.rentinghouse.bean;

import java.util.List;

/**
 * Created by Sunj on 2018/7/8.
 */

public class DataList<E> {
    List<E> data;

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }
}
