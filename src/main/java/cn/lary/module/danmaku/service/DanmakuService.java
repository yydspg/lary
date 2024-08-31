package cn.lary.module.danmaku.service;

import cn.lary.pkg.wk.entity.core.Channel;

public interface DanmakuService {
    Channel getDanmakuChannel(String uid);
}
