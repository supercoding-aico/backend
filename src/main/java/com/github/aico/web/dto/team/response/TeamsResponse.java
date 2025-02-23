package com.github.aico.web.dto.team.response;

import com.github.aico.repository.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class TeamsResponse {
    private final Long teamId;
    private final String name;

    public static TeamsResponse from(Team team){
        return TeamsResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getTeamName())
                .build();
    }
}
