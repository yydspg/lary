package cn.lary.module.pay.plugin;

import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.service.PaymentLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * 此类实现了 对 {@link cn.lary.module.pay.service.PaymentLogService} 的内聚，所以在业务调用时，不应手动操作 paymentLogService
 */
@Slf4j
@Component
public class PluginSupport {

    private final HashMap<Integer, Payment> plugins = new HashMap<>();
    private final PaymentLogService paymentLogService;

    @Autowired
    public PluginSupport(List<Payment> payments,PaymentLogService paymentLogService) {
        for (Payment payment : payments) {
            plugins.putIfAbsent(payment.getPayWay(),payment);
        }
        this.paymentLogService = paymentLogService;
    }

    /**
     * 通过 支付方式和对应的支付类型，进行支付
     * @param payClient 客户端
     * @param payParam {@link PayParam}
     * @param payWay 支付方法
     * @return 支付结果
     */
    public PayBuildVO pay(Integer payClient, Integer payWay, PayParam payParam) {
        // build payment log
        PaymentLog log = new PaymentLog().setPayCost(payParam.getPrice()).setPayWay(payWay);
        paymentLogService.save(log);
        Payment payment = plugins.get(payWay);
        return switch (payClient) {
            case 1 -> payment.pcPay(payParam);
            case 2 -> payment.appPay(payParam);
            default -> null;
        };
    }
    /**
     * 聚合 不同的支付方式 回调
     * @param req {@link HttpServletRequest}
     * @param method 支付方法
     */
    public void callback(HttpServletRequest req,int method,int biz){
        plugins.get(method).callBack(req,biz);
    }

    /**
     * 聚合 不同支付方式 异步通知
     * @param req {@link HttpServletRequest}
     * @param method 支付方式
     */
    public void notify(HttpServletRequest req, int method){
        plugins.get(method).notify(req);
    }

}
