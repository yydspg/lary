package cn.lary.raffle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
     * 事件id
     */
    private Long eid;
    /**
     * 直播id
     */
    private Long sid;

    /**
     * 直播间id
     */
    private Long rid;



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
    private Integer sync;

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

    private long createAt;

}
