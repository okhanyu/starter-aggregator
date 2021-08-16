package net.ninini.starter.jwt;

import com.google.common.collect.Lists;
import net.ninini.starter.common.JWTConstant;
import net.ninini.starter.common.JWTPayloadUser;
import net.ninini.starter.common.json.JacksonUtil;
import net.ninini.starter.response.builder.ResponseBuilder;
import net.ninini.starter.response.code.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


/**
 * @ClassName: JwtLoginAuthenticationFilter
 * @ProjectName scaffold
 * @Description: todo 登录认证过滤器
 * @Author HanYu
 * @Date 2021/7/7 19:51
 * @Version 1.0.0
 */
@Slf4j
public class JwtLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private JWTGenerator jwtGenerator;

    public JwtLoginAuthenticationFilter(AuthenticationManager authenticationManager, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        super.setAuthenticationManager(authenticationManager);
        // super.setFilterProcessesUrl(super.path);
    }

    @Nullable
    protected String obtainUserPhone(HttpServletRequest request) {
        return request.getParameter(JWTConstant.JWT_USER_PHONE_PARAMETER);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals(JWTConstant.JWT_SUPPORT_METHOD)) {
            // 仅支持POST登录请求
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String phone = this.obtainUserPhone(request);
            String password = this.obtainPassword(request);
            if (StringUtils.isEmpty(phone)) {
                throw new BadCredentialsException("Phone is invalid");
            }
            phone = phone.trim();

            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(phone, password);
            this.setDetails(request, authRequest);
            return authenticationManager.authenticate(authRequest);
            //return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        PrintWriter writer = null;
        try {
            List<String> simpleGrantedAuthorities = Lists.newArrayList();
            authResult.getAuthorities().forEach(x ->  simpleGrantedAuthorities.add(x.getAuthority()));

            String token = jwtGenerator.getToken(new JWTPayloadUser(authResult.getPrincipal().toString(), simpleGrantedAuthorities));

            setResponse(response, HttpServletResponse.SC_OK);
            response.addHeader(JWTConstant.JWT_AUTHORIZATION, JWTConstant.JWT_PREFIX.concat(token));
            writer = response.getWriter();
            writer.write(JacksonUtil.writeAsString(ResponseBuilder.success(token)));
            log.info("{} jwt login success", authResult.getPrincipal());
        } catch (IOException e) {
            log.error("jwt login fail because exception ", e);
            throw e;
        } catch (Exception e) {
            log.error("jwt login fail because exception ", e);
            throw e;
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {

        PrintWriter writer = null;
        try {
            setResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            writer = response.getWriter();
            writer.write(JacksonUtil.writeAsString(ResponseBuilder.fail(ResponseCode.LOGIN_FAIL_USER_PWD_INVALID)));
            //super.unsuccessfulAuthentication(request, response, failed);
            log.error("jwt login fail because password invalid", failed);
        } catch (IOException e) {
            log.error("jwt login fail because password invalid and ", e);
            throw e;
        } catch (Exception e) {
            log.error("jwt login fail because password invalid and ", e);
            throw e;
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }

        //
        //        String returnData="";
        //        // 账号过期
        //        if (failed instanceof AccountExpiredException) {
        //            returnData="账号过期";
        //        }
        //        // 密码错误
        //        else if (failed instanceof BadCredentialsException) {
        //            returnData="密码错误";
        //        }
        //        // 密码过期
        //        else if (failed instanceof CredentialsExpiredException) {
        //            returnData="密码过期";
        //        }
        //        // 账号不可用
        //        else if (failed instanceof DisabledException) {
        //            returnData="账号不可用";
        //        }
        //        //账号锁定
        //        else if (failed instanceof LockedException) {
        //            returnData="账号锁定";
        //        }
        //        // 用户不存在
        //        else if (failed instanceof InternalAuthenticationServiceException) {
        //            returnData="用户不存在";
        //        }
        //        // 其他错误
        //        else{
        //            returnData="未知异常";
        //        }
    }

    private void setResponse(HttpServletResponse response, int httpStatus) {
        response.setContentType(JWTConstant.JWT_APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(JWTConstant.JWT_UTF8);
        response.setStatus(httpStatus);
    }
}