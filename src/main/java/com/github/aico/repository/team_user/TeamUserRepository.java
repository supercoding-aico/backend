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
public interface TeamUserRepository extends JpaRepository<TeamUser,Long> {
    @EntityGraph(attributePaths = {"team"})
    Page<TeamUser> findAllByUser(User user, Pageable pageable);

    Optional<TeamUser> findByTeamAndUser(Team team, User user);

    List<TeamUser> findAllByTeam(Team team);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tu FROM TeamUser tu WHERE tu.team = :team AND tu.teamRole = :role")
    List<TeamUser> findByTeamAndRoleWithLock(@Param("team") Team team, @Param("role") TeamRole role);
}
