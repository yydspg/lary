package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.*;

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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stream_record")
public class StreamRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    private Long uid;

    private Long channelId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

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

    private Boolean isDelete;

    private Long createBy;

    private Long updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
