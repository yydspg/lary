package cn.lary.module.comment.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-11-10
 */
@Getter
@Setter
@Accessors(chain = true)
public class RootComment implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论id
     */
    private Long cid;

    /**
     * 评论事件id
     */
    private Long eid;

    /**
     * 用户uid
     */
    private Long uid;

    /**
     * 用户名
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 被提及用户
     */
    private String mentions;

    /**
     * 评论图片
     */
    private String images;

    /**
     * 评论等级
     */
    private Integer level;

    /**
     * 评论策略
     */
    private Integer policy;

    /**
     * 评论Ip
     */
    private String ipLabel;

    /**
     * 评论状态
     */
    private Integer status;

    /**
     * 回复数量
     */
    private Integer replyCount;

    /**
     * 点赞数量
     */
    private Integer starCount;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Long createAt;
}
