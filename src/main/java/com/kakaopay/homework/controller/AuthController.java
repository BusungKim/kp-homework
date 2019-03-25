package com.kakaopay.homework.controller;

import com.kakaopay.homework.domain.request.AuthRequest;
import com.kakaopay.homework.domain.response.SignInResponse;
import com.kakaopay.homework.service.UserService;
import com.kakaopay.homework.support.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.Callable;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(final UserService userService,
                          final JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public Callable<Void> signUp(@Valid @RequestBody final AuthRequest authRequest) {
        return () -> {
            userService.signUp(authRequest.getId(), authRequest.getPassword());
            return null;
        };
    }

    @PostMapping("/signin")
    public Callable<SignInResponse> signIn(@Valid @RequestBody final AuthRequest authRequest) {
        return () -> userService.signIn(authRequest.getId(), authRequest.getPassword());
    }

    @PostMapping("/refresh")
    public Callable<SignInResponse> refresh(final HttpServletRequest httpServletRequest) {
        return () -> {
            String bearerToken = jwtTokenProvider.resolveToken(httpServletRequest);
            if (bearerToken == null) {
                throw new Exception();
            }
            return userService.refreshToken(bearerToken);
        };
    }
}
