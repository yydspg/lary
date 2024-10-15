package cn.lary.core.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResPair<T> {
    private boolean ok;
    private String msg;
    private T data;
}
