package com.github.aico.repository.meeting_user;

import com.github.aico.repository.meeting.Meeting;
import com.github.aico.repository.team_user.TeamUser;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "meetingUserId")
@Builder
@Entity
@Table(name = "meeting_user")
public class MeetingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_user_id")
    private Long meetingUserId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;
}
