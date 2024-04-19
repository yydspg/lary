package cn.lary.oss.standard.service;

import cn.lary.oss.standard.args.obj.*;
import cn.lary.oss.standard.domain.obj.ListObj;
import cn.lary.oss.standard.domain.obj.ListV2Obj;
import cn.lary.oss.standard.domain.obj.Obj;
import com.aliyun.oss.model.BucketStat;

import java.time.Duration;
import java.util.List;

/**
 * @author paul 2024/4/17
 */

public interface ObjService {

    ListV2Obj listV2(ListV2ObjArgs args);
    ListObj list(ListObjArgs args);
    Obj create(CreateObjArgs args);
    void del(DelObjArgs args);
    void del(DelMultiObjArgs args);
    Object get(GetObjArgs args);
    void rename(CreateObjArgs args);
    boolean exist(GetObjArgs args);
    default ListObj list(String bucketName){
        return list(bucketName,null);
    }
    default ListObj list(String bucketName,String prefix){
        ListObjArgs args = new ListObjArgs();
        args.setBucketName(bucketName);
        if (prefix != null) {
            args.setPrefix(prefix);
        }
        return list(args);
    }
    default ListV2Obj listV2(String bucketName){
        return listV2(bucketName,null);
    }
    default ListV2Obj listV2(String bucketName,String prefix){
        ListV2ObjArgs args = new ListV2ObjArgs();
        args.setBucketName(bucketName);
        if(prefix != null) {
            args.setPrefix(prefix);
        }
        return listV2(args);
    }
}
