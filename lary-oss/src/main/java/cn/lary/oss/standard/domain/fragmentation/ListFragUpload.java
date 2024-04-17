package cn.lary.oss.standard.domain.fragmentation;

import cn.lary.oss.standard.args.fragmentation.TabulationUploadFragArgs;
import cn.lary.oss.standard.domain.common.Frag;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
public class ListFragUpload extends TabulationUploadFragArgs {
    private boolean truncated;

    private String nextKeyMarker;

    private String nextUploadIdMarker;

    private List<Upload> multipartUploads = new ArrayList<>();

    private List<String> commonPrefixes = new ArrayList<>();
}
