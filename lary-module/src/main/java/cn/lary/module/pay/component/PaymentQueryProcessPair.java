package cn.lary.module.pay.component;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public  class PaymentQueryProcessPair {

    private int paymentId;

    private int businessSign;

    private int client;

    private int plugin;
}
