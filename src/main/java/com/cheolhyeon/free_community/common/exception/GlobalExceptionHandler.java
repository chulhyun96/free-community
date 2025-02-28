package com.cheolhyeon.free_community.common.exception;

import com.cheolhyeon.free_community.user.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ErrorResponse handleUserException(UserException e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.of(e.getErrorStatus());
    }
}
