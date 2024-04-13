package cn.lary.core.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author paul 2024/4/13
 */

@Data
@Builder
public class UploadRes {

    private String url;

    private String filename;
}
