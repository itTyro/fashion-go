package com.lzl.handler;

import com.lzl.constant.MessageConstant;
import com.lzl.exception.BaseException;
import com.lzl.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        ex.printStackTrace();
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result exceptionHandler(DuplicateKeyException e) {
        e.printStackTrace();
        log.error("重复字段异常信息：{}", e.getMessage());
        String message = "唯一字段异常";
        message = e.getCause().getMessage().split(" ")[2] + "已存在";
        return Result.error(message);
    }

    @ExceptionHandler
    public Result exceptionHandler(Exception e) {
        e.printStackTrace();
        log.info("其他异常: {}", e.getMessage());

        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

}
