package cn.lary.core.model.vo;

import cn.lary.core.constant.ApiCodeEnum;
import cn.lary.core.util.DateKit;
import cn.lary.core.util.JSONKit;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author paul 2024/4/9
 */



@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiRes implements Serializable {

    private Integer code;
    private String msg;
    private Object data;
    private final long timestamp = DateKit.currentTimeMillis();

    public String toJSONString() {return JSON.toJSONString(this);}

    public static <T> ApiRes success(T data) {
        return new ApiRes(ApiCodeEnum.SUCCESS.getCode(),ApiCodeEnum.SUCCESS.getMsg(),data);
    }
    public static ApiRes success() {
        return success(null);
    }
    public static ApiRes successWithJSON(String k,Object v) {
        return success(JSONKit.convert(k,v));
    }

    public static ApiRes customFail(String customMsg) {
        return new ApiRes(ApiCodeEnum.CUSTOM_FAIL.getCode(), ApiCodeEnum.CUSTOM_FAIL.getMsg(), null);
    }
    public static ApiRes fail(ApiCodeEnum ace,String... params) {
        return new ApiRes(ace.getCode(),(params == null || params.length == 0) ? ace.getMsg():String.format(ace.getMsg(),params),null);
    }
}
