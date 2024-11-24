package cn.lary.gift.dto;

import cn.lary.common.dto.PageQuery;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GiftOrderPageQueryDTO extends PageQuery {

    private long uid;

    private Integer status;

    private LocalDateTime startAt;

    private LocalDateTime endAt;
}
