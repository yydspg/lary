package com.yutak.cache;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class YutakCacheFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {

    private final Class<T> componentInterface;

    private Environment environment;

    private YutakCacheConfig config;

    private ApplicationContext applicationContext;

    public YutakCacheFactoryBean(Class<T> componentInterface) {
        this.componentInterface = componentInterface;
    }
    @Override
    public T getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return this.componentInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.config = applicationContext.getBean(YutakCacheConfig.class);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
