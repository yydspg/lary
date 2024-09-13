package cn.lary.module.pay.plugin;

import cn.lary.core.exception.SysException;
import cn.lary.module.pay.dto.PayBuildRes;
import cn.lary.module.pay.dto.PayParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 不同 支付服务商 支付需提供的支付逻辑
 */
public interface Payment {
    /**
     * pc 支付，需要重定向,返回 form表单
     * @param response {@link HttpServletResponse}
     * @param payParam {@link PayParam} 核心支付参数
     * @return 返回 VO
     */
    default PayBuildRes pcPay( HttpServletResponse response, PayParam payParam) {
        throw new SysException("no support pc pay");
    }

    /**
     * app 支付
     * @param payParam {@link PayParam} 核心支付参数
     * @return VO
     */
    default PayBuildRes appPay( PayParam payParam) {
        throw new SysException("no support app pay");
    }

    /**
     * 回调接口
     * @param request {@link HttpServletRequest}
     */
    default void callBack(HttpServletRequest request) {
        throw new SysException("no support callBack");
    }
    /**
     * 异步通知接口
     * @param request {@link HttpServletRequest}
     */
    default void notify(HttpServletRequest request) {
        throw new SysException("no support notify");
    }
    default String callBackUrl(String api, String method) {
        return api + "/pay/callback/" + method;
    }
    default String notifyUrl(String api,String method) {
        return api + "/pay/notify/" + method;
    }

    /**
     * 获取 当前支付方式的名字
     * @return 支付名称
     */
    String getPluginName();
}
