package com.github.aico.repository.team_user;

import com.github.aico.repository.team.Team;

import java.util.List;

public interface QTeamUserRepository {
    List<TeamUser> findTeamUsersByTeamFetchUser(Team team);
    List<TeamUser> findByTeamAndRoleWithLockDsl(Team team, TeamRole role);
}
