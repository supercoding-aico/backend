package com.github.aico.repository.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.scheduleUsers su " +
            "JOIN FETCH su.teamUser tu " +
            "JOIN FETCH tu.user " +
            "WHERE s.team.teamId = :teamId AND s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Schedule> findByTeamIdAndDateRange(@Param("teamId") Long teamId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    @Query("SELECT s FROM Schedule s WHERE s.team.teamId = :teamId")
    List<Schedule> findByTeamId(Long teamId);

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.scheduleUsers su " +
            "JOIN FETCH su.teamUser tu " +
            "JOIN FETCH tu.user " +
            "JOIN s.team t " +
            "JOIN TeamUser tu2 ON tu2.team = t " +
            "WHERE tu2.user.userId = :userId AND t.teamId = :teamId")
    List<Schedule> findByUserAndTeamId(@Param("userId") Long userId, @Param("teamId") Long teamId);

}
