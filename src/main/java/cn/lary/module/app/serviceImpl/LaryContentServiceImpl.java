package cn.lary.module.app.serviceImpl;

import cn.lary.kit.JSONKit;
import cn.lary.module.app.service.LaryContentService;
import cn.lary.module.common.CS.LaryContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class LaryContentServiceImpl implements LaryContentService {
    public String buildFriendApplyAckMessage(String content){
        HashMap<String, String> map = new HashMap<>() ;
        map.put("content",content);
        map.put("type", LaryContent.System.tip);
        return JSONKit.toJSON(map);
    }
}
