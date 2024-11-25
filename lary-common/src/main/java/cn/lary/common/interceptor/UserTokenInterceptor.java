package cn.lary.common.interceptor;

import cn.lary.common.context.Profile;
import cn.lary.common.interceptor.token.TokenOperation;
import cn.lary.common.interceptor.token.jwt.JWTTokenOperation;





public class UserTokenInterceptor {

    private final TokenOperation operation;

    public UserTokenInterceptor(){
        operation = new JWTTokenOperation();
    }

    public UserTokenInterceptor(TokenOperation operation){
        this.operation = operation;
    }

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = operation.getTokenFromRequest(request);
//        Profile profile = operation.process(token, response);
//        if(profile == null){
//            return false;
//        }
//        operation.store(profile);
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        operation.remove();
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }
}
