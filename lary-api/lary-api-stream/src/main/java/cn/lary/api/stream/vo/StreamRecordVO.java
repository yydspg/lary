package cn.lary.api.stream.vo;

import cn.lary.api.stream.entity.StreamRecord;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StreamRecordVO {

    private long sid;

    private long startAt;

    private long endAt;

    /**
     * 直播简介
     */
    private String remark;


    /**
     * 观看人数
     */
    private long watchNum;

    /**
     * 开播时长以s为单位
     */
    private long duration;


    private long newFansNum;

    private long starNum;

    /**
     * 粉丝观看数量
     */
    private long watchFanNum;

    /**
     * 礼物数量
     */
    private long giftNum;

    /**
     * 礼物花费
     */
    private BigDecimal giftAmount;


    /**
     * 封禁类型
     */
    private String blockTypeId;

    /**
     * 封禁详情
     */
    private String description;

    private int status;

    public StreamRecordVO() {}
    public StreamRecordVO(StreamRecord record) {
       setSid(record.getSid());
       setStartAt(record.getStartAt());
       setEndAt(record.getEndAt());
       setRemark(record.getRemark());
       setWatchNum(record.getWatchNum());
       setDuration(record.getDuration());
       setNewFansNum(record.getNewFansNum());
       setStarNum(record.getStarNum());
       setWatchFanNum(record.getWatchFanNum());
       setGiftNum(record.getGiftNum());
       setGiftAmount(record.getGiftAmount());
       setStatus(record.getStatus());
       setBlockTypeId(record.getTid());
       setDescription(record.getDescription());
    }
}
