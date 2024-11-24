package cn.lary.group.constant;

public interface GROUP {
    interface ROLE {
        int COMMON = 1001;
        int CREATOR = 1002;
        int MANAGER = 1003;
    }
    interface STATUS {
        int DISBAND = 1;
        int COMMON = 2;
    }
    interface MEMBER {
        interface STATUS {
            int INIT = 1001;
            int COMMON = 1002;
            int QUIT = 1003;
            int BLOCK = 1004;
            int DISBAND = 1005;
        }
    }
}
