package cn.lary.oss.standard.domain.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class Frag extends BaseFrag{
    private long partSize;
    private Date lastModifiedDate;
    private String uploadId;
}
