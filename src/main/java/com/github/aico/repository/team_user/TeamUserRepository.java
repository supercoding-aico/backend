package com.github.aico.repository.team_user;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser,Long>, QTeamUserRepository, CustomTeamUserRepository {
    @EntityGraph(attributePaths = {"team"})
    Page<TeamUser> findAllByUser(User user, Pageable pageable);
    @EntityGraph(attributePaths = {"team"})
    List<TeamUser> findByUserAndTeamTeamIdIn(User user, List<Long> teamIds);
    @EntityGraph(attributePaths = {"user", "user.userProfileImage"})
    List<TeamUser> findByTeamAndUserIdIn(Team team, Collection<Long> user_id);
    @EntityGraph(attributePaths = {"user"}) // user 정보도 함께 로드
    List<TeamUser> findByTeamTeamIdAndUserUserIdIn(Long teamId, List<Long> userIds);


    Optional<TeamUser> findByTeamAndUser(Team team, User user);

    List<TeamUser> findAllByUser(User user);
    List<TeamUser> findAllByTeam(Team team);
    List<TeamUser> findByTeamTeamIdIn(List<Long> teamIds);

    Optional<TeamUser> findByTeamTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeamAndUser(Team team, User user);

    @Query("SELECT COUNT(tu) > 0 FROM TeamUser tu WHERE tu.user = :user AND tu.team.teamId = :teamId AND tu.teamRole = :teamRole")
    boolean existsByUserAndTeamIdAndTeamRole(@Param("user") User user, @Param("teamId") Long teamId, @Param("teamRole") TeamRole teamRole);

    @Query("SELECT tu FROM TeamUser tu WHERE tu.user = :user AND tu.team.teamId = :teamId")
    List<TeamUser> findByUserAndTeamId(@Param("user") User user, @Param("teamId") Long teamId);


//    @Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.user WHERE tu.team = :team")
//    List<TeamUser> findAllByTeamFetchUser(Team team);
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT tu FROM TeamUser tu WHERE tu.team = :team AND tu.teamRole = :role")
//    List<TeamUser> findByTeamAndRoleWithLock(@Param("team") Team team, @Param("role") TeamRole role);
}
