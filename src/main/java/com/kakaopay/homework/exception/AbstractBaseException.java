package com.kakaopay.homework.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractBaseException extends RuntimeException {
    public abstract HttpStatus getHttpStatus();

    public abstract String getDescription();
}
