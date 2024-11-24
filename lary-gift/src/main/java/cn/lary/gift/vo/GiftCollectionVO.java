package cn.lary.gift.vo;

import cn.lary.gift.entity.GiftCollectionCache;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GiftCollectionVO {

    private int typeId;

    private String typeName;

    private String avatar;

    @JSONField(format="item")
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
