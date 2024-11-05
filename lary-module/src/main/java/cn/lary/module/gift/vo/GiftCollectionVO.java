package cn.lary.module.gift.vo;

import cn.lary.module.gift.entity.GiftCollectionCache;
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


    public GiftCollectionVO() {}

    public GiftCollectionVO(GiftCollectionCache cache) {
        this.typeId = cache.getTypeId();
        this.typeName = cache.getTypeName();
        this.avatar = cache.getAvatar();
        this.gifts = cache.getGifts()
                .stream()
                .map(GiftVO::new)
                .toList();
    }
}
