package cn.lary.core.util;

import java.lang.management.ManagementFactory;
import java.util.UUID;

/**
 * @author paul 2024/4/13
 */

public class LockKit {
    public static String getLockV() {
        return simpleUUID();
    }
    public static String getJvmPid() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        int indexOf = pid.indexOf('@');
        if (indexOf > 0) {
            pid = pid.substring(0, indexOf);
            return pid;
        }
        throw new IllegalStateException("ManagementFactory error");
    }
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
