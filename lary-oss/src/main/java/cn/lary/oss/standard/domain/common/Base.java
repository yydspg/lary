package cn.lary.oss.standard.domain.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public abstract class Base {
    public Base(){}
    public Base(String objectName) {this.objectName = objectName;}
    private String bucketName;
    private String region;
    private String objectName;
    private Map<String, Object> header = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}
