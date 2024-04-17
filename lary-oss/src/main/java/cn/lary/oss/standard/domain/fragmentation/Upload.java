package cn.lary.oss.standard.domain.fragmentation;

import cn.lary.oss.standard.domain.common.Owner;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
public class Upload {
    private String key;
    private String uploadId;
    private Owner owner;
    private Owner initiator;
    private String storageClass;
    private Date initiated;
}
