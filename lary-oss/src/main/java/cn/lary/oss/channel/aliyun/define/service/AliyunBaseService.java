package cn.lary.oss.channel.aliyun.define.service;

import cn.lary.core.model.base.AbstractObjPool;
import cn.lary.oss.channel.aliyun.define.pool.AliyunClientPool;
import cn.lary.oss.standard.service.OssService;
import com.aliyun.oss.OSS;

/**
 * @author paul 2024/4/17
 */

public class AliyunBaseService extends OssService<OSS> {

    public AliyunBaseService(AliyunClientPool objectPool) {
        super(objectPool);
    }
}
