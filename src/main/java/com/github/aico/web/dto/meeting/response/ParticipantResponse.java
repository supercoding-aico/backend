package com.github.aico.web.dto.meeting.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor

class ParticipantResponse {
    private Long userId;
    private String nickname;
}
