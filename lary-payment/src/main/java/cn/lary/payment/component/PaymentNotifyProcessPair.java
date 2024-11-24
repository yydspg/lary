package cn.lary.payment.component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.util.Map;

@Data
public abstract class PaymentNotifyProcessPair {

    private HttpServletRequest request;

    private Map<String,String> params;

    private int paymentPlugin;

    private int businessSign;

}
