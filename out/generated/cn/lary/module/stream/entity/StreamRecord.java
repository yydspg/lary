package cn.lary.module.stream.entity;

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
@TableName("stream_record")
public class StreamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String streamId;

    private Integer uid;

    private String channelId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /**
     * 直播简介
     */
    private String lastRemark;

    /**
     * 观看人数
     */
    private Integer lastWatchNum;

    /**
     * 开播时长以s为单位
     */
    private Integer duration;

    private Long watchNum;

    private Long newFansNum;

    private Long starNum;

    /**
     * 粉丝观看数量
     */
    private Long watchFanNum;

    /**
     * 送礼人数
     */
    private Long giftNum;

    /**
     * 礼物花费
     */
    private Long giftCost;

    /**
     * 是否被封禁
     */
    private Boolean isBlock;

    /**
     * 封禁类型
     */
    private String blockTypeId;

    /**
     * 封禁详情
     */
    private String blockDescription;

    private Boolean isDelete;

    

    

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
