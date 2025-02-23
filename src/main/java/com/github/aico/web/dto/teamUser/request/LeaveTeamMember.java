package com.github.aico.web.dto.teamUser.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class LeaveTeamMember {
    private final Long userId;
}
