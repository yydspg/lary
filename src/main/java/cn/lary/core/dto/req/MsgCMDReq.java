package cn.lary.core.dto.req;


import cn.lary.kit.JSONKit;
import cn.lary.module.common.CS.Lary;
import cn.lary.pkg.wk.dto.message.MessageHeader;
import cn.lary.pkg.wk.dto.message.MessageSendDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

@Data
@Accessors(chain = true)
public class MsgCMDReq {
    private String fromUID;
    private String chanelID;
    private int channelType;
    // 订阅者
    private List<String> subscribers;
    private HashMap<String,String> param;
    private String cmd;
    private boolean isPersist;

    public MessageSendDTO convertCMD() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("cmd",cmd);
        map.put("type", Lary.ContentType.CMD);
        if (getParam() != null) {
            map.put("param",param);
        }
        short noPersist = 0;
        if (!isPersist()) {
            noPersist = 1;
        }
        byte[] payload = JSONKit.toJSON(map).getBytes();
        MessageSendDTO r = new MessageSendDTO();
        r.setPayload(payload);

        r.setChannelType(channelType);
        r.setSubscribers(subscribers);
        MessageHeader header = new MessageHeader();
        header.setNoPersist(noPersist);
        header.setSyncOnce(1);
        r.setHeader(header);
        return r;
    }
}
