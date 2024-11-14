package cn.lary.module.auth.core;

import cn.lary.common.kit.StringKit;
import lombok.AllArgsConstructor;
import lombok.Getter;


import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum AuthUserGender {

    MALE("1", "男"),
    FEMALE("0", "女"),
    UNKNOWN("-1", "未知");

    private String code;
    private String desc;


    public static AuthUserGender getRealGender(String originalGender) {
        if (null == originalGender || UNKNOWN.getCode().equals(originalGender)) {
            return UNKNOWN;
        }
        String[] males = {"m", "男", "1", "male"};
        if (Arrays.asList(males).contains(originalGender.toLowerCase())) {
            return MALE;
        }
        return FEMALE;
    }


    public static AuthUserGender getWechatRealGender(String originalGender) {
        if (StringKit.isEmpty(originalGender) || "0".equals(originalGender)) {
            return AuthUserGender.UNKNOWN;
        }
        return getRealGender(originalGender);
    }
}
