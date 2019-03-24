package com.kakaopay.homework.controller;

import com.kakaopay.homework.domain.response.SignInResponse;
import com.kakaopay.homework.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(final UserService userService) {
        this.userService = userService;
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
        return () -> {
            String token = userService.signIn(userId, password);
            return SignInResponse.builder().userId(userId).token(token).build();
        };
    }
}
