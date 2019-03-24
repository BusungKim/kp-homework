package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UnregisteredInstituteException extends Exception implements BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getDescription() {
        return "Given institute has not been registered.";
    }
}
