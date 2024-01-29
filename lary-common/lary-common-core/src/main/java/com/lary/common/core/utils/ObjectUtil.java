package com.lary.common.core.utils;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author paul 2023/12/14
 */
@Slf4j
public class ObjectUtil {
    public static String mapToString(Map<String,String[]> paramMap){
        if (paramMap == null) {
            return "";
        }
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String[]> t : paramMap.entrySet()) {
            String k = t.getKey();
            String v = (t.getValue() != null && t.getValue().length > 0 ? t.getValue()[0] : "");
            String obj = StringUtil.endWithIgnoreCase(k,"password") ? "******": v;
            map.put(k,obj);
        }
        return JSONUtil.toString(map);
    }
}

