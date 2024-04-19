package cn.lary.oss.standard.domain.obj;

import cn.lary.oss.standard.args.obj.ListObjArgs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ListObj extends ListObjArgs {
    private List<Obj> summaries;
    private String nextMarker;
    private Boolean truncated;
}
