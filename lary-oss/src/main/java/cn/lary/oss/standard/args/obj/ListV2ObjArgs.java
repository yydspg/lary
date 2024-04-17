package cn.lary.oss.standard.args.obj;

import lombok.Getter;
import lombok.Setter;

/**
 * @author paul 2024/4/17
 */
@Getter
@Setter
public class ListV2ObjArgs extends ListObjArgs {
    private String continuationToken;
    private Boolean fetchOwner = false;
    private String versionIdMarker;
}
