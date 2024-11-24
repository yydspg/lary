package cn.lary.user.constant;

public interface USER {
    interface STATUS {
        int BAN = 1;
        int OK = 2;
        int DESTROY = 3;
    }
    interface SEX {
        int MAN = 1;
        int WOMAN = 2;
    }
    interface VERIFY_CODE {
        int QR = 12;
        int USER = 13;
    }
    interface ROLE {
        int ADMIN = 14;
        int COMMON = 15;
        int VC = 16;
    }
}
