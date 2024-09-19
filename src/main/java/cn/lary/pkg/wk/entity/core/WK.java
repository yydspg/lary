package cn.lary.pkg.wk.entity.core;

public interface WK {
    public interface ChannelType {
        int person = 1; // 个人
        int group = 2; // 群组
        int stream = 3; // 客服
        int community = 4; // 社区
        int communityTopic = 5; // 社区话题
        int info = 6; // 咨讯
        int data = 7; //数据
    }
    interface DeviceLevel {
        int slave = 0;
        int master = 1;
    }
    interface DeviceFlag {
        int app = 0;
        int web = 1;
        int pc = 2;
    }
    interface UpdateTokenStatus {
        int success = 200;
        int ban = 19;
    }
}
