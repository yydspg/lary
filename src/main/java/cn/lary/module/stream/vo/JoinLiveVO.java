package cn.lary.module.stream.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinLiveVO {

    private String danmakuId;

    private String specify;
    /**
     * 回调 token
     */
    private String token;
}
