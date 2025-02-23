package com.github.aico.web.dto.teamUser.response;

import com.github.aico.repository.team_user.TeamRole;
import com.github.aico.repository.team_user.TeamUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@Builder
public class TeamMember {
    private final Long userId;
    private final String nickname;
    private final String email;
    private final TeamRole teamRole;
    public static TeamMember from(TeamUser teamUser){
        return TeamMember.builder()
                .userId(teamUser.getUser().getUserId())
                .nickname(teamUser.getUser().getNickname())
                .email(teamUser.getUser().getEmail())
                .teamRole(teamUser.getTeamRole())
                .build();
    }
}
