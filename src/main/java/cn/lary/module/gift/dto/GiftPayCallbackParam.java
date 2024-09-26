package cn.lary.module.gift.dto;

import cn.lary.module.pay.dto.CallbackParam;
import lombok.Data;

import java.util.Map;

@Data
public class GiftPayCallbackParam extends CallbackParam {


    @Override
    protected CallbackParam of(boolean ok, Map<String, String> args) {
              super.setOk(ok);
              super.setCode(args.get("code"));
              super.setMsg(args.get("msg"));
              super.setSubCode(args.get("subCode"));
              super.setSubMsg(args.get("subMsg"));
              return this;
    }
}
