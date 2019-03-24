package com.kakaopay.homework.service.impl;

import com.google.common.base.Strings;
import com.kakaopay.homework.dao.UserRepository;
import com.kakaopay.homework.domain.entity.User;
import com.kakaopay.homework.domain.response.SignInResponse;
import com.kakaopay.homework.exception.client.InvalidAuthInfoException;
import com.kakaopay.homework.service.UserService;
import com.kakaopay.homework.support.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(final UserRepository userRepository,
                           final JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void signOn(String userId, String password) throws InvalidAuthInfoException {
        if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(password)) {
            throw new InvalidAuthInfoException("Empty value is not allowed.");
        }
        if (userRepository.findOne(userId) != null) {
            throw new InvalidAuthInfoException("Already registered user.");
        }
        User user = User.builder()
                .id(userId)
                .encodedPassword(Base64.getEncoder().encodeToString(password.getBytes()))
                .build();
        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(String userId, String password) throws InvalidAuthInfoException {
        User user = userRepository.findOne(userId);
        if (user == null) {
            log.error("No user registered with id {}", userId);
            throw new InvalidAuthInfoException("No user registered with given id.");
        }
        byte[] passwordBytes = user.getEncodedPassword().getBytes();
        if (!password.equals(new String(Base64.getDecoder().decode(passwordBytes)))) {
            log.error("Password is incorrect");
            throw new InvalidAuthInfoException("Password is incorrect.");
        }

        return SignInResponse.builder().userId(userId).token(jwtTokenProvider.createToken(userId)).build();
    }

    @Override
    public SignInResponse refreshToken(String token) throws InvalidAuthInfoException {
        String userId = jwtTokenProvider.getUserId(token);
        if (userRepository.findOne(userId) == null) {
            throw new InvalidAuthInfoException("There is no user registered with given id.");
        }
        return SignInResponse.builder().userId(userId).token(jwtTokenProvider.createToken(userId)).build();
    }
}
