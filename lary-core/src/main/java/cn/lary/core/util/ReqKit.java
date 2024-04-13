package cn.lary.core.util;

import cn.lary.core.constant.ApiCodeEnum;
import cn.lary.core.exception.BizException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author paul 2024/4/9
 */
@Slf4j
@Component
public class ReqKit {

    protected  HttpServletRequest request;
    @Autowired
    public ReqKit(HttpServletRequest request) {
        this.request = request;
    }
    private static final String REQ_CONTEXT_KEY_PARAM_JSON  = "REQ_CONTEXT_KEY_PARAM_JSON";


    public JSONObject getParamsInJSON() {
        JSONObject resJSON = JSONKit.get();
        //JSON
        if(isJSON()) {
            String body = "";
            try {
                body = request.getReader().lines().collect(Collectors.joining(""));
                if(StringKit.isEmpty(body)) {
                    return resJSON;
                }
                return JSONObject.parseObject(body);
            } catch (Exception e) {
                log.error("[{}]ConvertParamsError", body);
                throw new BizException(ApiCodeEnum.PARAMS_ERROR, "ConvertError");
            }
        }
        //NON-JSON
        Map<String, String[]> properties = request.getParameterMap();
        Iterator<Map.Entry<String, String[]>> entries = properties.entrySet().iterator();
        Map.Entry<String,String[]> entry;
        StringBuilder value = new StringBuilder();
        String name;
        while(entries.hasNext()) {
            entry = entries.next();
            name = entry.getKey();
            String[] valueObj = entry.getValue();
            if (null != valueObj) {
                for (String s : valueObj) {
                    value.append(s).append(",");
                }
                value.deleteCharAt(value.length()-1);
            }
            // normal json
            if(!name.contains("[")) {
                resJSON.put(name,value.toString());
                continue;
            }
            //special json example: {ps[abc] : 1}
            String mainKey = name.substring(0, name.indexOf("["));
            String subKey = name.substring(name.indexOf("[") + 1 , name.indexOf("]"));
            JSONObject subJson = new JSONObject();
            if(resJSON.get(mainKey) != null) {
                subJson = (JSONObject)resJSON.get(mainKey);
            }
            subJson.put(subKey, value);
            resJSON.put(mainKey, subJson);
        }
        return resJSON;
    }
    private boolean isJSON() {
        String contentType = request.getContentType();
        return contentType != null
                && contentType.toLowerCase().contains("application/json")
                && !"GET".equalsIgnoreCase(request.getMethod());
    }
    public JSONObject getReqParamJSON() {
        Object o = Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(REQ_CONTEXT_KEY_PARAM_JSON, RequestAttributes.SCOPE_REQUEST);
        if (null == o) {
            JSONObject reqParam = this.getParamsInJSON();
            RequestContextHolder.getRequestAttributes().setAttribute(REQ_CONTEXT_KEY_PARAM_JSON,reqParam,RequestAttributes.SCOPE_REQUEST);
            return reqParam;
        }
        return (JSONObject) o;
    }
    public String getClientIp() {
        String ipAddress = null;
        ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
