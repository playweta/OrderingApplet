package com.yu.common.common.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import com.yu.common.common.util.result.Result;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理器
 */
@Slf4j
@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public Result exceptionHandler(ServiceException e, HttpServletRequest req) {
        log.error("[异常{}] [{}][{}] {} ", e.getCode(), req.getMethod(), req.getServletPath(), e.getMessage());
        return Result.newInstance(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e, HttpServletRequest req) {
        String message = null;

        if (e instanceof MissingServletRequestParameterException) {
            message = "请求错误 缺少参数";
        }

        if (e instanceof IllegalArgumentException) {
            message = "非法参数";
        }

        if (e instanceof MethodArgumentNotValidException) {
            message = "参数不符合要求";
        }

        if (e instanceof JsonParseException) {
            log.error("json字符串解析异常");
            message = "参数解析异常";
        }

        if (e instanceof RedisException) {
            log.error("redis异常");
        }

        if (e instanceof MysqlDataTruncation) {
            log.error("mysql异常 插入的数据格式有误");
            message = "输入的数据不符合要求";
        }

        log.error("[异常] [{}][{}] ", req.getMethod(), req.getServletPath());

        if (message == null)
            message = "服务器异常";
        e.printStackTrace(); // 未知异常就打印栈轨迹
        return Result.newInstance(500, message);
    }

}
