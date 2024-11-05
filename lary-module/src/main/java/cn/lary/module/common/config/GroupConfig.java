package cn.lary.module.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lary.group")
public class GroupConfig {
    private int sameDayCreateMaxCount;
    private boolean createVerifyFriendOn; // 建群是否开启好友验证
    private boolean  allowViewHistoryMsgStatus; // 是否允许查看历史消息
    private int maxGroupMemberCountAboutAvatar; // 最大的群头像组成人数
}
