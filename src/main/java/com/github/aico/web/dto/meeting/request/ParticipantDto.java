package com.github.aico.web.dto.meeting.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
    private Long userId;
    private String nickname;
}
