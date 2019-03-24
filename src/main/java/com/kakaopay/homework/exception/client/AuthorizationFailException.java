package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseException;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletException;

public class AuthorizationFailException extends ServletException implements BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getDescription() {
        return "Invalid bearer token";
    }
}
