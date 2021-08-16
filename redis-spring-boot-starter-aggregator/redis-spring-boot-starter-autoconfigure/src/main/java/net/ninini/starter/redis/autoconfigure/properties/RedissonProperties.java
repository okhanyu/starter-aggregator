package net.ninini.starter.redis.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ProjectName redis-spring-boot-starter
 * @ClassName: RidissonProperties
 * @Description: TODO
 * @Author HanYu
 * @Date 2021/6/13 12:56 上午
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "i.redis.redisson")
public class RedissonProperties {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
