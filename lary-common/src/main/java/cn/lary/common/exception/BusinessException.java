package cn.lary.common.exception;

import java.io.Serial;

public class BusinessException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_ERR_CODE = "BIZ_ERROR";

    public BusinessException(String errMessage) {
        super(DEFAULT_ERR_CODE, errMessage);
    }

    public BusinessException(String errCode, String errMessage) {
        super(errCode, errMessage);
    }

    public BusinessException(String errMessage, Throwable e) {
        super(DEFAULT_ERR_CODE, errMessage, e);
    }

    public BusinessException(String errorCode, String errMessage, Throwable e) {
        super(errorCode, errMessage, e);
    }

}