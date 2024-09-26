package cn.lary.module.pay.core;

import java.util.Map;

public interface PayCallback {

    void onSuccess(Map<String,String> map);
    void onFail(Map<String,String> map);
    Integer getBiz();
}
