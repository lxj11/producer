package com.example.producer.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.producer.entity.Order;

/**
 * @author LXJ
 * @description
 * @date 2020/3/3 16:46
 */
public class FastJsonConvertUtil<T> {
    public static String convertObjectToJSON(Order order)
    {
        return JSON.toJSONString(order);

    }
    public static JSONObject toJsonObject(Object javaBean)
    {
        return JSONObject.parseObject(JSONObject.toJSON(javaBean).toString());
    }
}
