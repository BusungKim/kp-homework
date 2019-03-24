package com.kakaopay.homework.service.impl;

import com.kakaopay.homework.dao.UserRepository;
import com.kakaopay.homework.domain.entity.User;
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
    public void signOn(String userId, String password) throws Exception {
        User user = User.builder()
                .id(userId)
                .encodedPassword(Base64.getEncoder().encodeToString(password.getBytes()))
                .build();
        userRepository.save(user);
    }

    @Override
    public String signIn(String userId, String password) throws Exception {
        User user = userRepository.findOne(userId);
        if (user == null) {
            log.error("No user registered with id {}", userId);
            throw new Exception();
        }
        byte[] passwordBytes = user.getEncodedPassword().getBytes();
        if (!password.equals(new String(Base64.getDecoder().decode(passwordBytes)))) {
            log.error("Password is incorrect");
            throw new Exception();
        }

        return jwtTokenProvider.createToken(userId);
    }
}
