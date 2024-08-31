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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid 等同于 room id
     */
    private Long uid;

    private String streamTypeId;

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
    private Boolean isAlive;

    /**
     * 直播简介
     */
    private String lastRemark;

    /**
     * 观看人数
     */
    private Integer lastWatchNum;

    /**
     * 是否是著名主播
     */
    private Boolean isHot;

    /**
     * 是否上传封面
     */
    private Boolean isUploadCover;

    /**
     * 封面地址
     */
    private String coverUrl;

    /**
     * 评分
     */
    private Long score;

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

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
