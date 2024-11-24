package cn.lary.engine.schedule;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTaskService {

    private static final int MAX_APP_NUM = 10;
    public static final long SCHEDULE_RATE = 15000;


    private void schedule( List<Long> appIds) {

        long nowTime = System.currentTimeMillis();
        long timeThreshold = nowTime + 2 * SCHEDULE_RATE;
        Lists.partition(appIds, MAX_APP_NUM).forEach(partAppIds -> {

//            try {
//
//                // 查询条件：任务开启 + 使用CRON表达调度时间 + 指定appId + 即将需要调度执行
//                List<JobInfoDO> jobInfos = jobInfoRepository.findByAppIdInAndStatusAndTimeExpressionTypeAndNextTriggerTimeLessThanEqual(partAppIds, SwitchableStatus.ENABLE.getV(), timeExpressionType.getV(), timeThreshold);
//
//                if (CollectionUtils.isEmpty(jobInfos)) {
//                    return;
//                }
//
//                // 1. 批量写日志表
//                Map<Long, Long> jobId2InstanceId = Maps.newHashMap();
//                log.info("[NormalScheduler] These {} jobs will be scheduled: {}.", timeExpressionType.name(), jobInfos);
//
//                jobInfos.forEach(jobInfo -> {
//                    Long instanceId = instanceService.create(jobInfo.getId(), jobInfo.getAppId(), jobInfo.getJobParams(), null, null, jobInfo.getNextTriggerTime()).getInstanceId();
//                    jobId2InstanceId.put(jobInfo.getId(), instanceId);
//                });
//                instanceInfoRepository.flush();
//
//                // 2. 推入时间轮中等待调度执行
//                jobInfos.forEach(jobInfoDO -> {
//
//                    Long instanceId = jobId2InstanceId.get(jobInfoDO.getId());
//
//                    long targetTriggerTime = jobInfoDO.getNextTriggerTime();
//                    long delay = 0;
//                    if (targetTriggerTime < nowTime) {
//                        log.warn("[Job-{}] schedule delay, expect: {}, current: {}", jobInfoDO.getId(), targetTriggerTime, System.currentTimeMillis());
//                    } else {
//                        delay = targetTriggerTime - nowTime;
//                    }
//
//                    InstanceTimeWheelService.schedule(instanceId, delay, () -> dispatchService.dispatch(jobInfoDO, instanceId, Optional.empty(), Optional.empty()));
//                });
//
//                // 3. 计算下一次调度时间（忽略5S内的重复执行，即CRON模式下最小的连续执行间隔为 SCHEDULE_RATE ms）
//                jobInfos.forEach(jobInfoDO -> {
//                    try {
//                        refreshJob(timeExpressionType, jobInfoDO);
//                    } catch (Exception e) {
//                        log.error("[Job-{}] refresh job failed.", jobInfoDO.getId(), e);
//                    }
//                });
//                jobInfoRepository.flush();
//
//
//            } catch (Exception e) {
//                log.error("[NormalScheduler] schedule {} job failed.", timeExpressionType.name(), e);
//            }
//        });
        });
    }

}
