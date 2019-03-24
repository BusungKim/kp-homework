package com.kakaopay.homework.controller;

import com.kakaopay.homework.domain.response.SignInResponse;
import com.kakaopay.homework.service.UserService;
import com.kakaopay.homework.support.JwtTokenProvider;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(final UserService userService,
                          final JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signon")
    public Callable<Void> signOn(@RequestParam("id") final String userId,
                                 @RequestParam("password") final String password) {
        return () -> {
            userService.signOn(userId, password);
            return null;
        };
    }

    @PostMapping("/signin")
    public Callable<SignInResponse> signIn(@RequestParam("id") final String userId,
                                           @RequestParam("password") final String password) {
        return () -> userService.signIn(userId, password);
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
