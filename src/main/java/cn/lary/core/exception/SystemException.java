package cn.lary.core.exception;

import java.io.Serial;

public class SystemException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_ERR_CODE = "SYS_ERROR";

    public SystemException(String errMessage) {
        super(DEFAULT_ERR_CODE, errMessage);
    }

    public SystemException(String errCode, String errMessage) {
        super(errCode, errMessage);
    }

    public SystemException(String errMessage, Throwable e) {
        super(DEFAULT_ERR_CODE, errMessage, e);
    }

    public SystemException(String errorCode, String errMessage, Throwable e) {
        super(errorCode, errMessage, e);
    }

}
