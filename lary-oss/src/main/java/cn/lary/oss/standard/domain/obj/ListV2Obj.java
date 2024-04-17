package cn.lary.oss.standard.domain.obj;

import cn.lary.oss.standard.args.obj.ListV2ObjArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ListV2Obj extends ListV2ObjArgs {
    private List<Obj> summaries;
    private String nextContinuationToken;
    private Boolean truncated;
    private int keyCount;
}
