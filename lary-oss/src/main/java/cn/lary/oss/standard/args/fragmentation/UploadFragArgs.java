package cn.lary.oss.standard.args.fragmentation;

import cn.lary.oss.standard.args.common.BucketArgs;
import cn.lary.oss.standard.args.common.impl.ObjWArgs;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class UploadFragArgs extends ObjWArgs {
    private byte ops = 0;
}
