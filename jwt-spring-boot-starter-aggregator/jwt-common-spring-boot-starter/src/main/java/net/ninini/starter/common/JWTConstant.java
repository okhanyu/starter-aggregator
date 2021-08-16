package net.ninini.starter.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: JWTConstant
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/14 11:32
 * @Version 1.0.0
 */
public class JWTConstant {

    public static final String JWT_SUPPORT_METHOD = "POST";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    public static final String JWT_UTF8 = "UTF-8";

    public static final String JWT_USERID_PARAMETER = "userid";
    public static final String JWT_USER_PHONE_PARAMETER = "phone";
    public static final String JWT_PAYLOAD_CUSTOM = "user";
    public static final String JWT_SUBJECT = "data";
    public static final String JWT_AUDIENCE = "[\"app\",\"web\"]";
    public static final String JWT_ISSUER = "data manager";

    /**
     * JWT有效期 (秒)
     */
    public static final long EXPIRATION_TIME = 300;

    /**
     * JWT header信息
     */
    public static final Map<String, Object> JWT_HEADER = new HashMap() {
        {
            put("alg", "HS256");
            put("typ", "JWT");
        }
    };

}
