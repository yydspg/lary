package cn.lary.oss.standard.service;

import cn.lary.oss.standard.args.obj.CreateObjArgs;
import cn.lary.oss.standard.args.obj.DelMultiObjArgs;
import cn.lary.oss.standard.args.obj.DelObjArgs;
import cn.lary.oss.standard.args.obj.GetObjArgs;
import cn.lary.oss.standard.domain.obj.Obj;

/**
 * @author paul 2024/4/17
 */

public interface FragService {
    Obj create(CreateObjArgs args);
    void del(DelObjArgs args);
    void del(DelMultiObjArgs args);
    Object get(GetObjArgs args);
    void rename(CreateObjArgs args);
    boolean exist(GetObjArgs args);

}
