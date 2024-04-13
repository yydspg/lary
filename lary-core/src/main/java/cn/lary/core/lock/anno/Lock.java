package cn.lary.core.lock.anno;

import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.exec.LockExec;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author paul 2024/4/13
 */


@Repeatable(Lock.List.class)
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lock {
    String condition() default "";
    String name() default "";
    String[] keys() default "";
    long expire() default -1;
    long acquireTimeout() default -1;
    boolean autoRelease() default true;
    int order() default Ordered.LOWEST_PRECEDENCE;
    Class<? extends LockExec> exec() default LockExec.class;
    Class<? extends LockFailPloy> failStrategy() default LockFailPloy.class;
    Class<? extends LockKeyBuilder> keyBuilderStrategy() default LockKeyBuilder.class;
    @Inherited
    @Target(value = {ElementType.METHOD})
    @Retention(value = RetentionPolicy.RUNTIME)
    @interface List {
        Lock[] value();
    }
}
