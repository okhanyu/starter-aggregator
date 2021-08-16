package net.ninini.starter.jwt;

import net.ninini.starter.common.JWTPayloadUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: JWTGenerator
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/14 11:31
 * @Version 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "jwt", value = "enable", havingValue = "true", matchIfMissing = true)
@FeignClient(name = "jwt", url = "${jwt.server}")
public interface JWTGenerator {

    @PostMapping("/jwt/generator/get")
    String getToken(JWTPayloadUser jwtPayloadUser);

}
