package net.ninini.starter.jwt;

import net.ninini.starter.common.JWTConstant;
import net.ninini.starter.common.JWTPayloadUser;
import net.ninini.starter.common.json.JacksonUtil;
import net.ninini.starter.response.builder.ResponseBuilder;
import net.ninini.starter.response.code.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @ClassName: JwtAuthorizationFilter
 * @ProjectName scaffold
 * @Description: todo 鉴权过滤器
 * @Author HanYu
 * @Date 2021/7/7 21:02
 * @Version 1.0.0
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private RSAPublicProperties rsa;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, RSAPublicProperties rsa) {
        super(authenticationManager);
        this.rsa = rsa;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {

        String header = request.getHeader(JWTConstant.JWT_AUTHORIZATION);

        if (header != null && header.startsWith(JWTConstant.JWT_PREFIX)) { // 已登录过持有token
            try {
                SecurityContextHolder.getContext().setAuthentication(getAuthentication(header.replace(JWTConstant.JWT_PREFIX, "")));
                //super.doFilterInternal(request, response, chain);
                chain.doFilter(request, response);
            } catch (Exception e) {  // token无效
                setResponse(response, HttpServletResponse.SC_FORBIDDEN, ResponseCode.ACCESS_FAIL_TOKEN_INVALID);
                return;
            }
        } else {  // 未登录
            //chain.doFilter(request, response);
            setResponse(response, HttpServletResponse.SC_FORBIDDEN, ResponseCode.ACCESS_FAIL_TOKEN_REQUIRED);
            return;
        }
    }

    /**
     * @title getAuthentication
     * @description todo 验证并获取JWT中的用户和权限信息
     * @param: token
     * @return: JwtAuthorizationToken
     * @author HanYu
     * @updateTime 2021/7/13 20:47
     */
    private JwtAuthorizationToken getAuthentication(String token) {
        JWTPayloadUser jwtPayloadCustom = JWTClientUtils.validateTokenAndBack(token, rsa.getPublicKey());
        if (null == jwtPayloadCustom) throw new BadCredentialsException("Token无效或已过期");
        // ROLE_
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList();
        jwtPayloadCustom.getAuthorities().forEach(x -> grantedAuthorities.add(new SimpleGrantedAuthority(x)));
        return new JwtAuthorizationToken(jwtPayloadCustom.getUserid(), null, grantedAuthorities);
    }


    /**
     * @title setResponse
     * @description todo 设置响应格式
     * @param: response
     * @param: httpStatus
     * @param: businessResponseCode
     * @author HanYu
     * @updateTime 2021/7/13 20:51
     */
    private void setResponse(HttpServletResponse response, int httpStatus, ResponseCode businessResponseCode) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(httpStatus);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write(JacksonUtil.writeAsString(ResponseBuilder.fail(businessResponseCode)));
        } catch (IOException e) {
            log.error("JWT authorization fail", e);
            throw e;
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
