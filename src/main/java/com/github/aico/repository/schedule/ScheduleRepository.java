package com.github.aico.repository.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.team.teamId = :teamId AND s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Schedule> findByTeamIdAndDateRange(Long teamId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT s FROM Schedule s WHERE s.team.teamId = :teamId")
    List<Schedule> findByTeamId(Long teamId);

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.team t " +
            "JOIN TeamUser tu ON tu.team = t " +
            "WHERE tu.user.userId = :userId AND t.teamId = :teamId")
    List<Schedule> findByUserAndTeamId(Long userId, Long teamId);

}
