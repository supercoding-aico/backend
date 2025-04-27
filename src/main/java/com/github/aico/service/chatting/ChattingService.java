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
import com.github.aico.web.dto.chat.response.Toggle;
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


    //채팅 보내기
    public void sendChatting(Chatting chatting) {
        List<Long> userIds = redisUtil.getUserIdByTeamId(chatting.getTeamId());
        //그팀에 유저가 있는지 검증
        //매번 db 조회가 아닌 redis에 저장해두었다가 빠르게 가져와서 검증
        if (!checkUserInTeam(chatting.getTeamId(), chatting.getUserId())) {
            return;
        }
        //content길이가 넘지 않는지
        if (checkContentLength(chatting.getContent())) {
            return;
        }
        chatting.saveCreatedAt();
        redisUtil.addChatting(chatting);

        messagingTemplate.convertAndSend("/topic/room/" + chatting.getTeamId(), chatting);

        sendNewMessage(userIds);
    }


    //유저가 채팅방에서 끊기고 연결된 시간
    @Transactional
    public void roomInactiveUserResult(ActiveTeamUser activeTeamUser) {
        //유저가 팀에 속해 있는지 검증
        if (checkUserInTeam(activeTeamUser.getTeamId(),activeTeamUser.getUserId())){
            return;
        }
        activeTeamUser.saveLastReadAt();
        log.info(activeTeamUser.getLastReadAt()+ "시간");
        //redis에 저장해두었다가 팀 불러올 때 db에 저장
        redisUtil.addTeamLastReadAt(activeTeamUser);
    }
    //유저가 해당 팀에 속해있는지
    private boolean checkUserInTeam(Long teamId, Long userId){
        List<Long> userIds = redisUtil.getUserIdByTeamId(teamId);
        return userIds.contains(userId);
    }
    //content길이 검증
    private boolean checkContentLength(String content) {
        return content == null
                || content.trim().isEmpty()
                || content.length() >= 255;
    }

    //새로운 메시지가 도작했다는 Boolean 값보내기
    //sendChatting에서 해당 메소드 사용
    public void sendNewMessage(List<Long> userIds){
        if (!userIds.isEmpty()){
            for (Long userId : userIds){
                messagingTemplate.convertAndSend("/topic/toggle/" + userId, Toggle.status());
            }
        }
    }
}
