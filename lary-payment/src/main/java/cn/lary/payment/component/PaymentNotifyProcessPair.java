package cn.lary.payment.component;


import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Data
public abstract class PaymentNotifyProcessPair {

    private HttpServletRequest request;

    private Map<String,String> params;

    private int paymentPlugin;

    private int businessSign;

}
