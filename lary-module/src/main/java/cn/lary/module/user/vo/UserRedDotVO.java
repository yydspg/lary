package cn.lary.module.user.vo;

import cn.lary.module.user.entity.UserRedDot;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRedDotVO {

    private int count;

    private int category;
    /**
     * 是否显示
     */
    @JSONField(format="is_dot")
    private boolean isDot;

    public UserRedDotVO(UserRedDot redDot) {
        this.count = redDot.getCount();
        this.category = redDot.getCategory();
        this.isDot = redDot.getIsDot();
    }
}
