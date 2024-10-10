package cn.lary.module.gift.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("anchor_turnover")
public class AnchorTurnover implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer anchorId;

    private Integer buyUid;

    /**
     * 直播流id
     */
    private Integer streamId;

    /**
     * 单笔收入
     */
    private Long income;

    /**
     * 收入来源web,app
     */
    private Integer clientType;

    /**
     * 交易完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 礼物uid
     */
    private Integer giftId;

    private Integer giftNum;

    /**
     * 是否是粉丝
     */
    private Boolean isFan;

    private Boolean isDelete;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
