package com.github.aico.repository.schedule;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.team.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "scheduleId")
@Builder
@Entity
@Table(name = "schedule")
public class Schedule extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @Enumerated(EnumType.STRING) // Enum 타입으로 저장
    @Column(name = "schedule_status", nullable = false)
    private ScheduleStatus scheduleStatus;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
}
