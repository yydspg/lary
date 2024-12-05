package cn.lary.api.redpacket.constant;

public interface REDPACKET {
    interface CATEGORY {
        int SUPER = 101;
        int COMMON = 102;
    }
    interface STATUS {
        int INIT = 1;
        int COMMIT = 2;
        int FAIL = 3;
    }
}
