package cn.lary.module.common.serviceImpl;

import cn.lary.module.common.config.SmsConfig;
import cn.lary.module.common.service.SmsService;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    private final SmsConfig smsConfig;


    public SendSmsResponse sendSms(String phoneNumbers, String code)  {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou", // 地域ID
                smsConfig.getAccessKeyId(),
                smsConfig.getAccessKeySecret());

        IAcsClient acsClient = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(smsConfig.getSignName());
        request.setTemplateCode(smsConfig.getTemplateCode());
        Map<String, String> templateParam = new HashMap<>();
        templateParam.put("code", code);
        request.setTemplateParam(new Gson().toJson(templateParam));
        try {
            return acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verify(String code, String zone, String phone, int codeType) {
        return false;
    }
}
