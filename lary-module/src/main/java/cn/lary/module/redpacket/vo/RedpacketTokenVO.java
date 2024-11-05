package cn.lary.module.redpacket.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RedpacketTokenVO {

    private long streamId;

    private int status;

    private String token;

}
