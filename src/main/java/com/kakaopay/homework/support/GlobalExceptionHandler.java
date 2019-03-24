package com.kakaopay.homework.support;

import com.kakaopay.homework.domain.response.ApiError;
import com.kakaopay.homework.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        if (ex instanceof BaseException) {
            BaseException baseException = ((BaseException) ex);
            String description = baseException.getDescription();
            return new ResponseEntity<>(new ApiError(description), baseException.getHttpStatus());
        }
        return new ResponseEntity<>(new ApiError("Unknown exception"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
