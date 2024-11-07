package cn.lary.module.user.interceptor;

import cn.lary.common.context.Profile;
import cn.lary.module.web.token.redis.RedisTokenOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginTokenInterceptor implements HandlerInterceptor {

    private final RedisTokenOperation operation;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = operation.getTokenFromRequest(request);
        Profile profile = operation.process(token, response);
        if (profile == null) {
            return false;
        }
        operation.store(profile);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        operation.remove();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }


}
