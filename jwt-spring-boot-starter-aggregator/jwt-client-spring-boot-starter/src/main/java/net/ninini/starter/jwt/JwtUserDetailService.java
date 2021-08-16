package net.ninini.starter.jwt;

import com.google.common.collect.Lists;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName: JwtUserDetailService
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/7 23:23
 * @Version 1.0.0
 */
@Component
public class JwtUserDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (!username.equals("user") ) throw new UsernameNotFoundException("用户名不存在");

        List<GrantedAuthority> authorities = Lists.newArrayList();
        GrantedAuthority g = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_ADMIN";
            }
        };
        authorities.add(g);

        //authorities.add(new SimpleGrantedAuthority(role.getRoleName())); // 角色的信息

        return new User("user", "12322", authorities);
    }
}
