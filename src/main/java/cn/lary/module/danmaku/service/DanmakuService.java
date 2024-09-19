package cn.lary.module.danmaku.service;

import cn.lary.pkg.wk.entity.core.WKChannel;

public interface DanmakuService {
    WKChannel getDanmakuChannel(String uid);
    boolean exist(String channelId,String uid);
    void addCache(String channelId, String uid);
}
