package cn.lary.module.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReportDTO {
    /**
     * 举报id
     */
    private Integer id;
    /**
     * 举报类型id
     */
    @JsonProperty(value = "type_id")
    private Integer typeId;
    /**
     * 举报备注
     */
    private String remark;


}
