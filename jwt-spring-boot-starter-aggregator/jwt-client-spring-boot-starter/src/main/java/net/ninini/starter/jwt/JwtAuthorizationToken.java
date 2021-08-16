package net.ninini.starter.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @ClassName: JwtAuthorizationToken
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/13 17:03
 * @Version 1.0.0
 */
public class JwtAuthorizationToken extends UsernamePasswordAuthenticationToken {

    public JwtAuthorizationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtAuthorizationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

}
