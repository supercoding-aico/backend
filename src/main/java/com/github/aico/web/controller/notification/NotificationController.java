package com.github.aico.web.controller.notification;

import com.github.aico.repository.user.JwtUser;
import com.github.aico.repository.user.User;
import com.github.aico.service.notification.NotificationService;
import com.github.aico.web.dto.base.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseDto getUnreadNotifications(@JwtUser User user) {
        return notificationService.getUnreadNotifications(user);
    }

    @PutMapping("/read")
    public ResponseDto markNotificationsAsRead(@JwtUser User user,
                                               @RequestParam("notification-id") List<Long> notificationIds) {
        return notificationService.markNotificationsAsRead(user, notificationIds);
    }
}