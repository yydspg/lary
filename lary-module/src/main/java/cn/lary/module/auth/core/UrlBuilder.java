package cn.lary.module.auth.core;

import cn.lary.common.kit.StringKit;
import lombok.Setter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Setter
public class UrlBuilder {

    private final Map<String, String> params = new LinkedHashMap<>(7);
    private String baseUrl;

    private UrlBuilder() {

    }

    /**
     * @param baseUrl 基础路径
     * @return the new {@code UrlBuilder}
     */
    public static UrlBuilder fromBaseUrl(String baseUrl) {
        UrlBuilder builder = new UrlBuilder();
        builder.setBaseUrl(baseUrl);
        return builder;
    }


    public Map<String, Object> getReadOnlyParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 添加参数
     */
    public UrlBuilder param(String key, Object value) {
        if (StringKit.isEmpty(key)) {
            throw new RuntimeException("参数名不能为空");
        }
        String valueAsString = (value != null ? value.toString() : null);
        this.params.put(key, valueAsString);
        return this;
    }
    public String build() {
        return this.build(false);
    }


    public String build(boolean encode) {
        String baseUrl = StringKit.appendIfNotContain(this.baseUrl, "?", "&");
        String paramString = parseMapToString(this.params, encode);
        return baseUrl + paramString;
    }

    public static String parseMapToString(Map<String, String> params, boolean encode) {
        return params.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    return key + "=" + (value == null ? "" : (encode ? urlEncode(value) : value));
                })
                .collect(Collectors.joining("&"));
    }

    public static String urlEncode(String value) {
        if (value == null) {
            return "";
        } else {
            String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
            return encoded
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("~", "%7E")
                    .replace("/", "%2F");
        }
    }
}
