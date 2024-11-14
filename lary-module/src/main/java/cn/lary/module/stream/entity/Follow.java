package cn.lary.module.stream.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author paul
 * @since 2024-09-04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long rid;
    /**
     * uid
     */
    private Long uid;

    /**
     * 被关注id
     */
    private Long toUid;

    /**
     * 被关注用户名
     */
    private String username;
    /**
     * 业务来源
     */
    private Integer source;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 备注
     */
    private String remark;
    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 被关注角色
     */
    private Integer role;

    /**
     * 关注的状态 ,1 正常 2.取消关注 3.拉黑
     */
    private Integer status;

    /**
     * 粉丝等级
     */
    private Integer level;

    /**
     * 对主播的花费
     */
    private BigDecimal amount;

}
