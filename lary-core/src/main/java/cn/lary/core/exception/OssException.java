package cn.lary.core.exception;

import java.io.Serial;

/**
 * @author paul 2024/4/13
 */

public class OssException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

}