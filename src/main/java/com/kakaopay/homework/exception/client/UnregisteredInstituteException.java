package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UnregisteredInstituteException extends BaseRuntimeException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getDescription() {
        return "Given institute has not been registered.";
    }
}
