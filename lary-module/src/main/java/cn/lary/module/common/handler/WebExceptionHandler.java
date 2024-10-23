package cn.lary.module.common.handler;

import cn.lary.common.dto.SingleResponse;
import cn.lary.common.kit.ResponseKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public SingleResponse handleValidationException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseKit.fail(e.getAllErrors().get(0).getDefaultMessage());
    }
}
