package com.lary.common.core.utils;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * spring context 封装
 * @author paul 2023/12/14
 */
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext appContext) throws BeansException {
            applicationContext = appContext;
    }
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }
    public static <T> T getBean(Class<T> requiredType){
        return applicationContext.getBean(requiredType);
    }
    public static <T> T getBean(String name,Class<T> requiredType){
        return applicationContext.getBean(name,requiredType);
    }
    public static boolean containsBean(String name){
        return  applicationContext.containsBean(name);
    }
    public static boolean isSingleton(String name){
        return applicationContext.isSingleton(name);
    }
    public static Class<?> getType(String name){
        return applicationContext.getType(name);
    }
}
