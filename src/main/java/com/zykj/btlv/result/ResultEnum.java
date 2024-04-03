package com.zykj.btlv.result;

public enum ResultEnum {
    //这里是可以自己定义的，方便与前端交互即可
    FAIL(501,"失败"),

    AAMBGT(502,"金额超出限制"),
    TAAISANRAIR(503,"授权金额充足，无需重复授权"),
    SUCCESS(200,"成功"),
    DO_NOT_SUBMIT_TWICE(504,"重复提交"),
    ;
    private Integer code;
    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
