package com.github.aico.repository.team_user;


import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.QUser;
import com.github.aico.repository.user.QUserProfileImage;
import com.github.aico.repository.user.User;


import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.aico.repository.team_user.QTeamUser.teamUser;

@Repository
@RequiredArgsConstructor
public class QTeamUserRepositoryImpl implements QTeamUserRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamUser> findTeamUsersByTeamFetchUser(Team team) {
        QTeamUser teamUser = QTeamUser.teamUser;
        QUser user = QUser.user;
        QUserProfileImage userProfileImage = QUserProfileImage.userProfileImage;


        return jpaQueryFactory.selectFrom(teamUser)
                .join(teamUser.user,user).fetchJoin()
                .join(user.userProfileImage, userProfileImage).fetchJoin()
                .where(teamUser.team.eq( team))
                .fetch();
    }
    @Override
    public List<Long> findTeamIdsByUser(Long userId) {
        return jpaQueryFactory
                .select(teamUser.team.teamId)
                .from(teamUser)
                .where(teamUser.user.userId.eq(userId))
                .fetch();
    }
    @Override
    public List<Long> findTeamUserIdsByTeam(Long teamId) {
        return jpaQueryFactory
                .select(teamUser.user.userId)
                .from(teamUser)
                .where(teamUser.team.teamId.eq(teamId))
                .fetch();
    }




    @Override
    public List<TeamUser> findByTeamWithLockDsl(Team team) {
        QTeamUser teamUser = QTeamUser.teamUser;

        return jpaQueryFactory.selectFrom(teamUser)
                .where(teamUser.team.eq(team))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }
    @Override
    public List<TeamUser> findByTeamAndRoleWithLockDsl(Team team, TeamRole role) {
        QTeamUser teamUser = QTeamUser.teamUser;
        return jpaQueryFactory.selectFrom(teamUser)
                .where(teamUser.team.eq(team)
                        .and(teamUser.teamRole.eq(role)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }
    @Override
    public List<TeamUser> findByTeamIdInAndUserIdIn(Set<Long> teamIds, Set<Long> userIds) {
        QTeamUser teamUser = QTeamUser.teamUser;

        return jpaQueryFactory
                .selectFrom(teamUser)
                .where(
                        teamUser.team.teamId.in(teamIds)
                                .and(teamUser.user.userId.in(userIds))
                )
                .fetch();
    }
}
