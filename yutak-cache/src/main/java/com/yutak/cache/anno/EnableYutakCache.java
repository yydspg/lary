package com.yutak.cache.anno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface EnableYutakCache {
    /**
     * Scan package path
     * Same meaning as basePackages
     *
     * @return basePackages
     */
    String[] value() default {};

    /**
     * Scan package path
     *
     * @return basePackages
     */
    String[] basePackages() default {};

    /**
     * Scan package classes
     *
     * @return Scan package classes
     */
    Class<?>[] basePackageClasses() default {};
}
