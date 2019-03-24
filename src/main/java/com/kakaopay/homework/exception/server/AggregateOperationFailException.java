package com.kakaopay.homework.exception.server;

import com.kakaopay.homework.exception.BaseException;
import org.springframework.http.HttpStatus;

public class AggregateOperationFailException extends Exception implements BaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getDescription() {
        return "Failed to find requested value";
    }
}
