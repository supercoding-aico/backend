package com.github.aico.config.websocket;

import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.chatting.ChattingService;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.service.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final TeamUserRepository teamUserRepository;

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    //유저가 웹소켓연결이 모종의 이유로 끊겼을 때 유저가 읽은 시간 처리
    @EventListener
    @Transactional
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Long userId =  (Long)headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            log.info("끊긴 유저 값 : " +  userId);
            List<Long> userTeamIds = redisUtil.getTeamIdByUserId(userId);

            List<TeamUser> myTeamUsers = teamUserRepository.findByTeamTeamIdIn(userTeamIds);
            myTeamUsers.forEach(TeamUser::updateChatReadAt);
        } else {
            log.warn("유저에 해당하는 세션이 남아있지 않습니다.");
        }
    }
}
