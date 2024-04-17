package cn.lary.core.lock;

import cn.lary.core.lock.exec.LockExec;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author paul 2024/4/13
 */
@Getter @Setter
@AllArgsConstructor
public class LockInfo {

    private String lockK;

    private String lockV;

    private Long expire;

    private Long acquireTimeout;

    private int acquireCount;

    private Object lockInstance;

    private LockExec lockExec;
}
