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

    Optional<TeamUser> findByTeamAndUser(Team team, User user);
    Optional<TeamUser> findByTeamTeamIdAndUserId(Long teamId, Long userId);    boolean existsByTeamAndUser(Team team, User user);

    List<TeamUser> findAllByUser(User user);
    List<TeamUser> findAllByTeam(Team team);

//    @Query(value = "UPDATE team_user tu " +
//            "SET tu.chat_read_at = :lastReadAt " +
//            "WHERE tu.user_id = :userId " +
//            "AND tu.team_id IN :teamIds", nativeQuery = true)
//    @Modifying
//    void updateChatReadAtBulk(@Param("userId") Long userId, @Param("teamIds") List<Long> teamIds, @Param("lastReadAt") LocalDateTime lastReadAt);

//    @Query(" SELECT tu.user.userId FROM TeamUser tu WHERE tu.team.teamId = :teamId ")
//    List<Long> findTeamUserIdsByTeam(Long teamId);

//    @Query(" SELECT tu.team.teamId FROM TeamUser tu WHERE tu.user.userId = :userId ")
//    List<Long> findTeamIdsByUser(Long userId);
//    @Query("SELECT tu FROM TeamUser tu JOIN FETCH tu.user WHERE tu.team = :team")
//    List<TeamUser> findAllByTeamFetchUser(Team team);
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT tu FROM TeamUser tu WHERE tu.team = :team AND tu.teamRole = :role")
//    List<TeamUser> findByTeamAndRoleWithLock(@Param("team") Team team, @Param("role") TeamRole role);
}
