package cn.lary.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResponsePair<T> {

    private boolean fail;

    private String msg;

    private T data;
}
