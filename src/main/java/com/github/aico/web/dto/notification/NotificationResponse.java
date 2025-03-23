package com.github.aico.web.dto.notification;

import lombok.Getter;

@Getter
public class NotificationResponse {
    private final Long notificationId;
    private final String type; // "스케줄 등록", "스케줄 수정" 등
    private final String content;
    private Long scheduleId;
    private Long teamId;
    private String registeredBy;

    public NotificationResponse(Long notificationId, String type, String content, Long scheduleId, Long teamId, String registeredBy) {
        this.notificationId = notificationId;
        this.type = type;
        this.content = content;
        this.scheduleId = scheduleId;
        this.teamId = teamId;
        this.registeredBy = registeredBy;
    }
}