package net.ninini.starter.jwt;

import net.ninini.starter.common.http.HttpConstant;
import net.ninini.starter.common.json.JacksonUtil;
import net.ninini.starter.response.builder.ResponseBuilder;
import net.ninini.starter.response.code.ResponseCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName: JwtAccessDeniedHandler
 * @ProjectName scaffold
 * @Description: todo
 * @Author HanYu
 * @Date 2021/7/7 16:53
 * @Version 1.0.0
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

        httpServletResponse.setCharacterEncoding(HttpConstant.CHARACTER_ENCODING_UTF8);
        httpServletResponse.setContentType(HttpConstant.CONTENT_TYPE_JSON_UTF8);
        httpServletResponse.setStatus(httpServletResponse.SC_FORBIDDEN);
        httpServletResponse.getWriter().print(JacksonUtil.writeAsString(ResponseBuilder.fail(ResponseCode.ACCESS_FAIL_PERMISSION_DENIED)));

        // httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
    }
}
