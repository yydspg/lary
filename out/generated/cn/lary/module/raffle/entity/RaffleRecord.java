package cn.lary.module.raffle.entity;

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
 * @since 2024-10-31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("raffle_record")
public class RaffleRecord implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 直播id
     */
    private Long streamId;

    /**
     * 用户id
     */
    private Long toUid;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 物品数目
     */
    private Integer itemNum;

    /**
     * 同步状态
     */
    private Integer syncStatus;

    private Boolean isDelete;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
