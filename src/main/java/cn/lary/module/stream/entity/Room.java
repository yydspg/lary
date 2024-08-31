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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long anchorUid;

    /**
     * srs url
     */
    private String url;

    private Long streamType;

    /**
     * 直播地址
     */
    private String streamUrl;

    /**
     * 弹幕流id
     */
    private String channelId;

    /**
     * 是否在直播
     */
    private Boolean alive;

    /**
     * 当前直播简介
     */
    private String remark;

    /**
     * 是否是著名主播
     */
    private Boolean isHot;

    /**
     * 是否被封禁
     */
    private Boolean isBlock;

    /**
     * 封禁类型
     */
    private String blockType;

    private Boolean isDelete;

    private Long createBy;

    private Long updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
