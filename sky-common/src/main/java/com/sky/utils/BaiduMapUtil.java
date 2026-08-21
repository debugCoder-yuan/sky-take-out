package com.sky.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BaiduMapUtil {

    // 从 application.yml 中读取你的百度地图 AK
    @Value("${sky.baidu.ak}")
    private String ak;

    public Integer calculateDistance(String shopAddress, String userAddress) {
        // 1. 获取店铺和用户的经纬度坐标 (格式: lat,lng)
        String shopLngLat = getCoordinate(shopAddress);
        String userLngLat = getCoordinate(userAddress);

        // 2. 调用路线规划接口计算距离 (骑行版)
        String url = "https://api.map.baidu.com/directionlite/v1/riding";
        Map<String, String> map = new HashMap<>();
        map.put("ak", ak);
        map.put("origin", shopLngLat);
        map.put("destination", userLngLat);
        map.put("steps_info", "0"); // 只需要总距离，不需要详细路段信息

        // 发送 HTTP GET 请求
        String response = HttpClientUtil.doGet(url, map);

        // 3. 解析返回的 JSON 数据获取距离
        JSONObject jsonObject = JSON.parseObject(response);
        if (!"0".equals(jsonObject.getString("status"))) {
            throw new RuntimeException("路线规划接口调用失败：" + jsonObject.getString("message"));
        }

        // 逐层剥开 JSON 拿到距离字段
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray routes = result.getJSONArray("routes");
        // 返回单位为：米 (m)
        return routes.getJSONObject(0).getInteger("distance");
    }

    /**
     * 辅助方法：调用地理编码接口获取经纬度
     */
    private String getCoordinate(String address) {
        String url = "https://api.map.baidu.com/geocoding/v3";
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("output", "json");
        map.put("ak", ak);

        String response = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject = JSON.parseObject(response);

        if (!"0".equals(jsonObject.getString("status"))) {
            throw new RuntimeException("地理编码接口调用失败：" + jsonObject.getString("message"));
        }

        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat"); // 纬度
        String lng = location.getString("lng"); // 经度

        // 注意：路线规划接口要求的起点/终点坐标格式必须是 "纬度,经度"
        return lat + "," + lng;
    }
}
