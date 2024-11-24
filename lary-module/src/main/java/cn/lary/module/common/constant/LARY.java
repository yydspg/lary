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
        interface EVENT {
            int UP = 1;
            int DOWN = 2;
        }
    }

    interface PAYMENT {

    }

    interface REDPACKET {

    }
    interface DEVICE {

    }
    interface RAFFLE {

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

    }
    interface GROUP {

    }

    interface COMMENT {

    }
    interface AD {

    }
    interface INVEST {

    }
}
