package cn.lary.core.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author paul 2024/4/9
 */

public class JSONKit {
    public static JSONArray convert(String k,String ... values) {
        JSONArray jsonArray = new JSONArray();
        for (String value : values) {
            JSONObject entry = new JSONObject();
            entry.put(k,value);
            jsonArray.add(entry);
        }
        return jsonArray;
    }
    public static JSONObject convert(String k,Object v) {
        return JSONObject.of(k,v);
    }
    public static JSONObject get() {
        return new JSONObject();
    }
}
