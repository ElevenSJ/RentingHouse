package com.sj.rentinghouse.bean;

/**
 * Created by Sunj on 2018/7/14.
 */

public class TrainLineInfo {


    private String name;
    private String id;
    private String status;

    public TrainLineInfo() {
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String toDataString(){
        return name+"-"+id;
    }
}
