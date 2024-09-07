package cn.lary.module.stream.dto.res;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JoinStreamRes {
    private String streamId;
    private String streamUrl;
    private String giftBuyRecordId;
    private String danmakuId;
}
