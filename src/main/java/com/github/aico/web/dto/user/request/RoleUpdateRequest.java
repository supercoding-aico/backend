package com.github.aico.web.dto.user.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleUpdateRequest {
    private String roleName; // "MANAGER" 또는 "MEMBER"
}