package cn.lary.advertisement.config;

import cn.lary.common.interceptor.UserTokenInterceptor;
import cn.lary.common.interceptor.token.jwt.JWTTokenOperation;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

//    private final UserTokenInterceptor userTokenInterceptor = new UserTokenInterceptor(new JWTTokenOperation());
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(userTokenInterceptor)
//                .addPathPatterns(
//                        "/v1/ad/**"
//                )
//                .excludePathPatterns(
//                        "/v1/user/login"
//                );
//    }
}
