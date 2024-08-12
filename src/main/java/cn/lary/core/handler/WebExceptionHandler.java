package cn.lary.core.handler;

import cn.lary.core.dto.SingleResponse;
import cn.lary.kit.ResKit;
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
        return ResKit.fail(e.getAllErrors().get(0).getDefaultMessage());
    }
}
