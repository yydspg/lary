package cn.lary.oss.standard.args.obj;

import cn.lary.oss.standard.args.common.impl.ObjWArgs;
import jodd.net.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

/**
 * @author paul 2024/4/19
 */
@Getter
@Setter
public class PresignedUrlArgs extends ObjWArgs {
  private Duration expiration = Duration.ofDays(7);
  private String contentType;
  private String contentMD5;
}
