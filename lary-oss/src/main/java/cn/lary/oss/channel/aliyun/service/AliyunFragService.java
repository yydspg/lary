package cn.lary.oss.channel.aliyun.service;

import cn.lary.oss.standard.args.obj.CreateObjArgs;
import cn.lary.oss.standard.args.obj.DelMultiObjArgs;
import cn.lary.oss.standard.args.obj.DelObjArgs;
import cn.lary.oss.standard.args.obj.GetObjArgs;
import cn.lary.oss.standard.domain.obj.Obj;
import cn.lary.oss.standard.service.FragService;
import org.springframework.stereotype.Service;

/**
 * @author paul 2024/4/19
 */
@Service
public class AliyunFragService implements FragService {

    @Override
    public Obj create(CreateObjArgs args) {
        return null;
    }

    @Override
    public void del(DelObjArgs args) {

    }

    @Override
    public void del(DelMultiObjArgs args) {

    }

    @Override
    public Object get(GetObjArgs args) {
        return null;
    }

    @Override
    public void rename(CreateObjArgs args) {

    }

    @Override
    public boolean exist(GetObjArgs args) {
        return false;
    }
}
