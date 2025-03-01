package com.github.aico.repository.team_user;

import com.github.aico.repository.team.QTeam;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QTeamUserRepositoryImpl implements QTeamUserRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamUser> findTeamUsersByTeamFetchUser(Team team) {
        QTeamUser teamUser = QTeamUser.teamUser;
        QUser user = QUser.user;


        return jpaQueryFactory.selectFrom(teamUser)
                .join(teamUser.user,user).fetchJoin()
                .where(teamUser.team.eq( team))
                .fetch();
    }
    @Override
    public List<TeamUser> findByTeamAndRoleWithLockDsl(Team team, TeamRole role) {
        QTeamUser teamUser = QTeamUser.teamUser;

        return jpaQueryFactory.selectFrom(teamUser)
                .where(teamUser.team.eq(team)
                        .and(teamUser.teamRole.eq(role)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)  // 락 설정
                .fetch();  // 결과 리스트 반환
    }
}
