package cn.lary.module.pay.kit;

import cn.lary.module.common.constant.LARY;

public class PayBizKit {
    public static  String getPayClient(Integer clientType) {
        return switch(clientType) {
            case LARY.ClientType.web -> "web";
            case LARY.ClientType.app -> "app";
            default -> null;
        };
    }
    public static String getPayWay(Integer payWay) {
        return switch(payWay) {
            case LARY.PayWay.alipay -> "alipay";
            case LARY.PayWay.wechat -> "wechat";
            default -> null;
        };
    }

}
