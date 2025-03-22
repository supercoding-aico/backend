package com.github.aico.service.meeting;

import com.github.aico.repository.meeting.Meeting;
import com.github.aico.repository.meeting.MeetingRepository;
import com.github.aico.repository.meeting_user.MeetingUser;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team.TeamRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.repository.user.UserRepository;
import com.github.aico.service.openai.OpenAiClient;
import com.github.aico.service.redis.RedisUtil;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.chat.request.Chatting;
import com.github.aico.web.dto.meeting.request.MeetingAiRequest;
import com.github.aico.web.dto.meeting.request.MeetingUpdateRequest;
import com.github.aico.web.dto.meeting.response.MeetingAiResponse;
import com.github.aico.web.dto.meeting.response.MeetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final OpenAiClient openAiClient;
    private final RedisUtil redisUtil;

    @Transactional
    public ResponseDto requestAiSummary(Long teamId, List<MeetingAiRequest> requestList, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found for teamId: " + teamId));

        // 파라미터에서 받아온 request에서 유저 가져오기
        Set<Long> participantIds = requestList.stream()
                .map(MeetingAiRequest::getUserId)
                .collect(Collectors.toSet());

        // DB에서 사용자 정보 조회
        List<User> participantsUsers = userRepository.findAllByIdIn(participantIds);
        Map<Long, User> userMap = participantsUsers.stream()
                .collect(Collectors.toMap(User::getUserId, Function.identity()));

        // TeamUser 매핑
        List<TeamUser> teamUsers = teamUserRepository.findByTeamAndUserIdIn(team, participantIds);
        Map<Long, TeamUser> teamUserMap = teamUsers.stream()
                .collect(Collectors.toMap(tu -> tu.getUser().getUserId(), Function.identity()));

        // 채팅 내용 결합
        String combinedContent = requestList.stream()
                .map(request -> String.format("**%s**: %s", userMap.get(request.getUserId()).getNickname(), request.getContent()))
                .collect(Collectors.joining("\n"));

        String aiResponse = openAiClient.getAiSummary(combinedContent);
        String markdownResponse = String.format("### 회의록 요약\n%s", aiResponse);

        Meeting meeting = Meeting.builder()
                .team(team)
                .content(markdownResponse)
                .participants(new ArrayList<>())
                .build();

        // 모든 채팅 참여자를 participants에 추가
        List<MeetingUser> participants = participantIds.stream()
                .map(userId -> {
                    TeamUser teamUser = teamUserMap.get(userId);
                    if (teamUser == null) {
                        throw new IllegalArgumentException("TeamUser not found for userId: " + userId + " in teamId: " + teamId);
                    }
                    return MeetingUser.builder()
                            .meeting(meeting)
                            .teamUser(teamUser)
                            .build();
                })
                .collect(Collectors.toList());
        meeting.getParticipants().addAll(participants);

        meetingRepository.save(meeting);

        MeetingAiResponse aiResponseDto = new MeetingAiResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                markdownResponse
        );

        return new ResponseDto(200, "답변 성공", Map.of("aiResponse", aiResponseDto));
    }

    @Transactional(readOnly = true)
    public ResponseDto getMeetings(Long teamId, int page) {
        PageRequest pageable = PageRequest.of(page, 10);
        Page<Meeting> meetings = meetingRepository.findByTeamId(teamId, pageable);

        List<MeetingResponse> meetingResponses = meetings.getContent().stream()
                .map(MeetingResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> data = Map.of( //Page 객체를 활용해 페이지네이션 정보를 추가로 반환
                "meetings", meetingResponses,
                "totalPages", meetings.getTotalPages(),
                "currentPage", meetings.getNumber(),
                "totalElements", meetings.getTotalElements()
        );

        return new ResponseDto(200, "회의록 리스트", meetingResponses);
    }

    @Transactional
    public ResponseDto updateMeeting(Long meetingId, MeetingUpdateRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의록을 찾을 수 없습니다."));
        meeting.update(request.getMeeting(), request.getParticipant(), teamUserRepository);
        meetingRepository.save(meeting);
        return new ResponseDto(200, "회의록 수정 성공");
    }

    @Transactional
    public ResponseDto deleteMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의록을 찾을 수 없습니다."));
        meetingRepository.delete(meeting);
        return new ResponseDto(200, "회의록 삭제 성공");
    }
}