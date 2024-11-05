package cn.lary.module.raffle.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("raffle_event")
public class RaffleEvent implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 直播id
     */
    private Long streamId;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 抽奖类型
     */
    private Integer category;

    /**
     * 抽奖条件参数
     */
    private String param;

    /**
     * 抽奖人数
     */
    private Integer num;

    /**
     * 同步状态
     */
    private Integer syncStatus;

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
     * 持续时间
     */
    private Long duration;
    /**
     * 中奖者
     */
    private String recipients;

    private BigDecimal totalAmount;
    private BigDecimal amount;



    private Boolean isDelete;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
