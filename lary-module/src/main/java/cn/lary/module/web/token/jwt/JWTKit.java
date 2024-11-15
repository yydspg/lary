package cn.lary.module.web.token.jwt;

import cn.lary.common.context.Profile;
import cn.lary.common.kit.JSONKit;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTKit {
    // 过期时间
    private static long expire = 604800;
    // 秘钥
    private static String secret = "HSyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";


    public static String build(Profile profile) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expire);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setPayload(JSONKit.toJSON(profile))
                .setExpiration(expireDate).signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 解析token
     */
    public static Claims getClaimsByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断 srsToken 是否过期
     */
    public static boolean isTokenExpired(Date expiration){
        return expiration.before(new Date());
    }
}
