package cn.lary.api.payment.dto;

import lombok.Data;

import java.util.Map;

@Data
public abstract class CallbackParam {

    private boolean ok;

    private String code;
    private String msg;
    private String subCode;
    private String subMsg;

    /**
     * 自定义实现时，使用以下code
     super.setOk(OK);
     super.setCode(args.get("code"));
     super.setMsg(args.get("msg"));
     super.setSubCode(args.get("subCode"));
     super.setSubMsg(args.get("subMsg"));
     * @param ok 是否成功
     * @param args 参数
     * @return 实现类
     */
    abstract protected  CallbackParam of(boolean ok,Map<String,String> args);
}
