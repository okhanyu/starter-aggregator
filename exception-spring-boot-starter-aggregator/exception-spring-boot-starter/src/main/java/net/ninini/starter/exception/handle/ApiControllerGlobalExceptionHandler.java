package net.ninini.starter.exception.handle;

import lombok.extern.slf4j.Slf4j;
import net.ninini.starter.exception.info.BusinessException;
import net.ninini.starter.response.builder.ResponseBuilder;
import net.ninini.starter.response.code.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: ApiControllerGlobalExceptionHandler
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/6/25 13:20
 * @Version 1.0.0
 */
@Slf4j
public abstract class ApiControllerGlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity handle500Exception(HttpServletRequest req, HttpServletResponse rsp, Exception e) {
        log.error("handle500Error",e);
        return new ResponseEntity(ResponseBuilder.fail(ResponseCode.UNKNOWN_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity handleNullError(HttpServletRequest req, HttpServletResponse rsp, NullPointerException e) throws Exception {
        log.error("handleNullError",e);
        return new ResponseEntity(ResponseBuilder.fail(ResponseCode.UNKNOWN_EXCEPTION), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public ResponseEntity handleBusinessError(HttpServletRequest req, HttpServletResponse rsp, BusinessException e) throws Exception {
        log.error("handleBusinessException",e);
        if(StringUtils.isEmpty(e.getErrorExtra())){
            return ResponseEntity.ok(ResponseBuilder.fail(e.getErrorCode()));
        }else{
            return ResponseEntity.ok(ResponseBuilder.failWithCustomInfo(e.getErrorCode(),e.getErrorExtra()));
        }
    }


}
