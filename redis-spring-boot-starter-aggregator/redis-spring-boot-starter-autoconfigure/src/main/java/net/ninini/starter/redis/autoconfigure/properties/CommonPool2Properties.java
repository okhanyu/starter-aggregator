package net.ninini.starter.redis.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName: CommonPool2Properties
 * @ProjectName redis-spring-boot-starter
 * @Description: Redis连接池配置类
 * @Author HanYu
 * @Date 2021/6/11 18:07
 * @Version 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "i.redis.lettuce.pool")
public class CommonPool2Properties {
    private int maxTotal;

    private int maxIdle;

    private int minIdle;

    private int maxWait;

    private int maxActive;

    private long timeout;


    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
