package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("red_packet")
public class RedPacket implements Serializable {

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
    private Integer streamId;

    /**
     * 虚拟货币数量
     */
    private Long vc;

    /**
     * 红包数目
     */
    private Integer num;

    /**
     * 红包总金额
     */
    private Long vcAll;

    /**
     * 是否同步成功
     */
    private Boolean isSync;

    /**
     * 红包标题
     */
    private String title;
    /**
     * 中奖名单
     */
    private String winningList;

    private Boolean isDelete;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}

