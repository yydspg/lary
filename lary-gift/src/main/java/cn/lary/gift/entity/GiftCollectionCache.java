package cn.lary.gift.entity;

import lombok.Data;

import java.util.List;

@Data
public class GiftCollectionCache {

    private int typeId;

    private String typeName;

    private String avatar;

    private List<GiftCache> gifts;
}
