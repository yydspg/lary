package cn.lary.module.app.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EventData {
    private String event;

    private Short type;

    private String data;
}
