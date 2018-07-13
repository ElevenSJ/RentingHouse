package com.sj.rentinghouse.bean;

import com.sj.rentinghouse.R;

/**
 * Created by Sunj on 2018/7/12.
 */

public class MyItem {
    private int drawableLeftId;
    private String itemName;
    private String itemRemark;
    private int drawableRightId = R.drawable.arrow_right;

    public MyItem(int drawableLeftId, String itemName, String itemRemark) {
        this(drawableLeftId,itemName,itemRemark, R.drawable.arrow_right);
    }

    public MyItem(int drawableLeftId, String itemName, String itemRemark, int drawableRightId) {
        this.drawableLeftId = drawableLeftId;
        this.itemName = itemName;
        this.itemRemark = itemRemark;
        this.drawableRightId = drawableRightId;
    }

    public MyItem(int drawableLeftId, String itemName) {
        this(drawableLeftId,itemName,null, R.drawable.arrow_right);
    }

    public int getDrawableLeftId() {
        return drawableLeftId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemRemark() {
        return itemRemark;
    }

    public int getDrawableRightId() {
        return drawableRightId;
    }
}
