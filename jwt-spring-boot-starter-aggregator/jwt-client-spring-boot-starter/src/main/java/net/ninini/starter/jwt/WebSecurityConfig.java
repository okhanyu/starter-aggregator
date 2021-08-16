package net.ninini.starter.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @ClassName: WebSecurityConfig
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/6 20:00
 * @Version 1.0.0
 */
@EnableWebSecurity
//表示开启spring方法级安全，prePostEnabled = true表示启用@PreAuthorize 和@PostAuthorize 两个注解，controller中有用到的
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    JwtUserDetailService jwtUserDetailService;

    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    JwtAccessDeniedHandler jwtAccessDeniedHandler;

    RSAPublicProperties rsaProperties;

    JWTGenerator jwtGenerator;

    @Autowired
    public WebSecurityConfig(JwtUserDetailService jwtUserDetailService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, JwtAccessDeniedHandler jwtAccessDeniedHandler, RSAPublicProperties rsaProperties, JWTGenerator jwtGenerator) {
        this.jwtUserDetailService = jwtUserDetailService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.rsaProperties = rsaProperties;
        this.jwtGenerator = jwtGenerator;
    }

    //@Autowired
    //private AuthenticationSuccessHandler successHandler;

    //@Autowired
    //private AuthenticationFailureHandler failHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .apply(new JwtSecurityConfigurer(authenticationManagerBean(), rsaProperties, jwtGenerator))
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 关闭Session会话
                .and().cors() // 支持跨域请求
                .and().csrf().disable() // CSRF(跨站请求伪造)禁用,默认开启，会检测请求中是否包含令牌，没有则拒绝并返回403

                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/demo/a").hasRole("ADMIN")
                //.antMatchers("/resources/**", "/signup", "/about").permitAll()
                //.antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .antMatchers("/jwt/generator/get").permitAll()
                .anyRequest().authenticated()
                .and()   //添加header设置，支持跨域和ajax请求
                .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
                new Header("Access-control-Allow-Origin", "*"),
                new Header("Access-Control-Expose-Headers", "Authorization"))))
                //.and() //拦截OPTIONS请求，直接返回header
                //.addFilterAfter(new OptionRequestFilter(), CorsFilter.class)
                //添加登录filter
//                .apply(new JsonLoginConfigurer<>()).loginSuccessHandler(jsonLoginSuccessHandler())
//                .and()
//                //添加token的filter
//                .apply(new JwtLoginConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout")

                .and().exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler)
                .and().httpBasic().disable().formLogin().disable();


        //.and().formLogin()
        // 未认证时访问跳转登录页面
        //.loginPage("/loginPage")
        //.loginProcessingUrl("/login") // 表单登录url设置，默认/login
        //.successHandler(successHandler)
        //.failureHandler(failHandler).permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(jwtAuthenticationProvider());
        auth.authenticationProvider(jwtAuthorizationProvider());
        //.userDetailsService(databaseUserDetailService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * @title jwtAuthenticationProvider
     * @description todo 自定义Provider进行校验
     * @return: JwtAuthenticationProvider
     * @author HanYu
     * @updateTime 2021/7/8 11:00
     */
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(jwtUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public JwtAuthorizationProvider jwtAuthorizationProvider() {
        JwtAuthorizationProvider provider = new JwtAuthorizationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(jwtUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        };
    }


    /**
     * @ClassName: WebSecurityConfig
     * @ProjectName scaffold
     * @Description: todo JWT过滤器配置类
     * @Author HanYu
     * @Date 2021/7/6 20:00
     * @Version 1.0.0
     */
    @Component
    @Configuration
    public class JwtSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

        private AuthenticationManager authenticationManager;

        private RSAPublicProperties rsaProperties;

        private JWTGenerator jwtGenerator;

        public JwtSecurityConfigurer(AuthenticationManager authenticationManager, RSAPublicProperties rsaProperties, JWTGenerator jwtGenerator) {
            this.authenticationManager = authenticationManager;
            this.rsaProperties = rsaProperties;
            this.jwtGenerator = jwtGenerator;
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            //http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class)


            JwtAuthorizationFilter customJwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, rsaProperties);

            JwtLoginAuthenticationFilter customJwtLoginAuthenticationFilter = new JwtLoginAuthenticationFilter(authenticationManager, jwtGenerator);
            http.addFilter(customJwtLoginAuthenticationFilter).addFilter(customJwtAuthorizationFilter);
            //http.addFilterBefore(customJwtLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(customJwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

}
