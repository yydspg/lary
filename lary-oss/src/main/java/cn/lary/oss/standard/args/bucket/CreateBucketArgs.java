package cn.lary.oss.standard.args.bucket;

import cn.lary.oss.standard.args.common.BucketArgs;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class CreateBucketArgs extends BucketArgs {
    private boolean objectLock;
}
