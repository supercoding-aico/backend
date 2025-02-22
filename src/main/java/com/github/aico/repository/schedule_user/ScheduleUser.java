package com.github.aico.repository.schedule_user;

import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.team_user.TeamUser;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "scheduleUserId")
@Builder
@Entity
@Table(name = "schedule_user")
public class ScheduleUser {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_user_id")
    private Long scheduleUserId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id")
    private TeamUser teamUser;
}
