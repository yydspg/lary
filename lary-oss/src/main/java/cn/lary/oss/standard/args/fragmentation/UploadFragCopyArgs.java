package cn.lary.oss.standard.args.fragmentation;

import cn.lary.oss.standard.args.common.BaseFragArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class UploadFragCopyArgs extends BaseFragArgs {
    private final List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    private int partNumber;
    private String destinationBucketName;
    private String destinationObjectName;
    private List<String> matchingETagConstraints = new ArrayList<>();
    private Date modifiedSinceConstraint;
    private Date unmodifiedSinceConstraint;
}
