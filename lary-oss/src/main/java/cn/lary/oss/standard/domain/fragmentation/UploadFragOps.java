package cn.lary.oss.standard.domain.fragmentation;

import cn.lary.oss.standard.domain.common.ObjW;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class UploadFragOps extends ObjW {
    public UploadFragOps(){super();}
    public UploadFragOps(String objName){super(objName);}
    private byte ops = 0;
}
