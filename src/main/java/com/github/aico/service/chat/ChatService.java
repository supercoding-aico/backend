package com.github.aico.service.chat;

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
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.chat.request.Chatting;
import com.github.aico.web.dto.chat.response.ChatResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final TeamUserRepository teamUserRepository;
    private final ChatRepository chatRepository;
    private final TeamRepository teamRepository;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    @Transactional
    public ResponseDto getTeamChatListResult(User user , Long teamId, Integer page) {
        List<Chatting> chattings = redisUtil.getMessageListFromRedis(teamId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀은 존재하지 않습니다."));
        if (!chattings.isEmpty()){
            redisUtil.removeChattingFromRedis(teamId);
            List<Chat> historyChats = chattings.stream()
                    .map(chat -> {
                        User findUser = userRepository.findById(chat.getUserId())
                                .orElseThrow(()-> new NotFoundException("유저를 찾을 수 없습니다."));
                        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,findUser)
                                .orElseThrow(()-> new NotFoundException("팀에 해당되어 있지 않은 유저가 있습니다."));
                        return Chat.of(chat, teamUser);
                    })
                    .toList();
            chatRepository.saveAllBatch(historyChats);

        }


        boolean exists = teamUserRepository.existsByTeamAndUser(team,user);
        if (!exists){
            throw new NotFoundException(teamId + "에 해당하는 유저가 아닙니다.");
        }
        Pageable pageable = PageRequest.of(page,10,Sort.by( Sort.Direction.DESC,"createdAt"));
        List<TeamUser> teamUsers = teamUserRepository.findTeamUsersByTeamFetchUser(team);
        Page<Chat> chats = chatRepository.findByTeamUserInOrderByCreatedAtMillisDesc(teamUsers,pageable);
        Page<ChatResponse> chatResponses = chats.map(ChatResponse::from);
        return new ResponseDto(HttpStatus.OK.value(),team.getTeamName() + "에 대한 채팅 리스트 조회",chatResponses);
    }
}
