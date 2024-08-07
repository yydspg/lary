package cn.lary.kit;

import jakarta.servlet.http.HttpServletRequest;

public class IPKit {
    private static final String LOCAL_IP = "127.0.0.1";
    private static final String UNKNOWN = "unknown";
    /**
     * 获取IP地址
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (StringKit.isEmpty(ip) || StringKit.same(UNKNOWN,ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringKit.isEmpty(ip) || StringKit.same(UNKNOWN,ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringKit.isEmpty(ip) || StringKit.same(UNKNOWN,ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringKit.isEmpty(ip) || StringKit.same(UNKNOWN,ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StringKit.isEmpty(ip) || StringKit.same(UNKNOWN,ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? LOCAL_IP : ip;
    }
}
