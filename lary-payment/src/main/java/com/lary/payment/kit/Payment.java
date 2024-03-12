package com.lary.payment.kit;

import com.lary.common.core.response.ResponseMsg;
import com.lary.payment.kit.dto.PayParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 支付接口
 * @author paul 2024/1/29
 */

public interface Payment {
    default ResponseMsg<Object> h5pay(HttpServletRequest request , HttpServletResponse response, PayParam payParam){
            return null;
    }
}
