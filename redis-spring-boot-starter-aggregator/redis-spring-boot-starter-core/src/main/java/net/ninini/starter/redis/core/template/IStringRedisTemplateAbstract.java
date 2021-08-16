package net.ninini.starter.redis.core.template;

import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @ClassName: IAbstract
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/6/25 10:56
 * @Version 1.0.0
 */
public abstract class IStringRedisTemplateAbstract extends StringRedisTemplate {

    public IStringRedisTemplateAbstract() {
        super();
    }
}
