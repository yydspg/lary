package cn.lary.api.user.dto;

import lombok.Data;

@Data
public class UidQueryDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * Oauth
     */
    private String external;

    /**
     * Oauth id
     */
    private Long oid;


}
