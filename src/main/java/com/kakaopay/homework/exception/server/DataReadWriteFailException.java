package com.kakaopay.homework.exception.server;

import com.kakaopay.homework.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
public class DataReadWriteFailException extends RuntimeException implements BaseException {

    private String detail;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getDescription() {
        return "Failed to read data from database or write data to database. " + detail;
    }
}
