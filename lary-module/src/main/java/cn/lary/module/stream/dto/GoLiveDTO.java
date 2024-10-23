package cn.lary.module.stream.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoLiveDTO {
    @NotNull
    private String zone;

    @NotNull
    private String province;

    @JsonProperty("device_id")
    private Integer deviceId;

    @NotNull(message = "device_flag is null")
    @JsonProperty("device_flag")
    private int deviceFlag;

    @JsonProperty("type")
    @NotNull
    private Integer type;

    private String remark;
}
