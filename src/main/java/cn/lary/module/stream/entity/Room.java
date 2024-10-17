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
    private Integer uid;

    private Integer category;


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
    private Integer blockCategory;

    /**
     * 封禁详情
     */
    private String blockDescription;
    /**
     * 被拉黑数目
     */
    private Integer blockNum;
    /**
     * 被关注数目
     */
    private Integer followNum;
    /**
     * 介绍
     */
    private String remark;
    /**
     * 上次直播时间
     */
    private Long lastLogin;

    private Boolean isDelete;

    

    

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
