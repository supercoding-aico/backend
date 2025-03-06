package com.github.aico.service.chatting;

import com.github.aico.repository.chat.Chat;
import com.github.aico.repository.chat.ChatRepository;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.service.redis.RedisUtil;
import com.github.aico.web.dto.chat.request.Chatting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisUtil redisUtil;

    public void sendChatting(Chatting chatting) {
        chatting.saveCreatedAt();
        redisUtil.addChatting(chatting);
        messagingTemplate.convertAndSend("/topic/room/" + chatting.getTeamId(), chatting);
    }
}
