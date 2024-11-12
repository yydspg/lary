package cn.lary.module.comment.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("comment_event")
public class CommentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论事件id
     */
    private Long eid;

    /**
     * 事件类型id
     */
    private Integer tid;

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 事件描述
     */
    private String content;

    /**
     * 创建时间
     */
      @TableField(fill = FieldFill.INSERT)
    private Long createAt;
}
