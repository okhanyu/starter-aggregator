package net.ninini.starter.jwt;

import net.ninini.starter.common.rsa.RSAUtils;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @ClassName: RsaKeyProperties
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/8 18:51
 * @Version 1.0.0
 */
@ConditionalOnProperty(prefix = "rsa", value = "pubKeyBase64", matchIfMissing = false)
@Data
@Component
@ConfigurationProperties(prefix = "rsa")
public class RSAPublicProperties {

    private String pubKeyBase64;

    private PublicKey publicKey;

    @PostConstruct
    public void createRsaKey() throws Exception {
        publicKey = RSAUtils.getPublicKey(pubKeyBase64);
    }
}
