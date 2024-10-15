package cn.lary.core.context;

import java.util.Map;

public class RequestContext {
    private static final ThreadLocal<Map<String, String>> currentRequest = new ThreadLocal<>();

    public static void setCurrent(Map<String, String> data) {
        currentRequest.set(data);
    }

    public static Map<String, String> getCurrent() {
        return currentRequest.get();
    }
    public static void removeCurrent() {
        currentRequest.remove();
    }

    /**
     * this method use thread local ,can not return null
     * @return uid
     */
    public static int getLoginUID() {
        return  Integer.parseInt(getCurrent().get("uid"));
    }

    /**
     * this method use thread local ,can not return null
     * @return login username
     */
    public static String getLoginName() {
        return  getCurrent().get("name");
    }
}