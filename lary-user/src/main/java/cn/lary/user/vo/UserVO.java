package cn.lary.user.vo;

import cn.lary.user.entity.User;
import lombok.Data;

@Data
public class UserVO {
    /**
     * uid
     */
    private long uid;
    /**
     * setting id
     */
    private long sid;
    /**
     * 直播间id
     */
    private long rid;
    /**
     * 钱包id
     */
    private long wid;

    /**
     * 用户名
     */
    private String username;

    private String phone;

    /**
     * Oath 登陆
     */
    private String eid;

    private String password;

    /**
     * 1 is man,2 is woman
     */
    private Integer sex;


    private String birthday;

    private String zone;

    private String avatar;

    private String regin;

    private String bio;

    private String vercode;

    private String qrVercode;

    private Integer level;

    private String email;

    private int role;

    private int status;

    private String home;
    /**
     * 短编码
     */
    private String shortNo;

    private long createAt;

    public UserVO(){}

    public UserVO(User user) {
        uid = user.getUid();
        sid = user.getSid();
        rid = user.getRid();
        wid = user.getWid();
        username = user.getUsername();
        phone = user.getPhone();
        eid = user.getEid();
        password = user.getPassword();
        birthday = user.getBirthday();
        zone = user.getZone();
        avatar = user.getAvatar();
        regin = user.getRegin();
        bio = user.getBio();
        vercode = user.getVercode();
        qrVercode = user.getQrVercode();
        level = user.getLevel();
        email = user.getEmail();
        role = user.getRole();
        status = user.getStatus();
        home = user.getHome();
        shortNo = user.getShortNo();
        createAt = user.getCreateAt();
    }
}
