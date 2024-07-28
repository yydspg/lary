package cn.lary.module.gift.entity.service.entity;

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
 * @since 2024-07-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("gift_buy_record")
public class GiftBuyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user_id
     */
    private String uid;

    /**
     * app_Id
     */
    private String appId;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 是否同步用户数据成功
     */
    private Boolean isSync;

    private Long roomId;

    /**
     * 上游异常原因
     */
    private String failReason;

    /**
     * 直播流id
     */
    private Long streamId;

    private String anchorUid;

    private Integer status;

    /**
     * 订单编号
     */
    private String sn;

    /**
     * 完成时间
     */
    private LocalDateTime completeAt;

    /**
     * 结束时间
     */
    private LocalDateTime endAt;

    /**
     * whether to anchor
     */
    private Boolean isDirect;

    /**
     * cost CNY
     */
    private Long cost;

    /**
     * payment original
     */
    private Short originType;

    private Long giftId;

    private Integer giftNum;

    private Boolean isDelete;

    private String createBy;

    private String updateBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
}
