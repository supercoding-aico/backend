package com.github.aico.web.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class SignUpRequest {
    private final String nickname;
    private final String email;
    private final String password;
    private final String phoneNumber;

}
