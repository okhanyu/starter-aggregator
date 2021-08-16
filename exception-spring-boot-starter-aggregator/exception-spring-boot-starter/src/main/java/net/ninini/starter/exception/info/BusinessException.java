package net.ninini.starter.exception.info;

import lombok.Data;
import net.ninini.starter.response.code.ResponseCode;

/**
 * @ClassName: BusinessException
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/6/24 18:11
 * @Version 1.0.0
 */
@Data
//@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "")
public class BusinessException extends RuntimeException {

    private ResponseCode errorCode;

    private String errorExtra;

    @Deprecated
    public BusinessException(ResponseCode errorCode, String errorExtra) {
        super(new StringBuffer(errorCode.getCode()).append(",").append(errorExtra).toString());
        this.errorCode = errorCode;
        this.errorExtra = errorExtra;
    }

    public BusinessException(ResponseCode errorCode) {
        super(new StringBuffer(errorCode.getCode()).append(",").append(errorCode.getInfo()).toString());
        this.errorCode = errorCode;
    }
}
