package net.ninini.starter.jwt;

import net.ninini.starter.common.http.HttpConstant;
import net.ninini.starter.common.json.JacksonUtil;
import net.ninini.starter.response.builder.ResponseBuilder;
import net.ninini.starter.response.code.ResponseCode;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: JwtAuthenticationEntryPoint
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/7 16:18
 * @Version 1.0.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {

        httpServletResponse.setCharacterEncoding(HttpConstant.CHARACTER_ENCODING_UTF8);
        httpServletResponse.setContentType(HttpConstant.CONTENT_TYPE_JSON_UTF8);
        httpServletResponse.setStatus(httpServletResponse.SC_UNAUTHORIZED);
        if (e instanceof InsufficientAuthenticationException)
            httpServletResponse.getWriter().print(JacksonUtil.writeAsString(ResponseBuilder.fail(ResponseCode.LOGIN_FAIL_SYSTEM_ERROR)));
        else
            httpServletResponse.getWriter().print(JacksonUtil.writeAsString(ResponseBuilder.fail(ResponseCode.LOGIN_FAIL_USER_PWD_INVALID)));

        //        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e == null ? "Unauthorized" : e.getMessage());
        //        if (e instanceof LockedException) {
        //            map.put("msg", "账户被锁定，登录失败!");
        //        } else if (e instanceof BadCredentialsException) {
        //            map.put("msg", "账户名或密码输入错误，登录失败!");
        //        } else if (e instanceof DisabledException) {
        //            map.put("msg", "账户被禁用，登录失败!");
        //        } else if (e instanceof AccountExpiredException) {
        //            map.put("msg", "账户已过期，登录失败!");
        //        } else if (e instanceof CredentialsExpiredException) {
        //            map.put("msg", "密码已过期，登录失败!");
        //        } else {
        //            map.put("msg", e.getMessage());
        //        }


    }
}
