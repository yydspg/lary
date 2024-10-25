package cn.lary.module.app.service;

import cn.lary.module.app.entity.Event;
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
