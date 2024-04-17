package cn.lary.oss.channel.aliyun.define.args;

import cn.lary.oss.standard.args.ArgsConverter;
import cn.lary.oss.standard.args.common.BaseArgs;
import com.aliyun.oss.model.WebServiceRequest;

/**
 * @author paul 2024/4/17
 */

public abstract class ArgsToBaseConvert<S extends BaseArgs, T extends WebServiceRequest> implements ArgsConverter<S, T> {
    @Override
    public void prepare(S source, T target) {
      target.setHeaders(source.getExtraHeaders());
      target.setParameters(source.getExtraQueryParams());
    }
}
