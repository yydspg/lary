package cn.lary.common.exception;

import java.io.Serial;

public class ParamVerifyException extends BaseException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_ERR_CODE = "PARAM_ERROR";

    public ParamVerifyException(String message) {super(DEFAULT_ERR_CODE,message);}
}
