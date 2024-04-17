package cn.lary.oss.standard.domain.fragmentation;

import cn.lary.oss.standard.domain.common.Frag;
import cn.lary.oss.standard.domain.common.Owner;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author paul 2024/4/18
 */
@Getter
@Setter
public class ListFrag extends Frag {
    private Owner owner;

    private Owner initiator;

    private String storageClass;

    private Integer maxParts;

    private Integer partNumberMarker;

    private Integer nextPartNumberMarker;

    private Boolean truncated;

    private List<Frag> frags;
}
