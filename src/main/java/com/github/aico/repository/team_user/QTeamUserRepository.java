package com.github.aico.repository.team_user;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface QTeamUserRepository {
    List<TeamUser> findTeamUsersByTeamFetchUser(Team team);
    List<TeamUser> findByTeamAndRoleWithLockDsl(Team team, TeamRole role);
    List<TeamUser> findByTeamWithLockDsl(Team team);
    List<Long> findTeamIdsByUser(Long userId);
    List<Long> findTeamUserIdsByTeam(Long teamId);
    List<TeamUser> findByTeamIdInAndUserIdIn(Set<Long> teamIds, Set<Long> userIds);

}
