package cn.lary.module.gift.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GiftCollectionVO {

    private int typeId;

    private String typeName;

    private String avatar;

    @JsonProperty("item")
    private List<GiftVO> gifts;
}
