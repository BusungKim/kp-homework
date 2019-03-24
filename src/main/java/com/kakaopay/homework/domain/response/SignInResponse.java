package com.kakaopay.homework.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SignInResponse {
    private String userId;
    private String token;
}
