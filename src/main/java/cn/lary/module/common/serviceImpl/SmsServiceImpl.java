package cn.lary.module.common.serviceImpl;

import cn.lary.module.common.config.SmsConfig;
import cn.lary.module.common.service.SmsService;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsConfig smsConfig;


    public SendSmsResponse sendSms(String phoneNumbers, String code)  {
        return null;
    }

    @Override
    public boolean verify(String code, String zone, String phone, int codeType) {
        return false;
    }
}
