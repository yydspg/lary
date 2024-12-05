package cn.lary.api.stream.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FollowDTO {

    @JSONField(format="to_uid")
    @NotNull
    private Long toUid;

    @NotNull
    private Integer code;
}
