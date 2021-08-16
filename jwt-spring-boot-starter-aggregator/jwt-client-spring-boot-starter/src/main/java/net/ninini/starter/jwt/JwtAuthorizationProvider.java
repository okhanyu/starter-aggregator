package net.ninini.starter.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @ClassName: JwtAuthenticationProvider
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/7 23:17
 * @Version 1.0.0
 */
@Slf4j
public class JwtAuthorizationProvider extends DaoAuthenticationProvider {

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthorizationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("authorization verify JwtAuthorizationProvider");
        //getUserDetails(String.valueOf(authentication.getPrincipal()));
        //authentication.setAuthenticated(true);
        return authentication;
    }

    private UserDetails getUserDetails(String userid) throws AuthenticationException {
        // this.prepareTimingAttackProtection();JwtAuthenticationProvider
        try {
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(userid);
            if (loadedUser == null) {
                // throw new BadCredentialsException("user not exist");
                throw new InternalAuthenticationServiceException("user not exist , which is an interface contract violation");
            } else {
                return loadedUser;
            }
        } catch (UsernameNotFoundException usernameNotFoundException) {
            // this.mitigateAgainstTimingAttack(authentication);
            throw usernameNotFoundException;
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            throw internalAuthenticationServiceException;
        } catch (Exception exception) {
            throw new InternalAuthenticationServiceException(exception.getMessage(), exception);
        }
    }
}
