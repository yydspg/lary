package cn.lary.pkg.wk.dto.channel;

import cn.lary.pkg.wk.entity.core.WKChannel;
import lombok.Data;

import java.util.List;

@Data
public class WhitelistAddReq extends WKChannel {
    private List<String> uids;
}
