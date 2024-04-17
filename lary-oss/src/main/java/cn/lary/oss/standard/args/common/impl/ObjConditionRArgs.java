package cn.lary.oss.standard.args.common.impl;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class ObjConditionRArgs extends ObjRArgs {
    private Long offset;

    private Long length;

    private List<String> notMatchEtag;

    private List<String> matchETag;

    private Date modifiedSince;

    private Date unmodifiedSince;
}
