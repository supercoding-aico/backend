package com.github.aico.repository.team;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

    @Modifying
    @Query("DELETE FROM Team t WHERE t.teamId = :teamId")
    void deleteTeamById(@Param("teamId") Long teamId);


}
