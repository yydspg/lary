package cn.lary.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenDTO {
    @NotNull(message = "device id is empty")
    private Integer deviceId;
    @NotNull(message = "device flag is empty")
    private Integer flag;
    @NotNull(message = "user role is empty")
    private Integer role;
}
