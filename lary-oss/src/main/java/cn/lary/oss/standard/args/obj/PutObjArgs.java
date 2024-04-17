package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.impl.PutObjBaseArgs;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class PutObjArgs extends PutObjBaseArgs {
    private InputStream inputStream;
    private String filename;
}
