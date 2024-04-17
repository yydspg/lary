package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.BucketArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class DelMultiObjArgs extends BucketArgs {

    private Boolean bypassGovernanceMode;

    private List<DelObjArgs> objects;

    private Boolean quiet = false;
}
