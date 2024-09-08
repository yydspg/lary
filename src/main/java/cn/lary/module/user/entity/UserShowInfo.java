package cn.lary.module.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class UserShowInfo {
    private String uid;
    private String name;
    private String bio;
    @TableField(value = "is_upload_avatar")
    private boolean isUploadAvatar;
    @TableField(value = "avatar_url")
    private String avatarUrl;
    @TableField(value = "update_at")
    private LocalDateTime updateAt;
}
