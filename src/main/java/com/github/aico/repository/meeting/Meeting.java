package com.github.aico.repository.meeting;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.meeting_user.MeetingUser;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.web.dto.meeting.request.ParticipantDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "meetingId")
@Builder
@Entity
@Table(name = "meeting")
public class Meeting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetingUser> participants = new ArrayList<>();

    public void update(String content, List<ParticipantDto> participants, TeamUserRepository teamUserRepository) {
        this.content = content;
        this.participants.clear(); // 기존 참가자 삭제
        if (participants != null && !participants.isEmpty()) {
            this.participants.addAll(participants.stream()
                    .map(dto -> {
                        // team_id와 user_id로 TeamUser 조회
                        TeamUser teamUser = teamUserRepository.findByTeamTeamIdAndUserId(team.getTeamId(), dto.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("TeamUser not found for teamId: " + team.getTeamId() + ", userId: " + dto.getUserId()));
                        return MeetingUser.builder()
                                .meeting(this)
                                .teamUser(teamUser)
                                .build();
                    })
                    .collect(Collectors.toList()));
        }
    }

    public Long getMeetingId() { return meetingId; }
    public String getContent() { return content; }
    public List<MeetingUser> getParticipants() { return participants; }
}