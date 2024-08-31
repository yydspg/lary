package cn.lary.kit;

import com.alibaba.fastjson2.JSON;

import java.util.Map;

public class JSONKit {
    public static String toJSON(Object obj) {
        return JSON.toJSONString(obj);
    }
    public static Map<String,String> toMap(String json) {
        return JSON.parseObject(json, Map.class);
    }
}
