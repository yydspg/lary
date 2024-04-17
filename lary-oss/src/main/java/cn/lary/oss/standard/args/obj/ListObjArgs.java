package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.BucketArgs;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ListObjArgs extends BucketArgs {

    private String prefix;
    private String marker;
    private String delimiter = "";
    private Integer maxKeys = 1000;
    private String encodingType;
    private Boolean recursive = false;

}
