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
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.meeting.request.MeetingAiRequest;
import com.github.aico.web.dto.meeting.request.MeetingUpdateRequest;
import com.github.aico.web.dto.meeting.response.MeetingAiResponse;
import com.github.aico.web.dto.meeting.response.MeetingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MeetingService {
    private final MeetingRepository meetingRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final OpenAiClient openAiClient;

    public MeetingService(MeetingRepository meetingRepository,
                          TeamUserRepository teamUserRepository,
                          TeamRepository teamRepository,
                          UserRepository userRepository,
                          OpenAiClient openAiClient) {
        this.meetingRepository = meetingRepository;
        this.teamUserRepository = teamUserRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.openAiClient = openAiClient;
    }

    @Transactional
    public ResponseDto requestAiSummary(Long teamId, List<MeetingAiRequest> requestList, User user) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found for teamId: " + teamId));

        String combinedContent = requestList.stream()
                .map(req -> String.format("**%s**: %s", req.getNickname(), req.getContent()))
                .collect(Collectors.joining("\n"));

        String aiResponse;
        try {
            aiResponse = openAiClient.getAiSummary(combinedContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get AI summary: " + e.getMessage(), e);
        }
        String markdownResponse = String.format("### 회의록 요약\n%s", aiResponse);

        Meeting meeting = Meeting.builder()
                .team(team)
                .content(markdownResponse)
                .participants(new ArrayList<>())
                .build();

        if (requestList != null) {
            List<MeetingUser> participants = requestList.stream()
                    .map(req -> {
                        User participantUser = userRepository.findById(req.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("User not found for userId: " + req.getUserId()));
                        TeamUser teamUser = teamUserRepository.findByTeamAndUser(team, participantUser)
                                .orElseThrow(() -> new IllegalArgumentException("TeamUser not found for userId: " + req.getUserId() + " in teamId: " + teamId));
                        return MeetingUser.builder()
                                .meeting(meeting)
                                .teamUser(teamUser)
                                .build();
                    })
                    .collect(Collectors.toList());
            meeting.getParticipants().addAll(participants);
        }

        meetingRepository.save(meeting);

        MeetingAiResponse aiResponseDto = new MeetingAiResponse(
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                markdownResponse
        );

        // 생성자로 ResponseDto 반환
        return new ResponseDto(200, "답변 성공", Map.of("aiResponse", aiResponseDto));
    }

    @Transactional(readOnly = true)
    public ResponseDto getMeetings(Long teamId, int page) {
        PageRequest pageable = PageRequest.of(page, 10);
        Page<Meeting> meetings = meetingRepository.findByTeamId(teamId, pageable);

        List<MeetingResponse> meetingResponses = meetings.getContent().stream()
                .map(MeetingResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> data = Map.of( //Page 객체를 활용해 페이지네이션 정보를 추가로 반환?
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