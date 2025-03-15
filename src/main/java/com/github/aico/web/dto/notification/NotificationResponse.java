package com.github.aico.web.dto.notification;

import lombok.Getter;

@Getter
public class NotificationResponse {
    private final Long notificationId;
    private final String type; // "스케줄 등록", "스케줄 수정" 등
    private final String content;

    public NotificationResponse(Long notificationId, String type, String content) {
        this.notificationId = notificationId;
        this.type = type;
        this.content = content;
    }
}