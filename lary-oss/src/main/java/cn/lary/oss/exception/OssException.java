package cn.lary.oss.exception;

import cn.lary.core.constant.ApiCode;
import cn.lary.core.model.vo.ApiRes;

import java.io.Serial;

/**
 * @author paul 2024/4/17
 */

public class OssException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ApiRes apiRes;
    public OssException(String msg) {
        super(msg);
        this.apiRes = ApiRes.customFail(msg);

    }
    public OssException(ApiCode apiCode, String... params) {
        super();
        apiRes = ApiRes.fail(apiCode, params);
    }

}
