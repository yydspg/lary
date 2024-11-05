package cn.lary.module.event.dto;


import cn.lary.module.common.entity.Event;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class AbstractEventData {

    private int category;

    private int type;

    private String data;

    public abstract String getData();

    public abstract int getCategory();

    public abstract int getType();

    public final Event convert() {
        return  new Event()
                .setCategory(getCategory())
                .setType(getType())
                .setData(getData());
    }

}
