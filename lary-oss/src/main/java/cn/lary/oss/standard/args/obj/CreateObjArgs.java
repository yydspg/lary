package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.impl.ObjWArgs;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class CreateObjArgs extends ObjWArgs {
    private InputStream inputStream;
    private String rename;
    private String filename;
}
