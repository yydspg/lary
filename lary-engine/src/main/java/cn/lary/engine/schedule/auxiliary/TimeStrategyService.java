package cn.lary.engine.schedule.auxiliary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeStrategyService {

    private static final int NEXT_N_TIMES = 5;
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final List<String> TIPS = Collections.singletonList("It is valid, but has not trigger time list!");


    private final TimingStrategy timingStrategy;

    public List<String> calculateNextTriggerTimes( String timeExpression, Long startTime, Long endTime) {

        TimingStrategy timingStrategyHandler =getTimingStrategy();
        List<Long> triggerTimeList = new ArrayList<>(NEXT_N_TIMES);
        Long nextTriggerTime = System.currentTimeMillis();
        do {
            nextTriggerTime = timingStrategyHandler.calculateNextTriggerTime(nextTriggerTime, timeExpression, startTime, endTime);
            if (nextTriggerTime == null) {
                break;
            }
            triggerTimeList.add(nextTriggerTime);
        } while (triggerTimeList.size() < NEXT_N_TIMES);

        if (triggerTimeList.isEmpty()) {
            return TIPS;
        }
        return triggerTimeList.stream()
                .map(t -> DateFormatUtils.format(t, TIME_PATTERN))
                .collect(Collectors.toList());
    }


    public Long calculateNextTriggerTime(Long preTriggerTime,String timeExpression, Long startTime, Long endTime) {
        if (preTriggerTime == null || preTriggerTime < System.currentTimeMillis()) {
            preTriggerTime = System.currentTimeMillis();
        }
        return getTimingStrategy().calculateNextTriggerTime(preTriggerTime, timeExpression, startTime, endTime);
    }

    public Long calculateNextTriggerTimeWithInspection(  String timeExpression, Long startTime, Long endTime) {
        Long nextTriggerTime = calculateNextTriggerTime(null, timeExpression, startTime, endTime);
        if (nextTriggerTime == null) {
            log.error("yutak engine time invalid");
        }
        return nextTriggerTime;
    }

    public void validate(String timeExpression, Long startTime, Long endTime) {
        if (endTime != null) {
            if (endTime <= System.currentTimeMillis()) {
                log.error("yutak engine out date");
                return;
            }
            if (startTime != null && startTime > endTime) {
                log.error("start time error");
                return ;
            }
        }
        getTimingStrategy().validate(timeExpression);
    }

    public TimingStrategy getTimingStrategy() {
        return timingStrategy;
    }
}
