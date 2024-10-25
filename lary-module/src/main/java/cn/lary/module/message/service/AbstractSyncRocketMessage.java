package cn.lary.module.message.service;

import lombok.Data;

@Data
public abstract class AbstractSyncRocketMessage {


    public abstract int getBusinessSign();

    public abstract String getLogData();

    public abstract long getTimeOut();

    public abstract int getDelayLevel();

}
