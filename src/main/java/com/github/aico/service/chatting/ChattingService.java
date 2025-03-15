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
import com.github.aico.web.dto.chat.request.ActiveTeamUser;
import com.github.aico.web.dto.chat.request.Chatting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChattingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisUtil redisUtil;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public void sendChatting(Chatting chatting) {
        chatting.saveCreatedAt();
        redisUtil.addChatting(chatting);

        messagingTemplate.convertAndSend("/topic/room/" + chatting.getTeamId(), chatting);
//        List<Long> userIds = teamUserRepository.findTeamUserIdsByTeam(chatting.getTeamId());
        List<Long> userIds = redisUtil.getUserIdByTeamId(chatting.getTeamId());
        if (!userIds.isEmpty()){
            for (Long userId : userIds){
                messagingTemplate.convertAndSend("/topic/toggle/" + userId,true);
            }
        }

    }

    //유저가 채팅방에서 끊기고 연결된 시간
    @Transactional
    public void roomInactiveUserResult(ActiveTeamUser activeTeamUser) {
        activeTeamUser.saveLastReadAt();
        log.info(activeTeamUser.getLastReadAt()+ "시간");
        //redis에 저장해두었다가 팀 불러올 때 db에 저장
        redisUtil.addTeamLastReadAt(activeTeamUser);


    }
}
