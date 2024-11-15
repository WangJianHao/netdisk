package com.sen.netdisk.component;

import com.sen.netdisk.common.SenCommonResponse;
import com.sen.netdisk.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验
     *
     * @param ex 参数校验异常
     * @return 返回错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SenCommonResponse<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return SenCommonResponse.validateFailed(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public SenCommonResponse<String> handleBaseException(BusinessException ex) {
        return SenCommonResponse.failed(ex.getMessage());
    }

    // 可以添加其他异常处理方法
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public SenCommonResponse<String> handleRuntimeExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return SenCommonResponse.failed("未知异常");
    }
}

