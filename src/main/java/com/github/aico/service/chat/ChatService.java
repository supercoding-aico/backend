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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final TeamUserRepository teamUserRepository;
    private final ChatRepository chatRepository;
    private final TeamRepository teamRepository;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

@Transactional
public ResponseDto getTeamChatListResult(User user, Long teamId, Integer page) {
    List<Chatting> chattings = redisUtil.getMessageListFromRedis(teamId);
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new NotFoundException(teamId + "에 해당하는 팀은 존재하지 않습니다."));

    // Redis 채팅을 DB에 저장
    if (!chattings.isEmpty()) {
        saveRedisChatsToDatabase(chattings, team);
    }

    // 유저가 팀에 속해 있는지 검증
    boolean exists = teamUserRepository.existsByTeamAndUser(team, user);
    if (!exists) {
        throw new NotFoundException(teamId + "에 해당하는 유저가 아닙니다.");
    }

    // 페이징된 채팅 리스트 조회
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAtMillis"));
    List<TeamUser> teamUsers = teamUserRepository.findTeamUsersByTeamFetchUser(team);
    Page<Chat> chats = chatRepository.findByTeamUserInOrderByCreatedAtMillisDesc(teamUsers, pageable);
    Page<ChatResponse> chatResponses = chats.map(ChatResponse::from);

    return new ResponseDto(HttpStatus.OK.value(), team.getTeamName() + "에 대한 채팅 리스트 조회", chatResponses);
}

    // Redis 채팅을 DB에 저장하는 메서드
    private void saveRedisChatsToDatabase(List<Chatting> chattings, Team team) {
        Set<Long> userIds = chattings.stream().map(Chatting::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));
        Map<Long, TeamUser> teamUserMap = teamUserRepository.findByTeamAndUserIdIn(team, userIds).stream()
                .collect(Collectors.toMap(tu -> tu.getUser().getUserId(), Function.identity()));

        List<Chat> historyChats = chattings.stream()
                .map(chat -> {
                    User foundUser = userMap.get(chat.getUserId());
                    if (foundUser == null) {
                        throw new NotFoundException("유저를 찾을 수 없습니다: " + chat.getUserId());
                    }
                    TeamUser teamUser = teamUserMap.get(chat.getUserId());
                    if (teamUser == null) {
                        throw new NotFoundException("팀에 해당되지 않은 유저가 있습니다: " + chat.getUserId());
                    }
                    return Chat.of(chat, teamUser);
                })
                .toList();

        redisUtil.removeChattingFromRedis(team.getTeamId());
        chatRepository.saveAllBatch(historyChats);
    }


    /**
     * 일정 시간마다 redis에 남아있는 채팅 저장해주기
     * */
//    @Scheduled(fixedRate = 1800000) //30분
//    public void chatHistorySave(){
//        List<Chatting> chattings = redisUtil.getAllMessage();
//        if (!chattings.isEmpty()){
//            redisUtil.deleteAllMessage();
//            List<Chat> historyChats = chattings.stream()
//                    .map(chat -> {
//                        User findUser = userRepository.findById(chat.getUserId())
//                                .orElseThrow(()-> new NotFoundException("유저를 찾을 수 없습니다."));
//                        Team team = teamRepository.findById(chat.getTeamId())
//                                .orElseThrow(()-> new NotFoundException(chat.getTeamId() + "에 해당하는 팀은 존재하지 않습니다."));
//                        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team,findUser)
//                                .orElseThrow(()-> new NotFoundException("팀에 해당되어 있지 않은 유저가 있습니다."));
//                        return Chat.of(chat, teamUser);
//                    })
//                    .toList();
//            chatRepository.saveAllBatch(historyChats);
//        }
//    }
}
