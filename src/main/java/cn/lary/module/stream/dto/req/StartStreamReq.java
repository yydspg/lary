package cn.lary.module.stream.dto.req;

import cn.lary.core.dto.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StartStreamReq extends DTO {
    @NotNull
    private String zone;

    @NotNull
    private String province;

    @JsonProperty("device_id")
    private String deviceId;
    @NotNull(message = "device_flag is null")
    @JsonProperty("device_flag")
    private int deviceFlag;
    @JsonProperty("type_id")
    @NotNull
    private String streamTypeId;
    @JsonProperty("type_description")
    @NotNull
    private String streamTypeDescription;
}
