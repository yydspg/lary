package cn.lary.oss.standard.args.fragmentation;

import cn.lary.oss.standard.args.common.BucketArgs;
import cn.lary.oss.standard.args.common.impl.ObjWArgs;
import cn.lary.oss.standard.domain.common.Frag;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class UploadFragArgs extends ObjWArgs {
    private byte ops = 0;
    private int partNumber;
    private Long partSize;
    private InputStream inputStream;
    private String md5Digest;
    private String uploadId;
    private List<Frag> frags;
}
