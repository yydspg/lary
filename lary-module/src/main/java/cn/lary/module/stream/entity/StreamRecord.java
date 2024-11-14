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
 * @since 2024-08-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("stream_record")
public class StreamRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 直播id
     */
    private Long sid;

    /**
     * room id
     */
    private Long rid;
    /**
     * 用户id
     */
    private Long uid;
    /**
     * 频道id
     */
    private Long cid;

    private Long startAt;

    private Long endAt;

    /**
     * 直播简介
     */
    private String remark;
    /**
     * 直播地址
     */
    private String stream;
    /**
     * 唯一辨识符
     */
    private String identify;

    /**
     * 观看人数
     */
    private Long watchNum;

    /**
     * 开播时长以s为单位
     */
    private Long duration;

    /**
     * 新增粉丝
     */
    private Long newFansNum;
    /**
     * 点赞数目
     */
    private Long starNum;

    /**
     * 粉丝观看数量
     */
    private Long watchFanNum;

    /**
     * 送礼人数
     */
    private Long giftNum;

    /**
     * 礼物花费
     */
    private BigDecimal giftAmount;
    /**
     * 直播状态 1 预开播 2 开播 3 预关播 4 关播 5 封禁
     */
    private Integer status;


    /**
     * 封禁类型
     */
    private String tid;

    /**
     * 封禁详情
     */
    private String description;

    private Long createAt;


}
