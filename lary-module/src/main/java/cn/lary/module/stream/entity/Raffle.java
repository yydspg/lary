package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-10-02
 */
@Getter
@Setter
@Accessors(chain = true)
public class Raffle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 直播id
     */
    private Integer streamId;

    /**
     * 抽奖类型
     */
    private Integer type;

    /**
     * 抽奖条件参数
     */
    private String param;

    /**
     * 抽奖人数
     */
    private Integer num;

    /**
     * 是否同步成功
     */
    private Boolean isSync;
    /**
     * 持续时间
     */
    private Long duration;
    /**
     * 抽奖标题
     */
    private String title;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 物品数目
     */
    private Integer itemNum;
    /**
     * 中奖者
     */
    private String recipients;

    private Boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
