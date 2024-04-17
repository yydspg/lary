package cn.lary.oss.standard.prop;

import cn.lary.core.model.base.Pool;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class AbstractProp {
    /**
     * Oss Server endpoint
     */
    private String endpoint;

    /**
     * Oss Server accessKey
     */
    private String accessKey;

    /**
     * Oss Server secretKey
     */
    private String secretKey;


    private Pool pool = new Pool();
}
