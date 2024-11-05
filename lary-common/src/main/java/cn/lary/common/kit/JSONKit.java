package cn.lary.common.kit;


import com.alibaba.fastjson2.JSON;

public class JSONKit {

    public static String toJSON(Object obj) {
        return JSON.toJSONString(obj);
    }
}
