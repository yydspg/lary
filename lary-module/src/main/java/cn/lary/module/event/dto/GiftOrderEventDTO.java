package cn.lary.module.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GiftOrderEventDTO extends AbstractEventData {


    @Override
    public String getData() {
        return "";
    }

    @Override
    public int getCategory() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }
}
