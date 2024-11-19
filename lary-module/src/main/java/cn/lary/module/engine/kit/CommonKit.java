package cn.lary.module.engine.kit;

import java.util.function.Supplier;

public class CommonKit {

    private static final int MAXIMUM_CAPACITY = 1 << 30;


    /**
     * 重试执行，仅适用于根据返回值决定是否执行成功的方法
     * @param booleanExecutor 需要执行的方法，其返回值决定了执行是否成功
     * @param tryTimes 尝试执行次数
     * @param intervalMS 失败后下一次执行的间隔时间
     * @return 最终执行结果
     */
    public static boolean executeWithRetryV2(Supplier<Boolean> booleanExecutor, int tryTimes, long intervalMS) {

        if (tryTimes <= 1 || intervalMS <= 0) {
            return booleanExecutor.get();
        }

        for (int i = 1; i < tryTimes; i++) {
            try {
                if (booleanExecutor.get()) {
                    return true;
                }
                Thread.sleep(intervalMS);
            }catch (Exception ignore) {
            }
        }
        return booleanExecutor.get();
    }


    /**
     * 将大小格式化为 2的N次
     * @param cap 初始大小
     * @return 格式化后的大小，2的N次
     */
    public static int formatSize(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }




    public static void easySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }
}

