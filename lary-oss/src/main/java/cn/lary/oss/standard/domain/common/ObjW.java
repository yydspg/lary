package cn.lary.oss.standard.domain.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ObjW extends Base {
   public ObjW(){}
   public ObjW(String objName) {super(objName);}
   private String etag;
   private String versionId;
   private String uploadId;
}
