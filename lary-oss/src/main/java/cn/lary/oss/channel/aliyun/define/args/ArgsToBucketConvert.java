package cn.lary.oss.channel.aliyun.define.args;

import cn.lary.oss.standard.args.common.BucketArgs;
import com.aliyun.oss.model.WebServiceRequest;

/**
 * @author paul 2024/4/17
 */

public abstract class  ArgsToBucketConvert<S extends BucketArgs, T extends WebServiceRequest> extends ArgsToBaseConvert<S, T> {

}
