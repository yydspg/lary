package cn.lary.module.pay.kit;

import cn.lary.module.common.CS.Lary;

public class PayBizKit {
    public static  String getPayClient(Integer clientType) {
        return switch(clientType) {
            case Lary.ClientType.web -> "web";
            case Lary.ClientType.app -> "app";
            default -> null;
        };
    }
    public static String getPayWay(Integer payWay) {
        return switch(payWay) {
            case Lary.PayWay.alipay -> "alipay";
            case Lary.PayWay.wechat -> "wechat";
            default -> null;
        };
    }

}
