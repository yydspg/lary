package cn.lary.module.user.interceptor;

import cn.lary.common.context.RequestContext;
import cn.lary.common.constant.ResultCode;
import cn.lary.common.kit.ResponseKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginTokenInterceptor implements HandlerInterceptor {
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(StringKit.isEmpty(token)) {
            ResponseKit.responseFail(response, "login first");
            return false;
        }
        String value = getLoginTokenV(token);
        if(StringKit.isEmpty(value)) {
            ResponseKit.responseFail(response, "token empty");
            return false;
        }
        String[] args = StringKit.split(value, "@");
        if(args == null || args.length < 2) {
            ResponseKit.responseFail(response, "token error");
            return false;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", args[0]);
        map.put("name", args[1]);
        if (args.length > 2) {
            map.put("role", args[2]);
        }
        RequestContext.setCurrent(map);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestContext.getCurrent().clear();
        RequestContext.removeCurrent();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
    private String getLoginTokenV(String token) {
        return redisCache.get(kvBuilder.userLoginTokenK(token));
    }

}
