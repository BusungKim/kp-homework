package com.kakaopay.homework.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {
    HttpStatus getHttpStatus();

    String getDescription();
}
