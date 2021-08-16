package net.ninini.starter.redis.autoconfigure.conf;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName: RedisAutoConfigureFilter
 * @ProjectName redis-spring-boot-starter
 * @Description: Redis原生自动配置过滤类
 * @Author HanYu
 * @Date 2021/6/11 18:07
 * @Version 1.0.0
 */
//@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
public class IRedisAutoConfigureFilter implements AutoConfigurationImportFilter {

    private static final Set<String> SHOULD_SKIP = new HashSet<>(
            Arrays.asList("org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
                    "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
                    "org.redisson.spring.starter.RedissonAutoConfiguration")
    );

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            matches[i] = !SHOULD_SKIP.contains(classNames[i]);
        }
        return matches;
    }
}
