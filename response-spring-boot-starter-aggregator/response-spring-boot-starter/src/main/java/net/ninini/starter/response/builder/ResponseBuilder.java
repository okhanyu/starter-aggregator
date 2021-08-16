package net.ninini.starter.response.builder;

import lombok.Data;
import net.ninini.starter.response.code.ResponseCode;

/**
 * @ClassName: ResponseBuilder
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/6/24 14:12
 * @Version 1.0.0
 */

@Data
public class ResponseBuilder<T> {

    /**
     * 本类禁止使用构造者模式，禁止对外暴露构造函数
     * by HanYu
     */

    private String code;

    private String info;

    private T data;

    private ResponseBuilder() {
    }

    private ResponseBuilder(String code, String info, T data) {
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public static <T> ResponseBuilder<T> success() {
        return new ResponseBuilder(
                ResponseCode.SUCCESS.getCode(),
                ResponseCode.SUCCESS.getInfo(),
                "");
    }

    public static <T> ResponseBuilder<T> success(T data) {
        return new ResponseBuilder(
                ResponseCode.SUCCESS.getCode(),
                ResponseCode.SUCCESS.getInfo(),
                data);
    }

    public static <T> ResponseBuilder<T> fail(ResponseCode code, T data) {
        return new ResponseBuilder(
                code.getCode(),
                code.getInfo(),
                data);
    }

    public static <T> ResponseBuilder<T> fail(ResponseCode code) {
        return new ResponseBuilder(
                code.getCode(),
                code.getInfo(),
                "");
    }

    @Deprecated
    public static <T> ResponseBuilder<T> failWithCustomInfo(ResponseCode code, String errorInfo) {
        return new ResponseBuilder(
                code.getCode(),
                errorInfo,
                "");
    }
}
