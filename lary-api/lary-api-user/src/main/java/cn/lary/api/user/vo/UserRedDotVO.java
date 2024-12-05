package cn.lary.api.user.vo;

import cn.lary.api.user.entity.UserRedDot;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
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
