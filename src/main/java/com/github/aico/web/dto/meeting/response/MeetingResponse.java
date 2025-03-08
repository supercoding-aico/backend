package com.github.aico.web.dto.meeting.response;

import com.github.aico.repository.meeting.Meeting;
import com.github.aico.repository.meeting_user.MeetingUser;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MeetingResponse {
    private Long meetingId;
    private String meeting;
    private LocalDateTime date;
    private List<ParticipantResponse> participants;

    public MeetingResponse(Meeting meeting) {
        this.meetingId = meeting.getMeetingId();
        this.meeting = meeting.getContent();
        this.date = meeting.getCreatedAt();
        this.participants = meeting.getParticipants().stream()
                .map(mu -> new ParticipantResponse(
                        mu.getTeamUser().getUser().getId(),      // User에서 ID 가져옴
                        mu.getTeamUser().getUser().getNickname() // User에서 nickname 가져옴
                ))
                .collect(Collectors.toList());
    }
}