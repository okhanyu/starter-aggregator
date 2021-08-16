package net.ninini.starter.validate;

import lombok.extern.slf4j.Slf4j;
import net.ninini.starter.exception.info.BusinessException;
import net.ninini.starter.response.code.ResponseCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

/**
 * @ClassName: BindingResultAssert
 * @ProjectName starter
 * @Description: todo 参数检查断言工具
 * @Author HanYu
 * @Date 2021/6/24 18:41
 * @Version 1.0.0
 */
@Slf4j
public class BindingResultAssert {

    public static void checkParam(BindingResult... bindingResult) {
        boolean flag = true;
        StringBuffer errorMsg = new StringBuffer();
        for (int i = 0; i < bindingResult.length; i++) {
            BindingResult br = bindingResult[i];
            if (br.hasErrors()) {
                flag = false;
                //List<ObjectError> errorList = br.getAllErrors();
                List<FieldError> errorList = br.getFieldErrors();
                for (int j = 0; j < errorList.size(); j++) {
                    FieldError e = errorList.get(j);
                    errorMsg.append("错误")
                            .append(i+1)
                            .append(".")
                            .append(j+1)
                            .append(":")
                            .append(e.getField())
                            .append("的值为")
                            .append(e.getRejectedValue())
                            .append(",错误原因是")
                            .append(e.getDefaultMessage())
                            .append(";  ");
                }
            }
        }
        if (!flag) {
            log.error(errorMsg.toString());
            throw new BusinessException(ResponseCode.PARAM_ERROR_CUSTOM_INFO, errorMsg.toString());
        }

    }
}
