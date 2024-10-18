package cn.lary.module.common.config;

import cn.lary.module.user.interceptor.UserLoginTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {
    private final UserLoginTokenInterceptor userLoginTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginTokenInterceptor)
//                .addPathPatterns("/**")
                .addPathPatterns(
                        "/v1/group/**",
                        "/v1/friend/**",
                        "/v1/wallet/**",
                        "/v1/room/**",
                        "/v1/")
                .excludePathPatterns(
                        "/v1/user/login",
                        "/v1/user/register",
                        "/v1/user/logout");
    }
}
