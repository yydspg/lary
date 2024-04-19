package cn.lary.oss.standard.args.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class BaseArgs {
    private Map<String ,String> extraHeaders;
    private Map<String,String> extraQueryParams;
}
