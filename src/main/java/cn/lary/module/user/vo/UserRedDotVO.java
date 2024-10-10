package cn.lary.module.user.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRedDotVO {
    private Integer count;

    private String category;
    /**
     * 是否显示
     */
    @JsonProperty("is_dot")
    private Boolean isDot;
}
