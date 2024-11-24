package cn.lary.raffle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
     * 主播id
     */
    private Long toUid;

    /**
     * 抽奖物品描述
     */
    private String content;

    /**
     * 同步状态
     */
    private Integer sync;

    private long createAt;
 
}
