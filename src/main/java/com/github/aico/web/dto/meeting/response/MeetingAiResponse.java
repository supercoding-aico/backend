package com.github.aico.web.dto.meeting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingAiResponse {
    private String createdAt;
    private String contentResponse;
}