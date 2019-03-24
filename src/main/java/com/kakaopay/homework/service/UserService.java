package com.kakaopay.homework.service;

import com.kakaopay.homework.domain.response.SignInResponse;
import com.kakaopay.homework.exception.client.InvalidAuthInfoException;

public interface UserService {
    void signOn(String userId, String password) throws InvalidAuthInfoException;

    SignInResponse signIn(String userId, String password) throws InvalidAuthInfoException;

    SignInResponse refreshToken(String token) throws InvalidAuthInfoException;
}
