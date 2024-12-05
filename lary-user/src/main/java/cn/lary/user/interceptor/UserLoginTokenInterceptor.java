package cn.lary.user.interceptor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Slf4j
//@Component
//@RequiredArgsConstructor
public class UserLoginTokenInterceptor implements HandlerInterceptor {

//    private final TokenOperation operation;


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = operation.getTokenFromRequest(request);
//        Profile profile = operation.process(token, response);
//        if (profile == null) {
//            return false;
//        }
//        operation.store(profile);
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        operation.remove();
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }


}
