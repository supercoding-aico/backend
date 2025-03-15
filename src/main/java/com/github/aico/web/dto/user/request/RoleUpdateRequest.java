package com.github.aico.web.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class RoleUpdateRequest {
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "teamRole은 필수입니다.")
    private String teamRole; // "MANAGER" 또는 "MEMBER"
}