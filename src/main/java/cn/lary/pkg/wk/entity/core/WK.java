package cn.lary.pkg.wk.entity.core;

public interface WK {
    public interface ChannelType {
        byte person = 1; // 个人
        byte group = 2; // 群组
        byte customerService = 3; // 客服
        byte community = 4; // 社区
        byte communityTopic = 5; // 社区话题
        byte info = 6; // 咨讯
        byte data = 7; //数据
    }
    interface DeviceLevel {
        byte slave = 0;
        byte master = 1;
    }
    interface DeviceFlag {
        byte app = 0;
        byte web = 1;
        byte pc = 2;
    }
    interface UpdateTokenStatus {
        int success = 200;
        int ban = 19;
    }
}
