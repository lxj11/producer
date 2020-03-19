package com.example.producer.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.producer.entity.Order;

/**
 * @author LXJ
 * @description
 * @date 2020/3/3 16:41
 */
public class JsonConvertUtils {
    public static Order convertJSONToObject(JSONObject json)
    {
        return JSONObject.toJavaObject(json, Order.class);
    }
}
