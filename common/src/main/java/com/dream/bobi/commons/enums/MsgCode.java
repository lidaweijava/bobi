package com.dream.bobi.commons.enums;

public enum MsgCode {

    /* 系统信息，以00开头 */
    SYS_OK(200, "操作成功"),
    CHALLENGE_SUCCESS(20001, "挑战成功"),
    SYS_ERROR(50000, "系统异常"),
    SYS_ACCOUNT_ERROR(50001, "账号/密码错误"),
    SYS_USER_IS_EXIT(50002, "账号已经注册，请检查或使用其他账号注册！"),
    SYS_PARAM_ERROR(50003, "参数错误"),
    SYS_PASSWORD_NOT_NULL(50004, "密码不能为空!"),
    SYS_USER_NAME_NOT_NULL(50005, "用户名不能为空!"),
    SYS_REGISTER_FAIL(50006, "注册失败，请重新注册!"),
    SYS_PHONE_NOT_NULL(50007, "手机号不能为空!"),
    SYS_TOKEN_NOT_NULL(50008, "token不能为空!"),
    SYS_INVALID_LOGIN(50009, "登录已失效，请重新登录"),
    SYS_USER_IS_NOT_EXIT(50010, "没有关联的用户!"),
    PARAM_ERROR_MONEY_MUST_GREATER_THAN_0(50011, "金额必须大于0"),
    SYS_RECHARGE_FAILED(50012, "充值失败"),
    SYS_REGISTER_PHONE_NOT_VALID(500014, "手机格式不正确!"),

    SYS_DEDUCT_FAILED(50013, "扣减失败"),
    CHALLENGE_CONFIG_NOT_FOUND(50014, "挑战配置未找到"),
    CHALLENGE_FAILED(50015, "挑战失败"),
    CHALLENGE_EXCEED_MAX_TIMES(50016, "挑战超过最大次数"),

    SESSION_TIMEOUT(50017, "会话超时"),
    CHALLENGE_IS_OVER(50018, "挑战已结束"),
    CHALLENGE_NOT_FOUND(50019, "挑战记录未找到"),
    VERIFY_CODE_NOT_NULL(50020, "验证码不能为空!"),

    OTHER(59999, "其他");


    int code;
    String message;

    MsgCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
