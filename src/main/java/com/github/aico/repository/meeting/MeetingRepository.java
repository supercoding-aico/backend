package com.github.aico.repository.meeting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    @Query("SELECT m FROM Meeting m " +
            "JOIN FETCH m.participants mu " +
            "JOIN FETCH mu.teamUser tu " +
            "JOIN FETCH tu.user " +
            "WHERE m.team.teamId = :teamId")
    Page<Meeting> findByTeamId(@Param("teamId") Long teamId, Pageable pageable);
}