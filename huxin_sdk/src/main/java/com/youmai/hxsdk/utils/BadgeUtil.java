package com.youmai.hxsdk.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 作者：create by YW
 * 日期：2017.02.12 18:19
 * 描述: UserModel转Map，Map转json
 */

public class BadgeUtil {

    public static String mapToJson(Map<String, Integer> map) {
        JSONObject jsonObject = new JSONObject(map);
        return jsonObject.toString();
    }


    public static Map<String, Integer> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Integer> retMap = new HashMap<>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Integer> toMap(JSONObject object) throws JSONException {
        Map<String, Integer> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Integer value = (Integer) object.get(key);
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
