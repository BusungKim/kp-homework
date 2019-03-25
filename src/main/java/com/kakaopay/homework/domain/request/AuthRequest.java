package com.kakaopay.homework.domain.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AuthRequest {
    @NotNull
    private String id;
    @NotNull
    private String password;
}
