package cn.lary.module.common.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;


public interface SmsService {
    public SendSmsResponse sendSms(String phoneNumbers, String code);
    public boolean  verify(String code,String zone,String phone,int codeType);

}