package cn.lary.module.user.interceptor;

import cn.lary.core.context.ReqContext;
import cn.lary.core.cs.ResultCode;
import cn.lary.kit.ResKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.cache.KVBuilder;
import cn.lary.module.common.cache.RedisCache;
import cn.lary.module.common.server.RedisBizConfig;
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
public class UserInterceptor  implements HandlerInterceptor {
    private final RedisCache redisCache;
    private final KVBuilder kvBuilder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(StringKit.isEmpty(token)) {
            ResKit.responseFail(response, ResultCode.LOGIN_FIRST);
            return false;
        }
        String value = getLoginTokenV(token);
        if(StringKit.isEmpty(value)) {
            ResKit.responseFail(response, "token empty");
            return false;
        }
        String[] args = StringKit.split(value, "@");
        if(args == null || args.length < 2) {
            ResKit.responseFail(response, "token error");
            return false;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", args[0]);
        map.put("name", args[1]);
        if (args.length > 2) {
            map.put("role", args[2]);
        }
        ReqContext.setCurrent(map);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ReqContext.getCurrent().clear();
        ReqContext.removeCurrent();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
    private String getLoginTokenV(String token) {
        String[] args = StringKit.split(token, "@");
        if(args == null || args.length < 2) {
            return null;
        }
        return redisCache.get(kvBuilder.userLoginK(Integer.parseInt(args[0]),Integer.parseInt(args[1])));
    }

}
