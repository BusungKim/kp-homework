package com.kakaopay.homework.service;

public interface UserService {
    void signOn(String userId, String password) throws Exception;

    String signIn(String userId, String password) throws Exception;
}
