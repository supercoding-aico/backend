package com.github.aico.service.notification;

import com.github.aico.repository.notifications.Notifications;
import com.github.aico.repository.notifications.NotificationsRepository;
import com.github.aico.repository.schedule.Schedule;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationsRepository notificationsRepository;
    private final TeamUserRepository teamUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public ResponseDto getUnreadNotifications(User user) {
        List<TeamUser> teamUsers = teamUserRepository.findAllByUser(user, null).getContent();
        List<Notifications> unreadNotifications = notificationsRepository.findAllByTeamUserInAndIsReadFalse(teamUsers);
        List<NotificationResponse> response = unreadNotifications.stream()
                .map(notif -> new NotificationResponse(
                        notif.getNotificationsId(),
                        "알림",
                        notif.getContent(),
                        notif.getSchedule().getScheduleId(), // Schedule에서 가져옴
                        notif.getTeamUser().getTeam().getTeamId(), // TeamUser -> Team에서 가져옴
                        notif.getTeamUser().getUser().getNickname())) // 등록자 정보는 호출 시점에서 별도 전달 필요
                .collect(Collectors.toList());
        return new ResponseDto(HttpStatus.OK.value(), "안 읽은 알림 리스트 조회 성공", response);
    }

    @Transactional
    public ResponseDto markNotificationsAsRead(User user, List<Long> notificationIds) {
        int updatedCount = notificationsRepository.markAsReadByIdsAndUser(notificationIds, user);
        if (updatedCount == 0) {
            return new ResponseDto(HttpStatus.OK.value(), "읽음 처리할 알림이 없습니다.");
        }
        return new ResponseDto(HttpStatus.OK.value(), "알림 읽음 처리 성공 (" + updatedCount + "개)");
    }

    @Transactional
    public void sendScheduleNotification(Schedule schedule, String content, User registeredBy, List<TeamUser> teamUsers) {
        for (TeamUser teamUser : teamUsers) {
            Notifications notification = Notifications.builder()
                    .teamUser(teamUser)
                    .schedule(schedule)
                    .content(content)
                    .isRead(false)
                    .build();

            notificationsRepository.save(notification);

            NotificationResponse response = new NotificationResponse(
                    notification.getNotificationsId(),
                    "알림",
                    content,
                    schedule.getScheduleId(),
                    teamUser.getTeam().getTeamId(),
                    registeredBy.getNickname() // 등록자 정보는 User 객체에서 가져옴
            );
            messagingTemplate.convertAndSend("/topic/notification/" + teamUser.getUser().getUserId(), response);
        }
    }
}