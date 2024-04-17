package cn.lary.oss.standard.domain.fragmentation;

import cn.lary.oss.standard.domain.common.Frag;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
public class UploadFragCopy extends Frag {
    private String uploadId;
}
