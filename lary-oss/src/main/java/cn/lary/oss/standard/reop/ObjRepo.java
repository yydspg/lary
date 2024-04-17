package cn.lary.oss.standard.reop;

import cn.lary.oss.standard.args.obj.GetObjArgs;
import cn.lary.oss.standard.args.obj.PutObjArgs;
import cn.lary.oss.standard.domain.common.ObjW;
import cn.lary.oss.standard.domain.obj.ObjMeta;

/**
 * @author paul 2024/4/17
 */

public interface ObjRepo {
    ObjW upload(PutObjArgs args);
    ObjMeta download(GetObjArgs args);
}
