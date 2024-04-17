package cn.lary.oss.standard.args.fragmentation;

import cn.lary.oss.standard.args.common.BaseFragArgs;
import cn.lary.oss.standard.args.common.BucketArgs;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class TabulationFragArgs extends BaseFragArgs {
    private Integer maxParts;
    private Integer partNumberMarker;
}
