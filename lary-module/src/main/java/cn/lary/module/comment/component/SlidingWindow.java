package cn.lary.module.comment.component;

import cn.lary.common.id.SystemClock;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindow {

    /**
     * 队列的总长度 == window * 2
     */
    private static final int size = 4 * 2;
    /**
     * 数据采集以每个时间片为单位<br>
     * 每个时间片的时长以毫秒为单位
     */
    private static final long time = 5 *100;
    /**
     * 共有多少个时间片（即窗口长度）
     */
    private static final int window = 4;
    /**
     * 总限流窗口
     */
    private static final long total = time * window;
    /**
     * 在一个完整窗口期内允许通过的最大阈值
     */
    private static long threshold = 10000L;
    /**
     * 该滑窗的起始创建时间
     */
    private long begin;
    /**
     * 最后一个数据的时间戳
     */
    private long last;

    /**
     * 循环队列,存放时间窗口,该数量是windowSize的2倍
     */
    private final AtomicLong[] slices;

    private final ReentrantLock lock_1 = new ReentrantLock();
    private final ReentrantLock lock_2 = new ReentrantLock();

    public SlidingWindow() {
        slices = new AtomicLong[size];
        for (int i = 0; i < size; i++) {
            slices[i] = new AtomicLong(0);
        }
    }

    /**
     * rebuild sliding window
     */
    private void rebuild(){
        lock_1.lock();
        begin = SystemClock.now();
        for (int i = 0; i < size; i++) {
            slices[i].getAndSet(0L);
        }
        lock_1.unlock();
    }

    private boolean exec(long amount){
        int i = index();
        lock_2.lock();
        loop(i);
        long sum = slices[i].getAndAdd(amount);
        for(int j = 1; j < window; j++){
            sum += slices[(i-j+size) % size].get();
        }
        lock_2.unlock();
        last = SystemClock.now();
        return sum >= threshold;
    }

    /**
     * build current key index
     * @return index
     */
    private  int index(){
        long now = SystemClock.now();
        if (now - last > total) {
            rebuild();
        }
        long i = ((now - begin) / time) % size;
        if (i < 0L) {
            i = 0L;
        }
        return (int)i;
    }

    /**
     * loop time array<br>
     * if e == 4
     * then set slices[5,6,7,8] = 0 <br>
     * if e == 0
     * then set slices[1,2,3,4] = 0 <br>
     * @param e index from current index
     */
    private void loop(int e) {
        for (int i = 1; i < window; i++) {
            int j = i + e;
            if ( j >= size) {
                j -= size;
            }
            slices[j].set(0L);
        }
    }

//    public static void main(String[] args) throws Exception {
//        AtomicLong data = new AtomicLong();
//        int th = 10000;
//        for (int i = 0; i < th; i++) {
//            new Thread(() ->{
//                SlidingWindow sw = new SlidingWindow();
//                long l = System.currentTimeMillis();
//                for (int j = 0; j < 10002; j++) {
//                    boolean exec = sw.exec(1);
//                    if (exec) {
////                        System.out.println(" 1 over limit");
//                    }
//                }
//                data.getAndAdd(System.currentTimeMillis() - l);
//
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                l = System.currentTimeMillis();
//                for (int j = 0; j < 10002; j++) {
//                    boolean exec = sw.exec(1);
//                    if (exec) {
////                        System.out.println("2 over limit");
//                    }
//                }
//                data.getAndAdd(System.currentTimeMillis() - l);
//            }).start();
//        }
//        TimeUnit.SECONDS.sleep(3);
//        System.out.println(data.get());
//        long l = data.get() / th;
//        System.out.println(l);
//        System.out.println("avg cost time:"+data.get()/(2* th));
//
//    }
}
