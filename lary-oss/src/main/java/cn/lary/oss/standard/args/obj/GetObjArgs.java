package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.ObjArgs;
import cn.lary.oss.standard.args.common.impl.ObjConditionRArgs;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class GetObjArgs extends ObjArgs {
    private String filename;
}
