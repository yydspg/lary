package cn.lary.module.user.dto.req;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendApplyReq extends DTO {
    @JsonProperty("to_uid")
    @NotNull(message = "to uid is null")
    private String toUID; //被申请 uid
    private String remark; // 备注
    private String vercode;//验证码
}

