package cn.lary.external.srs.dto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class SrsDTO {
    /**
     * 处理srs返回的 param 类型： ？token=xxx?uid=xxx
     * @param paramStr 参数
     * @return map
     */
    public  Map<String, String> parseParams(String paramStr) {
        if (paramStr == null || paramStr.isEmpty()) {
            return new LinkedHashMap<>();
        }
        // 去掉开头的问号
        paramStr = paramStr.startsWith("?") ? paramStr.substring(1) : paramStr;
        Map<String, String> params = new LinkedHashMap<>();
        String[] pairs = paramStr.split("&");
        for (String pair : pairs) {

            int idx = pair.indexOf('=');
            String key = URLDecoder.decode(idx > 0 ? pair.substring(0, idx) : pair, StandardCharsets.UTF_8);
            String value = URLDecoder.decode(idx > 0 ? pair.substring(idx + 1) : "", StandardCharsets.UTF_8);
            params.put(key, value);
        }
        return params;
    }
}
