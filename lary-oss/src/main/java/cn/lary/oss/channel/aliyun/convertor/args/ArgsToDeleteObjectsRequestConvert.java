package cn.lary.oss.channel.aliyun.convertor.args;

import cn.lary.oss.channel.aliyun.define.args.ArgsToBucketConvert;
import cn.lary.oss.channel.aliyun.define.args.ArgsToGenericRequestConvert;
import cn.lary.oss.standard.args.obj.DelMultiObjArgs;
import cn.lary.oss.standard.args.obj.DelObjArgs;
import com.aliyun.oss.model.DeleteObjectsRequest;

import java.util.List;

/**
 * @author paul 2024/4/17
 */

public class ArgsToDeleteObjectsRequestConvert extends ArgsToBucketConvert<DelMultiObjArgs, DeleteObjectsRequest> {

    @Override
    public DeleteObjectsRequest get(DelMultiObjArgs source) {
        return new DeleteObjectsRequest(source.getBucketName());
    }

    @Override
    public void prepare(DelMultiObjArgs source, DeleteObjectsRequest target) {
        List<DelObjArgs> delObjArgs = source.getObjects();
       if(delObjArgs != null && !delObjArgs.isEmpty()) {
            target.setKeys(delObjArgs.stream().map(DelObjArgs::getObjectName).toList());
       }

        target.setQuiet(source.getQuiet());
        super.prepare(source, target);
    }
}
