package net.ninini.starter.redis.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: RedisProperties
 * @ProjectName redis-spring-boot-starter
 * @Description: Redis连接信息配置类
 * @Author HanYu
 * @Date 2021/6/11 18:07
 * @Version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "i.redis")
public class RedisProperties {

    private int database;

    private String host;

    private String password;

    private int port;

    private long timeout;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
