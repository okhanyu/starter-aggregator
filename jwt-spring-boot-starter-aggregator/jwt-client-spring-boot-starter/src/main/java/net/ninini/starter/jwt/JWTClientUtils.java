package net.ninini.starter.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.ninini.starter.common.JWTPayloadUser;
import net.ninini.starter.common.json.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import static net.ninini.starter.common.JWTConstant.*;

/**
 * @ClassName: JWTClientUtils
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/14 11:24
 * @Version 1.0.0
 */
@Slf4j
public class JWTClientUtils {

    /**
     * @title getClaimsFromToken
     * @description todo 获取载荷中的信息
     * @param: token
     * @param: publicKey
     * @return: io.jsonwebtoken.Claims
     * @author HanYu
     * @updateTime 2021/7/13 19:48
     */
    public static Claims getClaimsFromToken(String token, PublicKey publicKey) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * @title isTokenExpired
     * @description todo 判断令牌是否过期
     * @param: token 令牌
     * @param: publicKey RSA公钥
     * @return: java.lang.Boolean
     * @author HanYu
     * @updateTime 2021/7/13 19:56
     */
    public static Boolean isTokenExpired(String token, PublicKey publicKey) {
        try {
            Claims claims = getClaimsFromToken(token, publicKey);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date()); // 过期
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @title parserToken
     * @description todo 公钥解析token
     * @param: token 令牌
     * @param: publicKey RSA公钥
     * @return: io.jsonwebtoken.Jws<io.jsonwebtoken.Claims>
     * @author HanYu
     * @updateTime 2021/7/13 19:58
     */
    public static Jws<Claims> parserToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    /**
     * @title createJTI
     * @description todo JWT的唯一身份标识，主要用来作为一次性token，回避重放攻击
     * @return: java.lang.String
     * @author HanYu
     * @updateTime 2021/7/13 19:58
     */
    public static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    /**
     * @title getCustomFromTokenByClassType
     * @description todo 获取token中的自定义信息
     * @param: token 令牌
     * @param: publicKey RSA公钥
     * @param: classType 期望反序列化成的类定义
     * @return: T
     * @author HanYu
     * @updateTime 2021/7/13 20:24
     */
    public static <T> T getCustomFromTokenByClassType(String token, PublicKey publicKey, Class<T> classType) {
        Claims body = getClaimsFromToken(token, publicKey);
        try {
            String custom = body.get(JWT_PAYLOAD_CUSTOM).toString();
            T obj = JacksonUtil.readValue(custom, classType);
            return obj;
        } catch (JsonProcessingException e) {
            log.error("parse the token invalid ", e);
        }
        return null;
    }

    /**
     * @title getCustomFromToken
     * @description todo 获取token中的自定义信息
     * @param: token 令牌
     * @param: publicKey RSA私钥
     * @return: JWTPayloadUser
     * @author HanYu
     * @updateTime 2021/7/13 20:30
     */
    public static JWTPayloadUser getCustomFromToken(String token, PublicKey publicKey) {
        Claims body = getClaimsFromToken(token, publicKey);
        try {
            String custom = body.get(JWT_PAYLOAD_CUSTOM).toString();
            JWTPayloadUser obj = JacksonUtil.readValue(custom, JWTPayloadUser.class);
            return obj;
        } catch (JsonProcessingException e) {
            log.error("parse the token invalid ", e);
        }
        return null;
    }


    /**
     * @title validateToken
     * @description todo 令牌验证
     * @param: token 令牌
     * @param: publicKey RSA公钥
     * @return: java.lang.Boolean
     * @author HanYu
     * @updateTime 2021/7/13 20:35
     */
    public static Boolean validateToken(String token, PublicKey publicKey) {
        JWTPayloadUser JWTPayloadUser = getCustomFromToken(token, publicKey);
        return null != JWTPayloadUser && !isTokenExpired(token, publicKey) ? true : false;
    }

    /**
     * @title validateToken
     * @description todo 令牌验证
     * @param: token 令牌
     * @param: publicKey RSA公钥
     * @return: java.lang.Boolean
     * @author HanYu
     * @updateTime 2021/7/13 20:35
     */
    public static JWTPayloadUser validateTokenAndBack(String token, PublicKey publicKey) {
        JWTPayloadUser JWTPayloadUser = getCustomFromToken(token, publicKey);
        return null != JWTPayloadUser && !isTokenExpired(token, publicKey) ? JWTPayloadUser : null;
    }

}
