package com.yutak.cache;

import com.yutak.cache.anno.YutakCacheComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Objects;
import java.util.Set;

@Slf4j
public class ClassPathCacheComponentScanner extends ClassPathBeanDefinitionScanner {

    private final ClassLoader classLoader;

    public ClassPathCacheComponentScanner(BeanDefinitionRegistry registry, ClassLoader classLoader) {
        super(registry, false);
        this.classLoader = classLoader;
    }
    public void registerFilters() {
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(YutakCacheComponent.class);
        this.addIncludeFilter(annotationTypeFilter);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {

    }


    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        GenericBeanDefinition definition;
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (GenericBeanDefinition)holder.getBeanDefinition();
            if (log.isDebugEnabled()) {
                log.debug("yutak:cache:[Creating local cache component with name '" + holder.getBeanName()
                        + "' and '" + definition.getBeanClassName() + "' Interface"+"]");
            }
            definition.getConstructorArgumentValues()
                    .addGenericArgumentValue(Objects.requireNonNull(definition.getBeanClassName()));
            // beanClass全部设置为RetrofitFactoryBean
            definition.setBeanClass(YutakCacheFactoryBean.class);
        }
    }
}
