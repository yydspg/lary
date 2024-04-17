package cn.lary.oss.standard.domain.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class BaseFrag {
    private int partNumber;
    private String etag;
}
