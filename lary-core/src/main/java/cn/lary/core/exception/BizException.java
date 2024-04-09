package cn.lary.core.exception;

import cn.lary.core.constant.ApiCodeEnum;
import cn.lary.core.model.vo.ApiRes;
import lombok.Getter;

import java.io.Serial;

/**
 * @author paul 2024/4/9
 */

@Getter
public class BizException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;
    private final ApiRes apiRes;

    public BizException(String msg) {
        super(msg);
        this.apiRes = ApiRes.customFail(msg);
    }

    public BizException(ApiCodeEnum apiCodeEnum, String... params) {
        super();
        apiRes = ApiRes.fail(apiCodeEnum, params);
    }

    public BizException(ApiRes apiRes) {
        super(apiRes.getMsg());
        this.apiRes = apiRes;
    }
}