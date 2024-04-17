package cn.lary.oss.standard.args.common.impl;

import cn.lary.oss.standard.args.common.ObjArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class ObjWArgs extends ObjArgs {
    private Map<String, String> requestHeaders;
    private Map<String, String> metadata;
}
