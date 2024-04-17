package cn.lary.oss.standard.domain.obj;

import cn.lary.oss.standard.domain.common.Base;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ObjOps extends Base {
    private InputStream objectContent;
    private byte ops = 0;

}
