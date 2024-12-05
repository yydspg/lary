package cn.lary.api.stream.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
     *
     */
    private Long rid;
    /**
     * 用户id
     */
    private Long uid;

    /**
     * 直播标签
     */
    private Integer tag;

    /**
     * 主播级别
     */
   private Integer level;

    /**
     * 封面地址
     */
    private String cover;

    /**
     * 评分
     */
    private Long score;
    /**
     * 行为侧评分
     */
    private Long behaviorScore;
    /**
     * 内容评分
     */
    private Long contentScore;
    /**
     *效益评分
     */
    private Long benefitScore;

    /**
     * 直播状态,是否被封禁
     */
    private Integer status;

    /**
     * 封禁详情
     */
    private String blockDescription;

    /**
     * 被拉黑数目
     */
    private Long blockNum;
    /**
     * 被关注数目
     */
    private Long followNum;
    /**
     * 介绍
     */
    private String remark;
    /**
     * 上次直播时间
     */
    private Long lastLogin;

    

}
