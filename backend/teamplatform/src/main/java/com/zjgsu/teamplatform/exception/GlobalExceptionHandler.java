package com.zjgsu.teamplatform.exception;

import com.zjgsu.teamplatform.common.ErrorCode;
import com.zjgsu.teamplatform.common.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        return Result.failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常。
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public Result<Void> handleValidationException(Exception e) {
        return Result.failure(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * 处理请求体反序列化异常。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.failure(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    /**
     * 处理未捕获异常。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.failure(ErrorCode.INTERNAL_ERROR, e.getMessage());
    }
}
