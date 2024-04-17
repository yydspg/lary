package cn.lary.oss.standard.args.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 *
 */
@Getter
@Setter
public abstract class BucketArgs extends BaseArgs{

    private String bucketName;

    private String region;
}
