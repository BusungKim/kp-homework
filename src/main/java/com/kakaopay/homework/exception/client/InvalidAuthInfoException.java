package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
public class InvalidAuthInfoException extends Exception implements BaseException {

    private String detail;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getDescription() {
        return "Invalid data related to authentication or authorization. " + detail;
    }
}
