package cn.lary.module.report.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class ReportDTO {
    /**
     * 举报id
     */
    private Long id;
    /**
     * 举报类型id
     */
    @JSONField(format = "type_id")
    private Integer typeId;
    /**
     * 举报备注
     */
    private String remark;


}
