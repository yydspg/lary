package cn.lary.module.pay.core;

import java.util.Map;

public interface PayCallbackVO {
    PayCallbackVO of(Map<String, String> params,int payWay);
}
