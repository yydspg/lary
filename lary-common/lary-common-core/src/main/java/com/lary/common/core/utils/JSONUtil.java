package com.lary.common.core.utils;

import com.alibaba.fastjson2.*;

import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson2.JSONWriter.Feature.*;

/**
 * fastjson2 util
 * @author paul 2023/12/14
 */

public class JSONUtil {

    /**
     * fastjson2 property url: <a href="https://alibaba.github.io/fastjson2/features_cn">...</a>
     * SerializeFeature 在fastjson2 中被移除 代替的是 JSONWriter JSONReader
     */
    private static final JSONWriter.Feature[] WRITER_FEATURES = {
            //序列化输出空值字段
            WriteNulls,
            //List序列化空值输出为空数组"[]"
            WriteNullListAsEmpty,
            //Number序列化空值输出为空字符串""
            WriteNullStringAsEmpty,
            //Boolean序列化空值输出为 false
            WriteNullBooleanAsFalse,
            //数组类型length == 0 时,不输出
            NotWriteEmptyArray
    };
    /**
     * @param o: object
     * @return JSONString
     * @author paul
     * @description convert Object ==> String
     * @date 2023/12/14 22:10
     */
    public static String toString(Object o){
        return JSON.toJSONString(o);
    }
    public static String toFeatureString(Object o){
        return JSONObject.toJSONString(o,WRITER_FEATURES);
    }
    /**
     * @param json:
     * @param t: Class
     * @return T
     * @author paul
     * @description  convert String ==> Object
     * @date 2023/12/14 22:13
     */
    public static <T> T toObject(String json,Class<T> t){
        return  JSON.parseObject(json,t);
    }
    public static Object toObject(String json){
        return JSONObject.parse(json);
    }
    /**
     * @param s:
     * @return Map<K,V>
     * @author paul
     * @description string ==> map
     * @date 2023/12/14 22:27
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V>  toCollect(String s) {
        return (Map<K, V>) JSONObject.parseObject(s);
    }
    /**
     * @param m:
     * @return String
     * @author paul
     * @description map ==> string
     * @date 2023/12/14 22:25
     */
    public static <K, V> String collectToString(Map<K, V> m) {
        return JSONObject.toJSONString(m);
    }
    /**
     * @param json:
     * @param clazz:
     * @return Object
     * @author paul
     * @description json ==> list
     * @date 2023/12/14 22:25
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
    /**
     * @param json:
     * @return JSONArray
     * @author paul
     * @description List String ==> JSON array
     * @date 2023/12/14 23:25
     */
    public static JSONArray listToArray(String json){
        return JSON.parseArray(json);
    }
    /**
     * @param json: 
     * @param clazz: 
     * @return T 
     * @author paul
     * @description json ==> map
     * @date 2023/12/14 22:24
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
}
