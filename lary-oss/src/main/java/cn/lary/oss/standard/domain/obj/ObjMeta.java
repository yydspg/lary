package cn.lary.oss.standard.domain.obj;

import cn.lary.oss.standard.domain.common.ObjW;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ObjMeta extends ObjW {
    private Map<String, String> userMetadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private long contentLength;
    private String contentType;
    private Date LastModified;
}
