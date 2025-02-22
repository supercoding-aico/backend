package com.github.aico.repository.meeting;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.team.Team;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "meetingId")
@Builder
@Entity
@Table(name = "meeting")
public class Meeting extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id",nullable = false)
    private Team team;
    @Column(name = "content" , length = 255,nullable = false)
    private String content;
}
