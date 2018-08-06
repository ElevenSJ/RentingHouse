package com.sj.rentinghouse.events;

/**
 * Created by Sunj on 2018/7/22.
 */

public class OrderStatusEvent {
    String id;
    String status;
    public OrderStatusEvent(String id, String status) {
        this.id = id;
        this.status = status;
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
}
