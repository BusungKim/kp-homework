package com.kakaopay.homework.exception.client;

import com.kakaopay.homework.exception.BaseRuntimeException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
public class InvalidAuthInfoException extends BaseRuntimeException {

    private String detail;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    public String getDescription() {
        return "Invalid data related to authentication or authorization. " + detail;
    }
}
