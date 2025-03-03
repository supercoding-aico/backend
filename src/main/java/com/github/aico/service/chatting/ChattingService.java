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
    private final ChatRepository chatRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    public void sendChatting(Chatting chatting) {
//        Team team = teamRepository.findById(chatting.getTeamId())
//                .orElseThrow(()->new NotFoundException(chatting.getTeamId() + "에 해당하는 팀이 존재하지 않습니다."));
//        User user = userRepository.findById(chatting.getUserId())
//                .orElseThrow(()->new NotFoundException(chatting.getUserId()+ "에 해당하는 유저를 찾을 수 없습니다."));
//        TeamUser sendTeamUser =  teamUserRepository.findByTeamAndUser(team,user)
//                .orElseThrow(()-> new NotFoundException("해당 팀에 유저가 속해 있지 않습니다."));
//        Chat chat = Chat.of(chatting,sendTeamUser);

//        chatRepository.save(chat);
        chatting.saveCreatedAt();
        log.info("chatting");
        redisUtil.addChatting(chatting);
        log.info("chatting" + chatting);
        log.info("chatting" + chatting.getTeamId());

        messagingTemplate.convertAndSend("/topic/room/" + chatting.getTeamId(), chatting);
    }
}
