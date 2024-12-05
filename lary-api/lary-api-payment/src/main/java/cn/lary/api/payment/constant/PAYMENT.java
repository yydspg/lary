package cn.lary.api.payment.constant;

public interface PAYMENT {
    interface STATUS {
        int INIT = 1;
        int COMMIT = 2;
        int STOP = 3;
        int FAIL = 4;
        int FINISH = 5;
    }
    interface PLUGIN{
        int ALI = 301;
        int WECHAT = 302;
    }
    interface WAY {
        int PC = 401;
        int APP = 402;
    }
}
