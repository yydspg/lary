package cn.lary.module.common.CS;

public interface Lary {


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
        short man = 1;
        short woman = 0;
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
        String streamStart = "streamStart";
    }
    interface EventType {
        short none = 0;
        short message = 1;
        short cmd = 2;
    }
    interface UserStatus {
        short ban = 1;
        short ok = 0;
    }
    interface Group {
        interface Role {
            short common = 0;
            short creator = 1;
            short manager = 2;
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
    }

}
