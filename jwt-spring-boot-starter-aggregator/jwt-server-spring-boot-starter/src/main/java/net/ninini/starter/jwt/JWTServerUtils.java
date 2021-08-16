package net.ninini.starter.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.ninini.starter.common.JWTPayloadUser;
import net.ninini.starter.common.json.JacksonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static net.ninini.starter.common.JWTConstant.*;


/**
 * @ClassName: JwtUtils
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/8 22:09
 * @Version 1.0.0
 */
@Slf4j
public class JWTServerUtils {

    /**
     * @title generateToken
     * @description todo 生成JWT
     * @param: claim 自定义载荷信息
     * @param: privateKey RSA私钥
     * @return: java.lang.String
     * @author HanYu
     * @updateTime 2021/7/13 19:45
     */
    public static String generateToken(Object customClaim, PrivateKey privateKey) throws JsonProcessingException {

        return Jwts.builder()
                .setSubject(JWT_SUBJECT)
                .setHeader(JWT_HEADER)
                .claim(JWT_PAYLOAD_CUSTOM, JacksonUtil.writeAsString(customClaim))
                .setAudience(JWT_AUDIENCE)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())    // 发布时间
                .setNotBefore(new Date())   // 生效时间
                .setId(createJTI())
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * @title generateTokenExpireInSeconds
     * @description todo 生成自定义过期时间的JWT
     * @param: claim 自定义载荷信息
     * @param: privateKey RSA私钥
     * @param: expire 过期时间
     * @return: java.lang.String
     * @author HanYu
     * @updateTime 2021/7/13 19:45
     */
    public static String generateTokenExpireInSeconds(Object customClaim, PrivateKey privateKey, int expire) throws JsonProcessingException {

        return Jwts.builder()
                .setSubject(JWT_SUBJECT)
                .setHeader(JWT_HEADER)
                .claim(JWT_PAYLOAD_CUSTOM, JacksonUtil.writeAsString(customClaim))
                .setAudience(JWT_AUDIENCE)
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(new Date())    // 发布时间
                .setNotBefore(new Date())   // 生效时间
                .setId(createJTI())
                .setExpiration(Date.from(LocalDateTime.now().plusSeconds(expire).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

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
     * @title refreshToken
     * @description todo 刷新令牌
     * @param: token 原令牌
     * @param: publicKey RSA公钥
     * @param: privateKey RSA私钥
     * @return: java.lang.String 新令牌
     * @author HanYu
     * @updateTime 2021/7/13 19:56
     */
    @Deprecated
    public static String refreshToken(String token, PublicKey publicKey, PrivateKey privateKey) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token, publicKey);
            refreshedToken = generateToken(claims, privateKey);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
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
