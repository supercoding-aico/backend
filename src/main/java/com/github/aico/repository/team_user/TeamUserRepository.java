package com.github.aico.repository.team_user;

import com.github.aico.repository.team.Team;
import com.github.aico.repository.user.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamUserRepository extends JpaRepository<TeamUser,Long>, QTeamUserRepository {
    @EntityGraph(attributePaths = {"team"})
    Page<TeamUser> findAllByUser(User user, Pageable pageable);

    Optional<TeamUser> findByTeamAndUser(Team team, User user);
    Optional<TeamUser> findByTeamTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeamAndUser(Team team, User user);

    @Query("SELECT COUNT(tu) > 0 FROM TeamUser tu WHERE tu.user = :user AND tu.team.teamId = :teamId AND tu.teamRole = :teamRole")
    boolean existsByUserAndTeamIdAndTeamRole(@Param("user") User user, @Param("teamId") Long teamId, @Param("teamRole") TeamRole teamRole);

    @Query("SELECT tu FROM TeamUser tu WHERE tu.user = :user AND tu.team.teamId = :teamId")
    List<TeamUser> findByUserAndTeamId(@Param("user") User user, @Param("teamId") Long teamId);

    List<TeamUser> findAllByTeam(Team team);;
//    @Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.user WHERE tu.team = :team")
//    List<TeamUser> findAllByTeamFetchUser(Team team);
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT tu FROM TeamUser tu WHERE tu.team = :team AND tu.teamRole = :role")
//    List<TeamUser> findByTeamAndRoleWithLock(@Param("team") Team team, @Param("role") TeamRole role);
}
