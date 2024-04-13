package cn.lary.core.lock.config;

import cn.lary.core.lock.builder.LockKeyBuilder;
import cn.lary.core.lock.exec.LockExec;
import cn.lary.core.lock.lockFailPloy.LockFailPloy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author paul 2024/4/13
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "lock")
public class LockProp {

    private Long expire = 30000L;

    private Long acquireTimeout = 3000L;

    private Long retryInterval = 100L;


    private Class<? extends LockExec> primaryExecutor;

    private Class<? extends LockFailPloy> primaryFailureStrategy;

    private Class<? extends LockKeyBuilder> primaryKeyBuilder;

    private String lockKeyPrefix = "lock";
}
