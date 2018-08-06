package com.sj.rentinghouse.events;

import java.util.Map;

/**
 * Created by Sunj on 2018/7/17.
 */

public class SearchHouseEvent {
    Map<String, Object> keyMap;
    public SearchHouseEvent(Map<String, Object> keyMap) {
        this.keyMap = keyMap;
    }
}
