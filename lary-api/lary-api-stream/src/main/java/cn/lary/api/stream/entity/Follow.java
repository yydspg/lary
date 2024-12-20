package cn.lary.api.stream.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
//    /**
//     * relation id
//     */
//    private Long sid;
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

    private long createAt;
}
