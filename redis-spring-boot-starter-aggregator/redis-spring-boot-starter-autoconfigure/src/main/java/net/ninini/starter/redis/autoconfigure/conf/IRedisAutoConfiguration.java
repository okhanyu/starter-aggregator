package net.ninini.starter.redis.autoconfigure.conf;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import net.ninini.starter.redis.autoconfigure.properties.CommonPool2Properties;
import net.ninini.starter.redis.autoconfigure.properties.RedisProperties;
import net.ninini.starter.redis.autoconfigure.properties.RedissonProperties;
import net.ninini.starter.redis.core.template.IRedisTemplate;
import net.ninini.starter.redis.core.template.IStringRedisTemplate;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.List;

/**
 * @ClassName: IRedisAutoConfiguration
 * @ProjectName redis-spring-boot-starter
 * @Description: Redis自动配置类
 * @Author HanYu
 * @Date 2021/6/11 18:07
 * @Version 1.0.0
 */
@Configuration
@ConditionalOnClass(IRedisTemplate.class)
@ConditionalOnProperty(prefix = "i.redis", value = "enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({RedisProperties.class, CommonPool2Properties.class, RedissonProperties.class})
public class IRedisAutoConfiguration {

    @Resource
    CommonPool2Properties commonPool2Properties;

    @Resource
    RedissonProperties redissonProperties;

    @Resource
    RedisProperties redisProperties;

    /**
     * key 的序列化器
     */
    private final StringRedisSerializer keyRedisSerializer = new StringRedisSerializer();

    /**
     * value 的序列化器
     */
    private final Jackson2JsonRedisSerializer<Object> valueRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

    @Bean
    @ConditionalOnMissingBean
    public IRedisTemplate iRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        List<RedissonClient> clients = redissonClients(redissonConfigs());
        IRedisTemplate redisTemplate = new IRedisTemplate(clients);
        setTemplate(redisTemplate,redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public IStringRedisTemplate iStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        IStringRedisTemplate stringRedisTemplate = new IStringRedisTemplate();
        setTemplate(stringRedisTemplate,redisConnectionFactory);
        return stringRedisTemplate;
    }

    /**
     * @title setTemplate
     * @description todo 设置模板类
     * @param: redisTemplate
     * @param: redisConnectionFactory
     * @author HanYu
     * @updateTime 2021/6/25 11:01
     */
    private void setTemplate(RedisTemplate redisTemplate,RedisConnectionFactory redisConnectionFactory){
        // 配置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        valueRedisSerializer.setObjectMapper(objectMapper);
        // 值序列化
        redisTemplate.setValueSerializer(valueRedisSerializer);
        redisTemplate.setHashValueSerializer(valueRedisSerializer);
        // 键序列化
        redisTemplate.setKeySerializer(keyRedisSerializer);
        redisTemplate.setHashKeySerializer(keyRedisSerializer);
        redisTemplate.afterPropertiesSet();
    }

    /**
     * @title redissonClients
     * @description todo 获取redisson集群
     * @param: redissonConfigs
     * @return: java.util.List<org.redisson.api.RedissonClient>
     * @author HanYu
     * @updateTime 2021/6/25 11:01
     */
    private List<RedissonClient> redissonClients(List<Config> redissonConfigs) {
        if (null == redissonConfigs || redissonConfigs.isEmpty()) return null;
        List<RedissonClient> clients = Lists.newArrayList();
        for (Config config : redissonConfigs) {
            clients.add(Redisson.create(config));
        }
        return clients;
    }

    /**
     * @title redissonConfigs
     * @description todo 获取redisson配置
     * @return: java.util.List<org.redisson.config.Config>
     * @author HanYu
     * @updateTime 2021/6/25 11:01
     */
    private List<Config> redissonConfigs() {
        Config config = new Config();
        // 单机或主从
        config.useSingleServer()
                .setAddress(redissonProperties.getHost())
                .setPassword(redisProperties.getPassword())
                .setDatabase(redisProperties.getDatabase());

        return Lists.newArrayList(config);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        // RedisCacheConfiguration - 值的序列化方式
        RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(valueRedisSerializer);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(serializationPair);

        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

    /**
     * @title genericObjectPoolConfig
     * @description todo 连接池配置
     * @return: org.apache.commons.pool2.impl.GenericObjectPoolConfig
     * @author HanYu
     * @updateTime 2021/6/25 11:01
     */
    @Bean
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(commonPool2Properties.getMaxIdle());
        genericObjectPoolConfig.setMinIdle(commonPool2Properties.getMinIdle());
        genericObjectPoolConfig.setMaxTotal(commonPool2Properties.getMaxTotal());
        genericObjectPoolConfig.setMaxWaitMillis(commonPool2Properties.getMaxWait());
        return genericObjectPoolConfig;
    }

    /**
     * @title redisConnectionFactory
     * @description todo 构建连接信息
     * @param: genericObjectPoolConfig
     * @return: org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
     * @author HanYu
     * @updateTime 2021/6/25 11:02
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        redisStandaloneConfiguration.setPort(redisProperties.getPort());

        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
                .poolConfig(genericObjectPoolConfig)
                .build();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

        /**
         * @title redisConnectionFactory
         * @description todo 集群版设置
         * @param: genericObjectPoolConfig
         * @return: org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
         * @author HanYu
         * @updateTime 2021/6/25 11:04
         */
    //    @Bean
    //   public LettuceConnectionFactory redisConnectionFactory(GenericObjectPoolConfig genericObjectPoolConfig) {

    //        集群版配置
    //        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
    //        String[] serverArray = clusterNodes.split(",");
    //        Set<RedisNode> nodes = new HashSet<RedisNode>();
    //        for (String ipPort : serverArray) {
    //            String[] ipAndPort = ipPort.split(":");
    //            nodes.add(new RedisNode(ipAndPort[0].trim(), Integer.valueOf(ipAndPort[1])));
    //        }
    //        redisClusterConfiguration.setPassword(RedisPassword.of(password));
    //        redisClusterConfiguration.setClusterNodes(nodes);
    //        redisClusterConfiguration.setMaxRedirects(maxRedirects);

    //        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
    //                .commandTimeout(Duration.ofMillis(redisProperties.getTimeout()))
    //                .poolConfig(genericObjectPoolConfig)
    //                .build();
    //        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    //    }


    // private Config redissonConfig() {
    //        Config config = new Config();

    //        // 单机或主从
    //        config.useSingleServer()
    //                .setAddress(redisProperties.getHost())
    //                .setPassword(redisProperties.getPassword())
    //                .setDatabase(redisProperties.getDatabase());

    //        //哨兵
    //        config.useSentinelServers().addSentinelAddress(
    //                "redis://ip:26378", "redis://ip:26379", "redis://ip:26380")
    //                .setMasterName("mymaster")
    //                .setPassword("a123456").setDatabase(0);

    //        //集群
    //        config.useClusterServers().addNodeAddress(
    //                "redis://ip:6375","redis://ip:6376", "redis://ip:6377",
    //                "redis://ip:6378","redis://ip:6379", "redis://ip:6380")
    //                .setPassword("a123456").setScanInterval(5000);
    //        return config;
    // }


}
