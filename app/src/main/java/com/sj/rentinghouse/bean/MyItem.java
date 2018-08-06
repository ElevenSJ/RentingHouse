package com.sj.rentinghouse.bean;

/**
 * Created by Sunj on 2018/7/12.
 */

public class MyItem {
    private int drawableLeftId;
    private String itemName;
    private String itemRemark;
    private String drawableRight;

    public MyItem(int drawableLeftId, String itemName, String itemRemark) {
        this(drawableLeftId, itemName, itemRemark, null);
    }

    public MyItem(int drawableLeftId, String itemName, String itemRemark, String drawableRight) {
        this.drawableLeftId = drawableLeftId;
        this.itemName = itemName;
        this.itemRemark = itemRemark;
        this.drawableRight = drawableRight;
    }

    public MyItem(int drawableLeftId, String itemName) {
        this(drawableLeftId, itemName, null, null);
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

    public String getDrawableRight() {
        return drawableRight;
    }

    public void setDrawableLeftId(int drawableLeftId) {
        this.drawableLeftId = drawableLeftId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemRemark(String itemRemark) {
        this.itemRemark = itemRemark;
    }

    public void setDrawableRight(String drawableRight) {
        this.drawableRight = drawableRight;
    }
}
