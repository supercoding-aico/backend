package com.github.aico.repository.chat;

import com.github.aico.repository.team_user.TeamUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long>,CustomChatRepository, QChatRepository {

    Page<Chat> findByTeamUserInOrderByCreatedAtMillisDesc(List<TeamUser> teamUsers, Pageable pageable);
//    @Query("SELECT new com.github.aico.repository.chat.TeamLatestChatTimeDto(c.teamUser.team.teamId, MAX(c.createdAtMillis)) " +
//            "FROM Chat c WHERE c.teamUser.team.teamId IN :teamIds GROUP BY c.teamUser.team.teamId")
//    List<TeamLatestChatTimeDto> findLatestChatTimesByTeamIds(@Param("teamIds") List<Long> teamIds);


}
