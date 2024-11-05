package cn.lary.module.stream.vo;

import cn.lary.module.stream.entity.StreamRecord;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StreamRecordVO {

    private int streamId;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /**
     * 直播简介
     */
    private String remark;


    /**
     * 观看人数
     */
    private int watchNum;

    /**
     * 开播时长以s为单位
     */
    private int duration;


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

    public StreamRecordVO of(StreamRecord record) {
        StreamRecordVO vo = new StreamRecordVO();
        vo.setStreamId(record.getStreamId());
        vo.setStartAt(record.getStartAt());
        vo.setEndAt(record.getEndAt());
        vo.setRemark(record.getRemark());
        vo.setWatchNum(record.getWatchNum());
        vo.setDuration(record.getDuration());
        vo.setNewFansNum(record.getNewFansNum());
        vo.setStarNum(record.getStarNum());
        vo.setWatchFanNum(record.getWatchFanNum());
        vo.setGiftNum(record.getGiftNum());
        vo.setGiftAmount(record.getGiftAmount());
        vo.setIsBlock(record.getIsBlock());
        vo.setBlockTypeId(record.getBlockTypeId());
        vo.setBlockDescription(record.getBlockDescription());
        return vo;
    }
}
