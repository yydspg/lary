package cn.lary.common.kit;


import com.alibaba.fastjson2.JSON;

public class JSONKit {

    public static String toJSON(Object obj) {
        return JSON.toJSONString(obj);
    }
    public static <T> T fromJSON(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}
