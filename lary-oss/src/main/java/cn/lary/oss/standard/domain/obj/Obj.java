package cn.lary.oss.standard.domain.obj;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class Obj {
    private String objKey;
    private String eTag;
    private long size;
    private Date lastModified;
    private String storageClz;
    private boolean isDir;
}
