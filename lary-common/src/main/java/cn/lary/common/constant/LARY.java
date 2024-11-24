package cn.lary.common.constant;

public interface LARY {

    int REMOVE  = 3;
    int COMMON = 2;

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
        interface STREAM {
            int UP = 1;
            int DOWN = 2;
        }
    }
}
