package com.kakaopay.homework.exception.server;

import com.kakaopay.homework.exception.AbstractBaseException;
import org.springframework.http.HttpStatus;

public class AggregateOperationFailException extends AbstractBaseException {

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getDescription() {
        return "Failed to find requested value";
    }
}
