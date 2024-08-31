package cn.lary.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BasePageQuery {
    private long page;
    private long pageSize;
}
