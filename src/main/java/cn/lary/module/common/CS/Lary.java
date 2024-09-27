package cn.lary.module.common.CS;

public interface Lary {
    boolean R = false;

    interface RedisPrefix {
        String sms = "sms:";
        String token = "token:";
    }
    interface CodeType {
        int Register = 0;
        int PWd = 1;
        int ForgetLoginPWD = 2;
        int CheckMobile = 3;
        int DestroyCount = 4;
    }
    interface Sex {
        int man = 1;
        int woman = 2;
    }
    interface VerifyCode {
        int user = 1; //用户搜素
        int groupMember = 2; // 群成员
        int QR = 3; //二维码
        int friend = 4; // 好友
        int mailList = 5; //手机联系人
        int invitation = 6; // 邀请码
        int unknown = 7; // unknown
    }
    interface Event {
        String register = "register";
        String friendApplyAck = "friendApplyAck";
        String groupCreate = "groupCreate";
        String groupMemberAdd = "groupMemberAdd";
        String groupAvatarUpdate = "groupAvatarUpdate";
        String groupUnableAddDestroyAccount = "groupUnableAddDestroyAccount";
        String goLive = "goLive";
        String downLive = "downLive";
        String recharge = "recharge";
    }
    interface EventStatus {
        int init = 0;
        int commit = 1;
    }
    interface EventType {
        int  none = 0;
         int message = 1;
        int  cmd = 2;
    }
    interface UserStatus {
        int ban = 1;
        int ok = 0;
    }
    interface Group {
        interface Role {
            int common = 0;
            int creator = 1;
            int manager = 2;
        }
        interface UserStatus {
            int common = 1;
            int block = 2;
        }
    }
    interface SeqKey {
        String groupMember = "groupMember";
        String group = "group";
        String user = "user";
        String friend = "friend";
        String robot = "robot";
        String userSetting = "userSetting";

    }
    interface RedDot {
        String friendApply = "friendApply";
    }
    interface ApplyStatus {
        short notProcess = 0;
        short ok = 1;
        short refused = 2;
    }
    interface ContentType {
        int CMD = 99;
    }
    interface CMD {
        String friendRequest = "friendRequest";
        String friendApplyAck = "friendApplyAck";
        String typing = "typing";
    }
    interface ClientType {
        int web = 0;
        int app = 1;
    }
    interface RechargeStatus {
        short noPay = 0;
        short paid = 1;
        short cancel = 2;
        short failed = 3;
    }
    interface OrderType {
        int recharge = 1;
        int gift = 2;
    }
    interface PayWay {
        int alipay = 1;
        int wechat = 2;
    }
    interface PayStatus {
        int success = 1;
        int fail = 2;
    }
    interface PostStatus {
        int success = 1;
        int fail = 2;
    }
    interface UserRole{
        int normal = 1;
        int admin = 2;
    }
    interface FollowCode {
        int stream = 1;
    }
    interface Stream {
        interface Status {
            int preUp = 1;
            int up = 2;
            int preDown = 3;
            int down = 4;
        }
    }
    interface OrderStatus {
        int init = 0;
        int commit = 1;
        int stop = 2;
        int fail = 3;
    }
    interface PayBiz {
        int recharge = 1;
        int gift = 2;
    }
}
