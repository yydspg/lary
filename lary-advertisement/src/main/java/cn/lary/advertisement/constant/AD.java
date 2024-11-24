package cn.lary.advertisement.constant;

public interface AD {
    interface STATUS {
        int INIT = 1001;
        int COMMON = 1002;
        int FINISH = 1003;
        int FAIL = 1004;
    }
    interface PROVIDER {
        int COMMON = 1004;
        int BLOCK  = 1005;
        int DISBAND = 1006;
        int SUPER = 1007;
    }
    interface LEVEL {
        int COMMON = 1;
        int SUPER = 2;
    }
}
