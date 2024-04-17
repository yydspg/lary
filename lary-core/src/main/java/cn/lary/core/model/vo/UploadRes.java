package cn.lary.core.model.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/13
 */

@Getter
@Setter
@Builder
public class UploadRes {

    private String url;

    private String filename;
}
