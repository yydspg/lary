package cn.lary.oss.standard.domain.obj;

import cn.lary.oss.standard.domain.bucket.SysBucket;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class Obj extends SysBucket {
    private String objectName;
    private String eTag;
    private long size;
    private Date lastModified;
    private String storageClass;
    private boolean isDir;
    private String versionId;
}
