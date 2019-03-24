package com.kakaopay.homework.service;

import com.kakaopay.homework.domain.response.SignInResponse;

public interface UserService {
    void signOn(String userId, String password) throws Exception;

    SignInResponse signIn(String userId, String password) throws Exception;

    SignInResponse refreshToken(String token) throws Exception;
}
