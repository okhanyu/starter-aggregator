package net.ninini.starter.response.code;

/**
 * @ClassName: ResponseCodeCommon
 * @ProjectName starter
 * @Description: todo
 * @Author HanYu
 * @Date 2021/6/24 14:17
 * @Version 1.0.0
 */
public enum ResponseCode {

    //全局组
    SUCCESS("GLOBAL.SUCCESS", "成功"),
    UNKNOWN_FAIL("GLOBAL.UNKNOWN_FAIL", "失败，未知错误"),
    UNKNOWN_EXCEPTION("GLOBAL.UNKNOWN_EXCEPTION", "失败，系统异常"),

    //参数组
    PARAM_ERROR("PARAM.ERROR", "参数错误"),
    PARAM_ERROR_CUSTOM_INFO("PARAM.ERROR_CUSTOM", "参数错误"),

    ACCESS_FAIL_TOKEN_INVALID("ACCESS.FAIL_TOKEN_INVALID","访问失败，TOKEN无效"),
    ACCESS_FAIL_TOKEN_REQUIRED("ACCESS.FAIL_TOKEN_REQUIRED","访问失败，TOKEN必传"),
    ACCESS_FAIL_PERMISSION_DENIED("ACCESS.FAIL_PERMISSION_DENIED","访问失败，你的权限不足"),

    LOGIN_FAIL_SYSTEM_ERROR("LOGIN.FAIL_SYSTEM_ERROR","登录失败，系统内部错误"),
    LOGIN_FAIL_USER_PWD_INVALID("LOGIN.FAIL_USER_PWD_INVALID","登录失败，用户名或密码无效"),
    LOGIN_FAIL_USER_NOT_EXIST("LOGIN.FAIL_USER_NOT_EXIST","登录失败，用户不存在"),
    LOGIN_FAIL_PWD_INVALID("LOGIN.FAIL_PWD_INVALID","登录失败，密码无效");

    private String code;
    private String info;

    ResponseCode(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }


}
