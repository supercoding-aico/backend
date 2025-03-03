package com.github.aico.repository.chat;

import com.github.aico.repository.team_user.TeamUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long>,CustomChatRepository {

    Page<Chat> findByTeamUserInOrderByCreatedAtMillisDesc(List<TeamUser> teamUsers, Pageable pageable);
}
