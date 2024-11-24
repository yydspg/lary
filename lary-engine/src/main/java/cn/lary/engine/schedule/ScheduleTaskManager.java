package cn.lary.engine.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleTaskManager {

    private final List<Thread> threadContainer = new ArrayList<>(8);


    public synchronized void execute(){
//        threadContainer.add(new Thread(new LoopRunnable()))
        threadContainer.forEach(Thread::start);
    }


    @RequiredArgsConstructor
    private static class LoopRunnable implements Runnable {

        private final String taskName;

        private final Long runningInterval;

        private final Runnable innerRunnable;

        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            log.info("yutak engine task : {}.", taskName);
            while (true) {
                try {

                    // 倒置顺序为 先 sleep 再执行，解决异常情况 while true 打日志的问题 https://github.com/PowerJob/PowerJob/issues/769
                    Thread.sleep(runningInterval);

                    innerRunnable.run();
                } catch (InterruptedException e) {
                    log.warn("[{}] task has been interrupted!", taskName, e);
                    break;
                } catch (Exception e) {
                    log.error("[{}] task failed!", taskName, e);
                }
            }
        }
    }
}
