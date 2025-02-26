package com.github.aico.service.chat;

import com.github.aico.repository.chat.Chat;
import com.github.aico.repository.chat.ChatRepository;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.service.exceptions.NotFoundException;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.chat.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final TeamUserRepository teamUserRepository;
    private final ChatRepository chatRepository;
    private final TeamRepository teamRepository;
    public ResponseDto getTeamChatListResult(User user , Long teamId, Integer page) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(()-> new NotFoundException(teamId + "에 해당하는 팀은 존재하지 않습니다."));
        boolean exists = teamUserRepository.existsByTeamAndUser(team,user);
        if (!exists){
            throw new NotFoundException(teamId + "에 해당하는 유저가 아닙니다.");
        }
        Pageable pageable = PageRequest.of(page,10,Sort.by( Sort.Direction.DESC,"createdAt"));
        List<TeamUser> teamUsers = teamUserRepository.findTeamUsersByTeamFetchUser(team);
        Page<Chat> chats = chatRepository.findByTeamUserInOrderByCreatedAtDesc(teamUsers,pageable);
        Page<ChatResponse> chatResponses = chats.map(ChatResponse::from);
        return new ResponseDto(HttpStatus.OK.value(),team.getTeamName() + "에 대한 채팅 리스트 조회",chatResponses);
    }
}
