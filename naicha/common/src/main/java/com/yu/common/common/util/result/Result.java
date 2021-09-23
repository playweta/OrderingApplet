package com.yu.common.common.util.result;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 封装RestController返回的结果
 */
@Data
@ApiModel("封装RestController返回的结果")
public class Result<T> implements Serializable {
    private int code;
    private String message;
    private T data;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static int CONST_ok = 200;
    public static int CONST_fail = 400;

    public static <T> Result<T> ok() {
        return new Result(CONST_ok, "成功", null);
    }

    public static <T> Result<T> ok(T result) {
        return new Result(CONST_ok, "成功", result);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result(CONST_ok, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result(CONST_fail, message, null);
    }

    public static <T> Result<T> fail(String message, T data) {
        return new Result(CONST_fail, message, data);
    }

    public static <T> Result<T> newInstance(int code, String message){
        return new Result(code, message, null);
    }
}
