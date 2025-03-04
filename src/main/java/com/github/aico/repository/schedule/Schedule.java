package com.github.aico.repository.schedule;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.schedule_user.ScheduleUser;
import com.github.aico.repository.team.Team;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.user.User;
import com.github.aico.web.dto.schedule.request.ScheduleRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "scheduleId")
@Builder
@Entity
@Table(name = "schedule")
public class Schedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @Column(name = "content", nullable = false)
    private String content;
    @Enumerated(EnumType.STRING) // Enum 타입으로 저장
    @Column(name = "schedule_status", nullable = false)
    private ScheduleStatus scheduleStatus;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleUser> scheduleUsers = new ArrayList<>();


    public void update(ScheduleRequest request, List<TeamUser> teamUsers) {
        this.content = request.getContent();
        this.scheduleStatus = request.getScheduleStatus();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();

        this.scheduleUsers.clear();

        List<ScheduleUser> newScheduleUsers = teamUsers.stream()
                .map(teamUser -> new ScheduleUser(null, this, teamUser))
                .collect(Collectors.toList());
        this.scheduleUsers.addAll(newScheduleUsers);
    }


    public static Schedule of(ScheduleRequest request, Long teamId, List<TeamUser> teamUsers) {
        if (teamUsers == null) {
            teamUsers = new ArrayList<>();
        }

        Schedule schedule = Schedule.builder()
                .team(new Team(teamId))
                .content(request.getContent())
                .scheduleStatus(request.getScheduleStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        if (teamUsers.isEmpty()) {
            return schedule;
        }

        List<ScheduleUser> scheduleUsers = teamUsers.stream()
                .map(teamUser -> new ScheduleUser(null, schedule, teamUser))
                .collect(Collectors.toList());

        schedule.scheduleUsers.addAll(scheduleUsers);

        return schedule;
    }


}