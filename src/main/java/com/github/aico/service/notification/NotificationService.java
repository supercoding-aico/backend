package com.github.aico.service.notification;

import com.github.aico.repository.notifications.Notifications;
import com.github.aico.repository.notifications.NotificationsRepository;
import com.github.aico.repository.team_user.TeamUser;
import com.github.aico.repository.team_user.TeamUserRepository;
import com.github.aico.repository.user.User;
import com.github.aico.web.dto.base.ResponseDto;
import com.github.aico.web.dto.notification.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationsRepository notificationsRepository;
    private final TeamUserRepository teamUserRepository;

    @Transactional(readOnly = true)
    public ResponseDto getUnreadNotifications(User user) {
        List<TeamUser> teamUsers = teamUserRepository.findAllByUser(user, null).getContent();
        List<Notifications> unreadNotifications = notificationsRepository.findAllByTeamUserInAndIsReadFalse(teamUsers);
        List<NotificationResponse> response = unreadNotifications.stream()
                .map(notif -> new NotificationResponse(
                        notif.getNotificationsId(),
                        "알림",
                        notif.getContent()))
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
}