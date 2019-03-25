package com.kakaopay.homework.support;

import com.kakaopay.homework.domain.response.ApiError;
import com.kakaopay.homework.exception.AbstractBaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {AbstractBaseException.class})
    public ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) throws Exception {
        if (ex instanceof AbstractBaseException) {
            AbstractBaseException baseException = ((AbstractBaseException) ex);
            String description = baseException.getDescription();

            return new ResponseEntity<>(new ApiError(description), baseException.getHttpStatus());
        }
        return new ResponseEntity<>(new ApiError("Unknown exception. " + ex.getLocalizedMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
