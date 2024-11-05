package com.yutak.cache.anno;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface YutakCacheComponent {

    String prefix();


}
