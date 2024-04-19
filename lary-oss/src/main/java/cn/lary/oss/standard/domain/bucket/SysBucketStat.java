package cn.lary.oss.standard.domain.bucket;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/19
 */
@Getter
@Setter
public class SysBucketStat {
    Long storageSize;
    Long objectCount;
    Long multipartUploadCount;
    Long lastModifiedTime;
}
