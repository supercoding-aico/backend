package com.github.aico.web.dto.team.response;

import com.github.aico.repository.chat.Chat;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team_user.TeamUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class TeamsResponse {
    private final Long teamId;
    private final String name;
    private final LocalDateTime lastReadAt;
    private final LocalDateTime lastMessageAt;

    public static TeamsResponse of(Team team, TeamUser teamUser, LocalDateTime lastMessageAt){
        return TeamsResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getTeamName())
                .lastReadAt(teamUser.getChatReadAt())
                .lastMessageAt(lastMessageAt)
                .build();
    }
}
