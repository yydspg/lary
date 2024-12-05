package cn.lary.api.user.dto;


import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class DeviceAddDTO {

    @JSONField(format="name")
    private String name;

    @JSONField(format="flag")
    private int flag;

    private long uid;

    private String phone;
}
