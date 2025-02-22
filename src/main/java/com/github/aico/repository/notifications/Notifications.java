package com.github.aico.repository.notifications;

import com.github.aico.repository.base.BaseEntity;
import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.team_user.TeamUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "notificationsId")
@Builder
@Entity
@Table(name = "notifications")
public class Notifications extends BaseEntity {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notifications_id")
    private Long notificationsId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_user_id",nullable = false)
    private TeamUser teamUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id",nullable = false)
    private Schedule schedule;
    @Column(name = "content",length = 255,nullable = false)
    private String content;
    @Column(name = "is_read",nullable = false)
    private Boolean isRead;

}
