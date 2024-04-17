package cn.lary.oss.standard.args.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */

@Getter @Setter
public abstract class ObjArgs extends BucketArgs{

    private String objectName;
}
