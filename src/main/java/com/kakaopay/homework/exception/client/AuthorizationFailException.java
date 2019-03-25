package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class AuthorizationFailException extends BaseRuntimeException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getDescription() {
        return "Invalid bearer token";
    }
}
