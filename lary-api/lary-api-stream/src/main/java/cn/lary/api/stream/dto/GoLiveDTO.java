package cn.lary.api.stream.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoLiveDTO {
    @NotNull
    private String zone;

    @NotNull
    private String province;
    /**
     * 设备id
     */
    private Long did;

    @NotNull(message = "device_flag is null")
    private int flag;

    @NotNull
    private Integer tag;

    private String remark;
    /*
    直播间id
     */
    private Long rid;
}
