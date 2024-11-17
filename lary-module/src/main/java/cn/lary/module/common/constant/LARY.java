package cn.lary.module.common.constant;

public interface LARY {
    boolean testMode = false;
    interface  CORE {
        interface STATUS {
            int COMMON = 1;
            int REMOVE = 2;
        }
    }

    interface STATUS {
        int BLOCK = 45;
        int COMMON = 46;
    }
    interface USER {
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
    interface SYNC_STATUS {
        int INIT = 1;
        int FAIL = 2;
        int SUCCESS = 3;
    }
    interface STREAM {
        interface STATUS {
            int ING = 1;
            int BLOCK = 2;
            int COMMON = 3;
            int UP =4;
            int DOWN =5;
        }
    }

    interface PAYMENT {
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

    interface REDPACKET {
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
    interface DEVICE {
        interface FLAG{
            int APP = 1;
            int PC = 2;
        }
        interface LEVEL {
            int SLAVE = 1;
            int MASTER = 2;
        }

    }
    interface RAFFLE {
        int CUSTOM = 1101;
        int MONEY_RANDOM = 1103;
        int MONEY_FIX = 1104;
    }
    interface BUSINESS {
        interface FILE_UPLOAD {
            int USER_AVATAR = 101;
            int GROUP_AVATAR = 102;
            int ROOM_BACKEND = 103;
        }
        interface PAYMENT {
            int RECHARGE = 201;
            int GIFT = 202;
            int ORDER = 203;
            int AD = 204;
            int INVEST = 205;
        }
        interface TRANSFER {
            int STREAM_GIFT = 501;
            int RAFFLE = 502;
        }
        interface AUTO_CLOSE{
            int    RAFFLE = 401;
            int READ_PACKET = 402;
        }
        interface CACHE{
            int RAFFLE = 1201;
            int REDPACKET = 1201;
        }
    }
    interface CHANNEL {
        interface TYPE {
            int GROUP = 701;
            int STREAM = 702;
            int PERSON = 703;
            int TEMP = 704;
        }
    }
    interface FOLLOW {
        interface STATUS {
            int COMMON = 1;
            int UNFOLLOW = 2;
            int BLOCK = 3;
        }
        interface ROLE {
            int ANCHOR = 1;
            int COMMON = 2;
        }
        interface CODE {
            int STREAM = 1;
        }
    }
    interface EVENT {
        interface TYPE {
            int CMD = 1;
            int MESSAGE = 2;
        }
        interface CATEGORY {
            int DOWN_LIVE = 801;
            int GO_LIVE = 802;
            int RECHARGE_DETECTION = 803;
            int RED_PACKET = 804;
            int GIFT_DETECTION = 805;
            int RAFFLE = 806;
            int USER_REGISTER = 807;
        }
        interface STATUS {
            int INIT = 901;
            int COMMIT = 902;
            int FAIL = 903;
        }
    }
    interface WALLET {
        interface STATUS {
            int BLOCK =  1;
            int COMMON = 2;
            int DANGER = 3;
            int DESTROY =  4;
        }
        interface TRANSFER {
            int POCKET = 1;
            int AMOUNT = 2;
        }
    }
    interface GROUP {
        interface ROLE {
            int COMMON = 1001;
            int CREATOR = 1002;
            int MANAGER = 1003;
        }
        interface STATUS {
            int DISBAND = 1;
            int COMMON = 2;
        }
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
    interface COMMENT {
        interface EVENT{
            int POST = 1;
        }
        interface STATUS {
            int HIDE = 1;
            int COMMON =2;
        }
        interface BEHAVIOR {
            int COMMENT = 1;
            int STAR = 2;
            int HIDE = 3;
        }
    }
    interface AD {
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
    interface INVEST {
        interface STATUS {
            int INIT = 1001;
            int ING = 1002;
            int FINISH = 1003;
        }
    }
}
