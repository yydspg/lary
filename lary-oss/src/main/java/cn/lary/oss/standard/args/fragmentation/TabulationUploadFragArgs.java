package cn.lary.oss.standard.args.fragmentation;

import cn.lary.oss.standard.args.common.BucketArgs;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class TabulationUploadFragArgs extends BucketArgs {
    private String delimiter;
    private String prefix;
    private Integer maxUploads;
    private String keyMarker;
    private String uploadIdMarker;
    private String encodingType;
}
