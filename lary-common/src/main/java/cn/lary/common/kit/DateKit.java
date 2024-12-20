package cn.lary.common.kit;

import java.time.LocalDateTime;

public class DateKit {

    public static LocalDateTime getStartOfDay(LocalDateTime time) {
        return time.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDateTime time) {
        return time.toLocalDate().plusDays(1).atStartOfDay();
    }

}
