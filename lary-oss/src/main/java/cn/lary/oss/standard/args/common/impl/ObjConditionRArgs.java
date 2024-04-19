package cn.lary.oss.standard.args.common.impl;

import cn.lary.oss.standard.args.common.ObjArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class ObjConditionRArgs extends ObjArgs {
    private Long offset;

    private Long length;

    private List<String> notMatchEtag;

    private List<String> matchETag;

    private Date modifiedSince;

    private Date unmodifiedSince;
}
