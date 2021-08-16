package net.ninini.starter.redis.core.template;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.ninini.starter.redis.core.action.ActionScript;
import net.ninini.starter.redis.core.model.LockActionResult;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: IRedisTemplate
 * @ProjectName redis-spring-boot-starter
 * @Description: Redis操作模板类
 * @Author HanYu
 * @Date 2021/6/11 18:07
 * @Version 1.0.0
 */
@Slf4j
public  class IRedisTemplate extends IRedisTemplateAbstract {

    private List<RedissonClient> redissonClients;

    private ObjectMapper objectMapper;


    public IRedisTemplate() {
        super();
    }

    public IRedisTemplate(List<RedissonClient> redissonClients) {
        this.redissonClients = redissonClients;
    }

    @PostConstruct
    public void getObjectMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    }

    private RedissonRedLock getRedissonRedLock(String lockKey) {
        RLock[] rLocks = new RLock[redissonClients.size()];
        for (int i = 0; i < redissonClients.size(); i++) {
            rLocks[i] = redissonClients.get(i).getLock(lockKey);
        }
        return new RedissonRedLock(rLocks);
    }

    /**
     * @title tryLockOfRedissonWatchDog
     * @description 使用Redisson实现分布式锁
     * @param: lockKey
     * @param: tryWaitTime
     * @param: tryWaitTimeUnit
     * @param: actionScript
     * @return: cool.hanyu.develop.starter.redis.model.LockActionResult
     * @author HanYu
     * @updateTime 2021/6/15 17:57
     */
    public <T> LockActionResult tryLockOfRedissonWatchDog(String lockKey, Long tryWaitTime, TimeUnit tryWaitTimeUnit, ActionScript<T> actionScript) {

        Assert.notNull(lockKey, "lockKey must not be null!");
        Assert.notNull(actionScript, "actionScript must not be null!");

        // 拿锁失败时不停重试,WatchDog自动延期机制默认续30s每隔30/3=10秒续到30s
        RLock lock = getRedissonRedLock(lockKey);
        if (null == lock) return new LockActionResult(false, null);
        boolean res;
        try {
            res = lock.tryLock(null == tryWaitTime ? 2L : tryWaitTime, null == tryWaitTimeUnit ? TimeUnit.SECONDS : tryWaitTimeUnit);
        } catch (InterruptedException e) {
            return new LockActionResult(false, null);
        }
        try {
            if (res) { // get redisson lock success
                log.debug("get redisson lock success");
                return new LockActionResult(true, actionScript.exec());
            } else { // get redisson lock fail
                log.debug("get redisson lock fail");
                return new LockActionResult(false, null);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * @title tryLockDefault
     * @description 普通分布式锁（不支持续命机制）
     * @param: key
     * @param: time
     * @param: unit
     * @param: actionScript
     * @return: cool.hanyu.develop.starter.redis.model.LockActionResult
     * @author HanYu
     * @updateTime 2021/6/15 17:58
     */
    @Deprecated
    public <T> LockActionResult tryLockDefault(String key, Long time, TimeUnit unit, ActionScript<T> actionScript) {
        Assert.notNull(key, "Key must not be null!");
        Assert.notNull(time, "Time must not be null!");
        Assert.notNull(unit, "Unit must not be null!");
        Assert.notNull(actionScript, "ActionScript must not be null!");

        ValueOperations<String, String> opsForValue = this.opsForValue();
        String uuid = UUID.randomUUID().toString();
        if (opsForValue.setIfAbsent(key, uuid, time, unit)) { // get lock success
            log.debug("get lock success");
            try {
                return new LockActionResult(true, actionScript.exec());
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                if (this.hasKey(key) == true
                        && null != opsForValue.get(key)
                        && uuid.equals(opsForValue.get(key))) {
                    this.delete(key);
                }
            }
        } else { // get lock fail
            log.debug("get lock fail");
            return new LockActionResult(false, null);
        }
    }


}
