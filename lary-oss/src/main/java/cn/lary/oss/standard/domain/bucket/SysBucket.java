package cn.lary.oss.standard.domain.bucket;

import cn.lary.oss.standard.domain.common.Owner;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class SysBucket {
    private String bucketName;
    private Owner owner;
    private Date createTime;
}
