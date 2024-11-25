package cn.lary.common.interceptor.token.jwt;

import cn.lary.common.context.Profile;
import cn.lary.common.kit.SystemClock;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTKit {

    private static final long expire_time = 60 * 60 ;

    private static final String SECRET = "lary-jwt-secret-enhance";

    private static final SignatureAlgorithm ALGORITHM = SignatureAlgorithm.HS256;

    public static String build(Profile profile){
        long now = SystemClock.now();
        Map<String,Object> claims = new HashMap<>();
        claims.put("uid",profile.getUid());
        claims.put("name",profile.getName());
        claims.put("flag",profile.getFlag());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(now + expire_time))
                .signWith(ALGORITHM, SECRET).compact();
    }

    public static Profile verify(String token){
        Claims body = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        Long uid = body.get("uid", Long.class);
        String name = body.get("name", String.class);
        Integer flag = body.get("flag", Integer.class);
        Long exp = body.get("exp", Long.class);
        if (uid == null || name == null || flag == null|| exp == null) {
            return null;
        }
        if(exp < SystemClock.now()){
            return null;
        }
        Profile profile = new Profile();
        profile.setUid(uid);
        profile.setName(name);
        profile.setFlag(flag);
        return profile;
    }
//    public static void main(String[] args) {
//        Profile profile = new Profile();
//        profile.setFlag(1);
//        profile.setName("yydspg");
//        profile.setUid(1332432172233244566L);
//        System.out.println(build(profile));
//
//    }
}
