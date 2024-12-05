package cn.lary.api.user.constant;

public interface DEVICE {
    interface STATUS {
        int COMMON = 1;
        int REMOVE  = 2;
    }
    interface FLAG{
        int APP = 1;
        int PC = 2;
    }
    interface LEVEL {
        int SLAVE = 1;
        int MASTER = 2;
    }

}
