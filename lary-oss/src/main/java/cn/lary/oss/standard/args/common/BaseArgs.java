package cn.lary.oss.standard.args.common;

import cn.lary.oss.standard.args.Args;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class BaseArgs implements Args {
    private Map<String ,String> extraHeaders;
    private Map<String,String> extraQueryParams;
}
