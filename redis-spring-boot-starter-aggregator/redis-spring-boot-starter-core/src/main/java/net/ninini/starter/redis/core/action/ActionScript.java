package net.ninini.starter.redis.core.action;

/**
 * @ClassName: ActionScript
 * @ProjectName redis-spring-boot-starter
 * @Description: TODO
 * @Author HanYu
 * @Date 2021/6/11 18:42
 * @Version 1.0.0
 */
@FunctionalInterface
public interface ActionScript<T> {

    T exec();

}
