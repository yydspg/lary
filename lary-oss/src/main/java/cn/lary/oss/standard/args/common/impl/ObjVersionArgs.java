package cn.lary.oss.standard.args.common.impl;

import cn.lary.oss.standard.args.common.ObjArgs;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class ObjVersionArgs extends ObjArgs {
    private String versionId;
}
