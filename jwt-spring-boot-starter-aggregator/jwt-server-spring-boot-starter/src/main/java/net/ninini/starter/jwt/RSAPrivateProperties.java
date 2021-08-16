package net.ninini.starter.jwt;

import net.ninini.starter.common.rsa.RSAUtils;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;

/**
 * @ClassName: RsaKeyProperties
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/8 18:51
 * @Version 1.0.0
 */
@ConditionalOnProperty(prefix = "rsa", value = "priKeyBase64", matchIfMissing = false)
@Data
@Component
@ConfigurationProperties(prefix = "rsa")
public class RSAPrivateProperties {

    private String priKeyBase64;

    private PrivateKey privateKey;

    @PostConstruct
    public void createRsaKey() throws Exception {
        privateKey = RSAUtils.getPrivateKey(priKeyBase64);
    }
}
