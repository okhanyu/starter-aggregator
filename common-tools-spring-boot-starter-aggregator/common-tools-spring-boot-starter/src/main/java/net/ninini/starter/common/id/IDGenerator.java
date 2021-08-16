package net.ninini.starter.common.id;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName: IDMachine
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/2 11:31
 * @Version 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "leaf", value = "enable", havingValue = "true", matchIfMissing = true)
@FeignClient(name = "leaf", url = "${leaf.server}")
public interface IDGenerator {

    @GetMapping("/api/snowflake/get/online")
    String getID();

}
